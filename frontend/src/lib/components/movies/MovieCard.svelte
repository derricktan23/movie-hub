<script lang="ts">
  import type { Movie } from '../../types/movie'
  import { Calendar, Trash2, Clapperboard, Users } from '@lucide/svelte'
  import Badge from '../common/Badge.svelte'
  import Avatar from '../common/Avatar.svelte'

  interface Props {
    movie: Movie
    onDelete: (movie: Movie) => void
  }

  let { movie, onDelete }: Props = $props()

  let imageError = $state(false)

  function handleImageError() {
    imageError = true
  }
</script>

<article class="movie-card glass-panel">
  <div class="artwork-wrapper">
    {#if movie.artworkUrl && !imageError}
      <img
        src={movie.artworkUrl}
        alt={movie.title}
        class="artwork-img"
        onerror={handleImageError}
        loading="lazy"
      />
    {:else}
      <div class="artwork-fallback">
        <Clapperboard size={36} color="#2563EB" />
        <span class="fallback-title">{movie.title}</span>
      </div>
    {/if}

    {#if movie.releaseYear}
      <div class="year-badge">
        <Calendar size={13} color="#2563EB" />
        <span>{movie.releaseYear}</span>
      </div>
    {/if}

    <button
      type="button"
      class="delete-action-btn"
      onclick={() => onDelete(movie)}
      aria-label="Delete movie"
    >
      <Trash2 size={16} />
    </button>
  </div>

  <div class="card-content">
    <h3 class="movie-title" title={movie.title}>{movie.title}</h3>

    <div class="cast-section">
      {#if movie.people && movie.people.length > 0}
        <div class="cast-label">
          <Users size={13} />
          <span>Associated Talent</span>
        </div>
        <div class="cast-chips">
          {#each movie.people.slice(0, 3) as person}
            <div class="talent-tag">
              <Avatar name={person.name} size="sm" />
              <span class="person-name">{person.name}</span>
              <Badge role={person.role} />
            </div>
          {/each}
          {#if movie.people.length > 3}
            <span class="more-count">+{movie.people.length - 3} more</span>
          {/if}
        </div>
      {:else if movie.pplIds && movie.pplIds.length > 0}
        <div class="cast-chips">
          <span class="person-id-chip">{movie.pplIds.length} person(s) associated</span>
        </div>
      {:else}
        <span class="no-cast">No associated people</span>
      {/if}
    </div>
  </div>
</article>

<style>
  .movie-card {
    display: flex;
    flex-direction: column;
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
    overflow: hidden;
    transition: transform var(--transition-normal), box-shadow var(--transition-normal), border-color var(--transition-normal);
  }

  .movie-card:hover {
    transform: translateY(-3px);
    box-shadow: var(--shadow-lg);
    border-color: #CBD5E1;
  }

  .artwork-wrapper {
    position: relative;
    width: 100%;
    aspect-ratio: 16 / 10;
    background: #F1F5F9;
    overflow: hidden;
  }

  .artwork-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 400ms ease;
  }

  .movie-card:hover .artwork-img {
    transform: scale(1.03);
  }

  .artwork-fallback {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    background: #F8FAFC;
    padding: 16px;
    text-align: center;
  }

  .fallback-title {
    font-size: 0.8125rem;
    font-weight: 600;
    color: var(--text-secondary);
  }

  .year-badge {
    position: absolute;
    bottom: 12px;
    left: 12px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(8px);
    border: 1px solid var(--border-subtle);
    padding: 4px 10px;
    border-radius: var(--radius-full);
    font-size: 0.75rem;
    font-weight: 700;
    color: var(--text-primary);
    display: flex;
    align-items: center;
    gap: 5px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  }

  .delete-action-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(8px);
    border: 1px solid var(--border-subtle);
    color: var(--text-secondary);
    cursor: pointer;
    padding: 7px;
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all var(--transition-fast);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  }

  .delete-action-btn:hover {
    color: #ffffff;
    background: var(--danger);
    border-color: var(--danger);
  }

  .card-content {
    padding: 18px;
    display: flex;
    flex-direction: column;
    flex: 1;
    gap: 12px;
  }

  .movie-title {
    font-size: 1.05rem;
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.3;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .cast-section {
    display: flex;
    flex-direction: column;
    gap: 6px;
    margin-top: auto;
  }

  .cast-label {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 0.75rem;
    font-weight: 600;
    color: var(--text-muted);
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .cast-chips {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
  }

  .talent-tag {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    background: #F8FAFC;
    padding: 2px 8px 2px 2px;
    border-radius: var(--radius-full);
    border: 1px solid var(--border-subtle);
  }

  .person-name {
    font-size: 0.8125rem;
    color: var(--text-secondary);
    font-weight: 500;
  }

  .more-count {
    font-size: 0.75rem;
    color: var(--accent-primary);
    font-weight: 600;
  }

  .person-id-chip {
    font-size: 0.75rem;
    color: var(--text-muted);
  }

  .no-cast {
    font-size: 0.75rem;
    color: var(--text-muted);
    font-style: italic;
  }
</style>
