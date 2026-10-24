<script lang="ts">
  import Modal from '../common/Modal.svelte'
  import Button from '../common/Button.svelte'
  import TextInput from '../common/TextInput.svelte'
  import { movieStore } from '../../stores/movieStore'
  import { peopleStore } from '../../stores/peopleStore'
  import type { CreateMovieInput } from '../../types/movie'
  import Badge from '../common/Badge.svelte'
  import { Check } from '@lucide/svelte'

  interface Props {
    open: boolean
    onclose: () => void
  }

  let { open = false, onclose }: Props = $props()

  let title = $state('')
  let releaseYear = $state<number | undefined>(new Date().getFullYear())
  let artworkUrl = $state('')
  let selectedPplIds = $state<string[]>([])
  let submitting = $state(false)
  let titleError = $state('')

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
  }

  async function handleSubmit(e: SubmitEvent) {
    e.preventDefault()

    if (!title.trim()) {
      titleError = 'Title is required'
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
</script>

<Modal {open} title="Add New Movie" onclose={() => { resetForm(); onclose(); }}>
  <form id="movie-form" onsubmit={handleSubmit} class="form-layout">
    <TextInput
      id="movie-title"
      label="Movie Title"
      placeholder="e.g. Inception, The Dark Knight"
      required
      bind:value={title}
      error={titleError}
      oninput={() => titleError = ''}
    />

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

      <div class="row-item flex-2">
        <TextInput
          id="artwork-url"
          label="Artwork / Poster Image URL"
          type="url"
          placeholder="https://..."
          bind:value={artworkUrl}
        />
      </div>
    </div>

    {#if artworkUrl}
      <div class="artwork-preview">
        <span>Artwork Preview:</span>
        <img src={artworkUrl} alt="Preview" class="preview-img" onerror={() => {}} />
      </div>
    {/if}

    <div class="form-group">
      <label for="people-select">Associate Cast & Creators ({selectedPplIds.length} selected)</label>
      <div id="people-select" class="people-selector">
        {#if $peopleStore.items.length === 0}
          <div class="no-people">No people in directory yet. Add them in People Directory.</div>
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

  .row-item {
    flex: 1;
  }

  .flex-2 {
    flex: 2;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
    flex: 1;
  }

  label {
    font-size: 0.8125rem;
    font-weight: 600;
    color: var(--text-primary);
  }

  .artwork-preview {
    display: flex;
    flex-direction: column;
    gap: 6px;
    font-size: 0.75rem;
    color: var(--text-muted);
  }

  .preview-img {
    width: 100%;
    height: 120px;
    object-fit: cover;
    border-radius: var(--radius-md);
    border: 1px solid var(--border-subtle);
  }

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
</style>
