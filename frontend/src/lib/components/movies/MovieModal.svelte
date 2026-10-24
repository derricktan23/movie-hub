<script lang="ts">
  import Modal from '../common/Modal.svelte'
  import Button from '../common/Button.svelte'
  import TextInput from '../common/TextInput.svelte'
  import { movieStore } from '../../stores/movieStore'
  import { peopleStore } from '../../stores/peopleStore'
  import { tmdbSearchStore } from '../../stores/tmdbSearchStore'
  import { fetchTmdbMovieCreditsApi } from '../../api/movieService'
  import type { CreateMovieInput, TmdbMovieSearchResult, TmdbCreditsResult, TmdbCreditPerson } from '../../types/movie'
  import Badge from '../common/Badge.svelte'
  import { Check, Search, Film, X, Sparkles, UserPlus, UserCheck } from '@lucide/svelte'

  interface Props {
    open: boolean
    onclose: () => void
  }

  let { open = false, onclose }: Props = $props()

  // Form state
  let title = $state('')
  let releaseYear = $state<number | undefined>(new Date().getFullYear())
  let artworkUrl = $state('')
  let selectedPplIds = $state<string[]>([])
  let submitting = $state(false)
  let titleError = $state('')

  // Whether artwork was picked from TMDb (shows TMDb attribution badge)
  let tmdbSelected = $state(false)

  // TMDb likely cast and creators
  let tmdbCredits = $state<TmdbCreditsResult | null>(null)
  let loadingCredits = $state(false)
  let autoLinking = $state(false)

  // Search input element ref for focus management
  let searchInputEl: HTMLInputElement | undefined = $state()
  let dropdownRef: HTMLDivElement | undefined = $state()

  function handleSearchInput(e: Event) {
    const val = (e.target as HTMLInputElement).value
    title = val
    titleError = ''
    tmdbSelected = false
    artworkUrl = ''
    releaseYear = undefined
    tmdbCredits = null
    loadingCredits = false
    tmdbSearchStore.search(val)
  }

  function autoMatchCredits(credits: TmdbCreditsResult) {
    const allCreditPeople = [...credits.crew, ...credits.cast]
    const matchedIds: string[] = []

    for (const credit of allCreditPeople) {
      const existing = $peopleStore.items.find(
        (p) => p.name.trim().toLowerCase() === credit.name.trim().toLowerCase()
      )
      if (existing && !selectedPplIds.includes(existing.id)) {
        matchedIds.push(existing.id)
      }
    }

    if (matchedIds.length > 0) {
      selectedPplIds = Array.from(new Set([...selectedPplIds, ...matchedIds]))
    }
  }

  async function selectSuggestion(result: TmdbMovieSearchResult) {
    title = result.title
    releaseYear = result.releaseYear ?? undefined
    artworkUrl = result.posterUrl ?? ''
    tmdbSelected = true
    tmdbSearchStore.select()

    // Fetch likely cast and key creators from TMDb
    loadingCredits = true
    tmdbCredits = null
    try {
      const credits = await fetchTmdbMovieCreditsApi(result.tmdbId)
      tmdbCredits = credits
      autoMatchCredits(credits)
    } catch (err) {
      console.error('Failed to load TMDb credits:', err)
    } finally {
      loadingCredits = false
    }
  }

  function isPersonLinked(credit: TmdbCreditPerson): boolean {
    const existing = $peopleStore.items.find(
      (p) => p.name.trim().toLowerCase() === credit.name.trim().toLowerCase()
    )
    return Boolean(existing && selectedPplIds.includes(existing.id))
  }

  async function linkCreditPerson(credit: TmdbCreditPerson) {
    const existing = $peopleStore.items.find(
      (p) => p.name.trim().toLowerCase() === credit.name.trim().toLowerCase()
    )

    if (existing) {
      togglePerson(existing.id)
    } else {
      // Create in people registry via ppl-service and auto-select
      const created = await peopleStore.create({
        name: credit.name,
        role: credit.role
      })
      if (created) {
        selectedPplIds = [...selectedPplIds, created.id]
      }
    }
  }

  async function linkAllLikelyCredits() {
    if (!tmdbCredits) return
    autoLinking = true
    const allCredits = [...tmdbCredits.crew, ...tmdbCredits.cast]
    const seen = new Set<string>()
    const uniqueCredits: TmdbCreditPerson[] = []

    for (const c of allCredits) {
      const key = c.name.trim().toLowerCase()
      if (!seen.has(key)) {
        seen.add(key)
        uniqueCredits.push(c)
      }
    }

    const idsToAdd: string[] = []

    for (const credit of uniqueCredits) {
      const existing = $peopleStore.items.find(
        (p) => p.name.trim().toLowerCase() === credit.name.trim().toLowerCase()
      )
      if (!existing) {
        const created = await peopleStore.create({
          name: credit.name,
          role: credit.role
        })
        if (created) {
          idsToAdd.push(created.id)
        }
      } else if (!selectedPplIds.includes(existing.id)) {
        idsToAdd.push(existing.id)
      }
    }

    if (idsToAdd.length > 0) {
      selectedPplIds = Array.from(new Set([...selectedPplIds, ...idsToAdd]))
    }
    autoLinking = false
  }

  function clearSelection() {
    title = ''
    releaseYear = undefined
    artworkUrl = ''
    tmdbSelected = false
    tmdbCredits = null
    loadingCredits = false
    autoLinking = false
    tmdbSearchStore.clear()
    searchInputEl?.focus()
  }

  function togglePerson(id: string) {
    if (selectedPplIds.includes(id)) {
      selectedPplIds = selectedPplIds.filter((pId) => pId !== id)
    } else {
      selectedPplIds = [...selectedPplIds, id]
    }
  }

  function resetForm() {
    title = ''
    releaseYear = new Date().getFullYear()
    artworkUrl = ''
    selectedPplIds = []
    titleError = ''
    tmdbSelected = false
    tmdbCredits = null
    loadingCredits = false
    autoLinking = false
    tmdbSearchStore.clear()
  }

  async function handleSubmit(e: SubmitEvent) {
    e.preventDefault()

    if (!title.trim()) {
      titleError = 'Movie title is required'
      return
    }

    submitting = true
    const input: CreateMovieInput = {
      title: title.trim(),
      releaseYear: releaseYear ? Number(releaseYear) : undefined,
      artworkUrl: artworkUrl.trim() || undefined,
      pplIds: selectedPplIds
    }

    const created = await movieStore.create(input)
    submitting = false

    if (created) {
      resetForm()
      onclose()
    }
  }

  // Close dropdown on click-outside
  function handleWindowClick(e: MouseEvent) {
    if (
      $tmdbSearchStore.open &&
      dropdownRef &&
      !dropdownRef.contains(e.target as Node)
    ) {
      tmdbSearchStore.closeDropdown()
    }
  }
