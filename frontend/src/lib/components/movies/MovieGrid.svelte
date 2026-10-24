<script lang="ts">
  import type { Movie } from '../../types/movie'
  import MovieCard from './MovieCard.svelte'
  import DeleteConfirmModal from '../common/DeleteConfirmModal.svelte'
  import EmptyState from '../common/EmptyState.svelte'
  import { movieStore } from '../../stores/movieStore'
  import { searchQuery } from '../../stores/searchStore'
  import { Film } from '@lucide/svelte'

  interface Props {
    movies: Movie[]
    loading: boolean
    onAddMovie: () => void
  }

  let { movies = [], loading = false, onAddMovie }: Props = $props()

  let selectedMovieToDelete = $state<Movie | null>(null)
  let deleteModalOpen = $state(false)
  let deleteLoading = $state(false)

  function handleDeleteClick(movie: Movie) {
    selectedMovieToDelete = movie
    deleteModalOpen = true
  }

  async function handleConfirmDelete() {
    if (!selectedMovieToDelete?.id) return
    deleteLoading = true
    await movieStore.remove(selectedMovieToDelete.id)
    deleteLoading = false
    deleteModalOpen = false
    selectedMovieToDelete = null
  }

  function handleCloseDeleteModal() {
    deleteModalOpen = false
    selectedMovieToDelete = null
  }
</script>

{#if loading && movies.length === 0}
  <div class="grid-skeleton">
    {#each Array(6) as _}
      <div class="skeleton-card">
        <div class="skeleton-thumb"></div>
        <div class="skeleton-body">
          <div class="skeleton-line line-lg"></div>
          <div class="skeleton-line line-sm"></div>
        </div>
      </div>
    {/each}
  </div>
{:else if movies.length === 0}
  <EmptyState
    isSearch={Boolean($searchQuery)}
    searchQuery={$searchQuery}
    icon={Film}
    title={$searchQuery ? 'No Movies Found' : 'Your Movie Catalog is Empty'}
    description={$searchQuery ? '' : 'Get started by adding your first movie title and linking actors & creators.'}
    actionText="Add First Movie"
    onAction={onAddMovie}
    onClearSearch={() => searchQuery.set('')}
  />
{:else}
  <div class="movies-grid animate-fade-in">
    {#each movies as movie (movie.id)}
      <MovieCard {movie} onDelete={handleDeleteClick} />
    {/each}
  </div>
{/if}

{#if selectedMovieToDelete}
  <DeleteConfirmModal
    open={deleteModalOpen}
    title="Delete Movie"
    itemName={selectedMovieToDelete.title}
    loading={deleteLoading}
    onclose={handleCloseDeleteModal}
    onconfirm={handleConfirmDelete}
  />
{/if}

<style>
  .movies-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 24px;
  }

  .grid-skeleton {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 24px;
  }

  .skeleton-card {
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
    overflow: hidden;
    height: 320px;
    display: flex;
    flex-direction: column;
  }

  .skeleton-thumb {
    width: 100%;
    aspect-ratio: 16 / 10;
    background: #F1F5F9;
    animation: pulse 1.5s infinite;
  }

  .skeleton-body {
    padding: 18px;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .skeleton-line {
    background: #E2E8F0;
    border-radius: var(--radius-sm);
    animation: pulse 1.5s infinite;
  }

  .line-lg {
    height: 18px;
    width: 75%;
  }

  .line-sm {
    height: 12px;
    width: 45%;
  }

  @keyframes pulse {
    0%, 100% { opacity: 0.8; }
    50% { opacity: 0.4; }
  }
</style>