</script>

<svelte:window onclick={handleWindowClick} />

<Modal {open} title="Add New Movie" onclose={() => { resetForm(); onclose(); }}>
  <form id="movie-form" onsubmit={handleSubmit} class="form-layout">

    <!-- ── TMDb Search Field ── -->
    <div class="form-group">
      <label for="movie-search">
        Movie Title
        <span class="required-star">*</span>
        <span class="tmdb-hint">— powered by TMDb</span>
      </label>

      <div class="search-wrapper" bind:this={dropdownRef}>
        <!-- Search input with spinner -->
        <div
          class="search-input-row"
          role="combobox"
          aria-expanded={$tmdbSearchStore.open}
          aria-haspopup="listbox"
          aria-owns="tmdb-dropdown"
          aria-controls="tmdb-dropdown"
        >
          <span class="search-icon">
            {#if $tmdbSearchStore.loading}
              <span class="spinner"></span>
            {:else}
              <Search size={15} color="var(--text-muted)" />
            {/if}
          </span>

          <input
            id="movie-search"
            type="text"
            bind:this={searchInputEl}
            value={title}
            oninput={handleSearchInput}
            placeholder="Search TMDb e.g. Inception, Parasite..."
            class="search-input {titleError ? 'has-error' : ''}"
            autocomplete="off"
            aria-autocomplete="list"
            aria-controls="tmdb-dropdown"
          />

          {#if title}
            <button
              type="button"
              class="clear-btn"
              onclick={clearSelection}
              aria-label="Clear"
            >
              <X size={14} color="var(--text-muted)" />
            </button>
          {/if}
        </div>

        {#if titleError}
          <span class="error-msg">{titleError}</span>
        {/if}

        <!-- ── Autocomplete Dropdown ── -->
        {#if $tmdbSearchStore.open || ($tmdbSearchStore.loading && title.length > 0)}
          <div id="tmdb-dropdown" class="dropdown" role="listbox" aria-label="TMDb movie suggestions">

            {#if $tmdbSearchStore.loading && $tmdbSearchStore.results.length === 0}
              <!-- Skeleton rows while loading -->
              {#each [1, 2, 3] as _}
                <div class="skeleton-row">
                  <div class="skeleton-poster"></div>
                  <div class="skeleton-text">
                    <div class="skeleton-line wide"></div>
                    <div class="skeleton-line narrow"></div>
                  </div>
                </div>
              {/each}

            {:else if $tmdbSearchStore.results.length === 0 && !$tmdbSearchStore.loading}
              <div class="no-results">
                <Film size={20} color="var(--text-muted)" />
                <span>No results for "<strong>{title}</strong>" — you can still submit the title manually.</span>
              </div>

            {:else}
              {#each $tmdbSearchStore.results as result (result.tmdbId)}
                <button
                  type="button"
                  class="suggestion-row"
                  onclick={() => selectSuggestion(result)}
                  role="option"
                  aria-selected="false"
                >
                  <!-- Poster thumbnail -->
                  {#if result.posterUrl}
                    <img
                      src={result.posterUrl}
                      alt="{result.title} poster"
                      class="suggestion-poster"
                      loading="lazy"
                    />
                  {:else}
                    <div class="suggestion-poster no-poster">
                      <Film size={16} color="var(--text-muted)" />
                    </div>
                  {/if}

                  <div class="suggestion-meta">
                    <span class="suggestion-title">{result.title}</span>
                    {#if result.releaseYear}
                      <span class="suggestion-year">{result.releaseYear}</span>
                    {/if}
                  </div>
                </button>
              {/each}
            {/if}

          </div>
        {/if}
      </div>
    </div>

    <!-- ── Auto-filled poster preview & metadata ── -->
    {#if tmdbSelected}
      <div class="selection-card">
        {#if artworkUrl}
          <img src={artworkUrl} alt="{title} poster" class="selection-poster" />
        {:else}
          <div class="selection-poster no-poster">
            <Film size={24} color="var(--text-muted)" />
          </div>
        {/if}
        <div class="selection-info">
          <p class="selection-title">{title}</p>
          {#if releaseYear}
            <p class="selection-year">{releaseYear}</p>
          {/if}
          <span class="tmdb-badge">
            <svg width="12" height="12" viewBox="0 0 185.51 133.19" fill="none" aria-hidden="true">
              <path d="M5.83 0h25.96l28.32 80.1L88.43 0h25.38L77.03 133.19H52.39L5.83 0z" fill="#01B4E4"/>
              <path d="M104.04 0h65.73c10.1 0 15.74 5.64 15.74 15.74v101.7c0 10.1-5.64 15.74-15.74 15.74h-65.73V0zm25.54 22.96v87.27h35.92V22.96h-35.92z" fill="#01B4E4"/>
            </svg>
            Artwork provided by TMDb
          </span>
        </div>
      </div>

      <!-- ── Likely Cast & Creators from TMDb ── -->
      <div class="tmdb-credits-section">
        <div class="credits-header">
          <div class="credits-title-row">
            <Sparkles size={15} color="var(--primary-color)" />
            <span class="credits-title">Likely Cast & Creators from TMDb</span>
            {#if loadingCredits}
              <span class="credits-spinner"></span>
            {/if}
          </div>
          {#if tmdbCredits && (tmdbCredits.cast.length > 0 || tmdbCredits.crew.length > 0)}
            <button
              type="button"
              class="link-all-btn"
              onclick={linkAllLikelyCredits}
              disabled={autoLinking}
            >
              <Sparkles size={12} />
              <span>{autoLinking ? 'Linking...' : '⚡ Auto-Link All'}</span>
            </button>
          {/if}
        </div>

        {#if loadingCredits}
          <div class="credits-loading-skeleton">
            {#each [1, 2, 3, 4] as _}
              <div class="credit-skeleton-chip"></div>
            {/each}
          </div>
        {:else if tmdbCredits}
          <!-- Key Creators -->
          {#if tmdbCredits.crew.length > 0}
            <div class="credit-group">
              <span class="group-label">Key Creators</span>
              <div class="credit-chips-grid">
                {#each tmdbCredits.crew as member (member.id + member.characterOrJob)}
                  {@const linked = isPersonLinked(member)}
                  <button
                    type="button"
                    class="credit-chip {linked ? 'is-linked' : ''}"
                    onclick={() => linkCreditPerson(member)}
                    title="{linked ? 'Linked! Click to unlink' : 'Click to add & link to movie'}"
                  >
                    {#if member.profileUrl}
                      <img src={member.profileUrl} alt={member.name} class="credit-avatar" />
                    {:else}
                      <div class="credit-avatar-placeholder">{member.name.charAt(0)}</div>
                    {/if}
                    <div class="credit-chip-info">
                      <span class="credit-chip-name">{member.name}</span>
                      <span class="credit-chip-meta">{member.characterOrJob || member.role}</span>
                    </div>
                    <div class="credit-chip-action">
                      {#if linked}
                        <UserCheck size={14} color="#16A34A" />
                      {:else}
                        <UserPlus size={14} color="var(--primary-color)" />
                      {/if}
                    </div>
                  </button>
                {/each}
              </div>
            </div>
          {/if}

          <!-- Top Cast -->
          {#if tmdbCredits.cast.length > 0}
            <div class="credit-group">
              <span class="group-label">Top Cast</span>
              <div class="credit-chips-grid">
                {#each tmdbCredits.cast as member (member.id)}
                  {@const linked = isPersonLinked(member)}
                  <button
                    type="button"
                    class="credit-chip {linked ? 'is-linked' : ''}"
                    onclick={() => linkCreditPerson(member)}
                    title="{linked ? 'Linked! Click to unlink' : 'Click to add & link to movie'}"
                  >
                    {#if member.profileUrl}
                      <img src={member.profileUrl} alt={member.name} class="credit-avatar" />
                    {:else}
                      <div class="credit-avatar-placeholder">{member.name.charAt(0)}</div>
                    {/if}
                    <div class="credit-chip-info">
                      <span class="credit-chip-name">{member.name}</span>
                      <span class="credit-chip-meta">{member.characterOrJob ? `as ${member.characterOrJob}` : member.role}</span>
                    </div>
                    <div class="credit-chip-action">
                      {#if linked}
                        <UserCheck size={14} color="#16A34A" />
                      {:else}
                        <UserPlus size={14} color="var(--primary-color)" />
                      {/if}
                    </div>
                  </button>
                {/each}
              </div>
            </div>
          {/if}
        {/if}
      </div>

    {:else if title && !tmdbSelected}
      <!-- Manual title entered — show year field only -->
      <div class="form-row">
        <div class="row-item">
          <TextInput
            id="release-year"
            label="Release Year"
            type="number"
            min={1900}
            max={2099}
            placeholder="e.g. 2024"
            bind:value={releaseYear}
          />
        </div>
      </div>
    {/if}

    <!-- ── Cast & Creators selector ── -->
    <div class="form-group">
      <label for="people-select">
        Associate Cast & Creators
        {#if selectedPplIds.length > 0}
          <span class="count-badge">{selectedPplIds.length} selected</span>
        {/if}
      </label>
      <div id="people-select" class="people-selector">
        {#if $peopleStore.items.length === 0}
          <div class="no-people">No people in directory yet. Add them in the People tab.</div>
        {:else}
          {#each $peopleStore.items as person (person.id)}
            {@const isSelected = selectedPplIds.includes(person.id)}
            <button
              type="button"
              class="person-toggle {isSelected ? 'selected' : ''}"
              onclick={() => togglePerson(person.id)}
            >
              <div class="checkbox-indicator">
                {#if isSelected}
                  <Check size={12} color="#ffffff" strokeWidth={3} />
                {/if}
              </div>
              <span class="name">{person.name}</span>
              <Badge role={person.role} />
            </button>
          {/each}
        {/if}
      </div>
    </div>

  </form>

  {#snippet footer()}
    <Button variant="ghost" onclick={() => { resetForm(); onclose(); }} disabled={submitting}>
      Cancel
    </Button>
    <Button variant="primary" type="submit" form="movie-form" loading={submitting}>
      Create Movie
    </Button>
  {/snippet}
</Modal>

<style>
  .form-layout {
    display: flex;
    flex-direction: column;
    gap: 18px;
  }

  .form-row {
    display: flex;
    gap: 16px;
  }

  .row-item { flex: 1; }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  label {
    font-size: 0.8125rem;
    font-weight: 600;
    color: var(--text-primary);
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .required-star {
    color: var(--danger);
  }

  .tmdb-hint {
    font-size: 0.75rem;
    font-weight: 400;
    color: var(--text-muted);
  }

  /* ── Search field ── */
  .search-wrapper {
    position: relative;
  }

  .search-input-row {
    position: relative;
    display: flex;
    align-items: center;
  }

  .search-icon {
    position: absolute;
    left: 12px;
    display: flex;
    align-items: center;
    pointer-events: none;
    z-index: 1;
  }

  .search-input {
    width: 100%;
    padding: 10px 36px 10px 38px;
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-md);
    color: var(--text-primary);
    font-size: 0.875rem;
    font-family: inherit;
    outline: none;
    transition: all var(--transition-fast);
  }

  .search-input:focus {
    border-color: var(--border-focus);
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
  }

  .search-input.has-error {
    border-color: var(--danger);
  }

  .clear-btn {
    position: absolute;
    right: 10px;
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px;
    display: flex;
    align-items: center;
    border-radius: var(--radius-sm);
    transition: background var(--transition-fast);
  }

  .clear-btn:hover {
    background: var(--bg-secondary);
  }

  .error-msg {
    font-size: 0.75rem;
    color: var(--danger);
  }

  /* ── Spinner ── */
  .spinner {
    width: 15px;
    height: 15px;
    border: 2px solid var(--border-subtle);
    border-top-color: var(--accent-primary);
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
    display: inline-block;
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  /* ── Dropdown ── */
  .dropdown {
    position: absolute;
    top: calc(100% + 4px);
    left: 0;
    right: 0;
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-lg);
    z-index: 100;
    max-height: 280px;
    overflow-y: auto;
    animation: fadeIn 150ms ease;
  }

  @keyframes fadeIn {
    from { opacity: 0; transform: translateY(-6px); }
    to   { opacity: 1; transform: translateY(0); }
  }

  .suggestion-row {
    width: 100%;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 8px 12px;
    border: none;
    background: none;
    cursor: pointer;
    text-align: left;
    font-family: inherit;
    transition: background var(--transition-fast);
    border-bottom: 1px solid var(--border-subtle);
  }

  .suggestion-row:last-child {
    border-bottom: none;
  }

  .suggestion-row:hover {
    background: #EFF6FF;
  }

  .suggestion-poster {
    width: 36px;
    height: 54px;
    object-fit: cover;
    border-radius: 4px;
    flex-shrink: 0;
    background: var(--bg-secondary);
  }

  .suggestion-poster.no-poster {
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--bg-secondary);
  }

  .suggestion-meta {
    display: flex;
    flex-direction: column;
    gap: 3px;
    min-width: 0;
  }

  .suggestion-title {
    font-size: 0.875rem;
    font-weight: 600;
    color: var(--text-primary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .suggestion-year {
    font-size: 0.75rem;
    color: var(--text-muted);
  }

  /* ── Skeletons ── */
  .skeleton-row {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 8px 12px;
    border-bottom: 1px solid var(--border-subtle);
  }

  .skeleton-poster {
    width: 36px;
    height: 54px;
    border-radius: 4px;
    background: linear-gradient(90deg, #E2E8F0 25%, #F1F5F9 50%, #E2E8F0 75%);
    background-size: 200% 100%;
    animation: shimmer 1.2s infinite;
    flex-shrink: 0;
  }

  .skeleton-text {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .skeleton-line {
    height: 12px;
    border-radius: 4px;
    background: linear-gradient(90deg, #E2E8F0 25%, #F1F5F9 50%, #E2E8F0 75%);
    background-size: 200% 100%;
    animation: shimmer 1.2s infinite;
  }

  .skeleton-line.wide  { width: 70%; }
  .skeleton-line.narrow { width: 35%; }

  @keyframes shimmer {
    0%   { background-position: 200% 0; }
    100% { background-position: -200% 0; }
  }

  /* ── No results ── */
  .no-results {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 16px 12px;
    font-size: 0.8125rem;
    color: var(--text-muted);
  }

  .no-results strong {
    color: var(--text-primary);
  }

  /* ── Selected movie card ── */
  .selection-card {
    display: flex;
    gap: 14px;
    align-items: flex-start;
    background: #EFF6FF;
    border: 1px solid rgba(37, 99, 235, 0.2);
    border-radius: var(--radius-md);
    padding: 12px;
    animation: fadeIn 200ms ease;
  }

  .selection-poster {
    width: 56px;
    height: 84px;
    object-fit: cover;
    border-radius: var(--radius-sm);
    flex-shrink: 0;
    box-shadow: var(--shadow-sm);
  }

  .selection-info {
    display: flex;
    flex-direction: column;
    gap: 4px;
    min-width: 0;
    padding-top: 2px;
  }

  .selection-title {
    font-size: 0.9375rem;
    font-weight: 700;
    color: var(--text-primary);
    margin: 0;
  }

  .selection-year {
    font-size: 0.8125rem;
    color: var(--text-secondary);
    margin: 0;
  }

  .tmdb-badge {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    margin-top: 6px;
    font-size: 0.6875rem;
    font-weight: 600;
    color: #01B4E4;
    background: rgba(1, 180, 228, 0.1);
    border: 1px solid rgba(1, 180, 228, 0.25);
    border-radius: var(--radius-full);
    padding: 2px 8px;
    width: fit-content;
  }

  /* ── Count badge ── */
  .count-badge {
    font-size: 0.6875rem;
    font-weight: 600;
    background: var(--accent-primary);
    color: #fff;
    border-radius: var(--radius-full);
    padding: 1px 7px;
  }

  /* ── People selector ── */
  .people-selector {
    max-height: 180px;
    overflow-y: auto;
    padding: 8px;
    background: #F8FAFC;
    border: 1px solid var(--border-subtle);
    display: flex;
    flex-direction: column;
    gap: 6px;
    border-radius: var(--radius-md);
  }

  .no-people {
    padding: 12px;
    font-size: 0.8125rem;
    color: var(--text-muted);
    text-align: center;
  }

  .person-toggle {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 10px;
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-sm);
    color: var(--text-primary);
    cursor: pointer;
    font-family: inherit;
    text-align: left;
    transition: all var(--transition-fast);
  }

  .person-toggle:hover {
    background: #F1F5F9;
  }

  .person-toggle.selected {
    background: #EFF6FF;
    border-color: var(--accent-primary);
  }

  .checkbox-indicator {
    width: 18px;
    height: 18px;
    border-radius: 4px;
    border: 1px solid #CBD5E1;
    background: #FFFFFF;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .selected .checkbox-indicator {
    background: var(--accent-primary);
    border-color: var(--accent-primary);
  }

  .name {
    flex: 1;
    font-size: 0.875rem;
    font-weight: 500;
  }

  /* ── TMDb Credits Section ── */
  .tmdb-credits-section {
    background: #F8FAFC;
    border: 1px solid #E2E8F0;
    border-radius: var(--radius-md);
    padding: 14px;
    display: flex;
    flex-direction: column;
    gap: 12px;
    animation: fadeIn 200ms ease;
  }

  .credits-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    flex-wrap: wrap;
  }

  .credits-title-row {
    display: flex;
    align-items: center;
    gap: 7px;
  }

  .credits-title {
    font-size: 0.8125rem;
    font-weight: 700;
    color: var(--text-primary);
  }

  .credits-spinner {
    width: 13px;
    height: 13px;
    border: 2px solid #CBD5E1;
    border-top-color: var(--accent-primary);
    border-radius: 50%;
    animation: spin 0.6s linear infinite;
  }

  .link-all-btn {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    background: linear-gradient(135deg, #2563EB, #1D4ED8);
    color: #FFFFFF;
    font-size: 0.75rem;
    font-weight: 600;
    padding: 4px 10px;
    border-radius: var(--radius-sm);
    border: none;
    cursor: pointer;
    transition: all var(--transition-fast);
    box-shadow: 0 1px 2px rgba(37, 99, 235, 0.2);
  }

  .link-all-btn:hover:not(:disabled) {
    background: linear-gradient(135deg, #1D4ED8, #1E40AF);
    transform: translateY(-1px);
    box-shadow: 0 3px 8px rgba(37, 99, 235, 0.3);
  }

  .link-all-btn:disabled {
    opacity: 0.65;
    cursor: not-allowed;
  }

  .credits-loading-skeleton {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
    gap: 8px;
  }

  .credit-skeleton-chip {
    height: 48px;
    background: linear-gradient(90deg, #EDF2F7 25%, #E2E8F0 50%, #EDF2F7 75%);
    background-size: 200% 100%;
    animation: shimmer 1.2s infinite;
    border-radius: var(--radius-sm);
  }

  .credit-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .group-label {
    font-size: 0.6875rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: var(--text-secondary);
  }

  .credit-chips-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
    gap: 8px;
  }

  .credit-chip {
    display: flex;
    align-items: center;
    gap: 9px;
    padding: 6px 10px;
    background: #FFFFFF;
    border: 1px solid #E2E8F0;
    border-radius: var(--radius-sm);
    cursor: pointer;
    text-align: left;
    transition: all var(--transition-fast);
  }

  .credit-chip:hover {
    border-color: #93C5FD;
    background: #F0F9FF;
  }

  .credit-chip.is-linked {
    border-color: #86EFAC;
    background: #F0FDF4;
  }

  .credit-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
  }

  .credit-avatar-placeholder {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: #E2E8F0;
    color: #475569;
    font-size: 0.75rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .credit-chip-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
  }

  .credit-chip-name {
    font-size: 0.75rem;
    font-weight: 600;
    color: var(--text-primary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .credit-chip-meta {
    font-size: 0.6875rem;
    color: var(--text-secondary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .credit-chip-action {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
  }
</style>
