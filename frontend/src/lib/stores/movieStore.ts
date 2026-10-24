import { writable } from 'svelte/store'
import type { Movie, CreateMovieInput } from '../types/movie'
import { fetchMoviesApi, createMovieApi, deleteMovieApi } from '../api/movieService'
import { toastStore } from './toastStore'

interface MovieState {
  items: Movie[]
  loading: boolean
  error: string | null
}

function createMovieStore() {
  const { subscribe, set, update } = writable<MovieState>({
    items: [],
    loading: false,
    error: null
  })

  async function load() {
    update((s) => ({ ...s, loading: true, error: null }))
    try {
      const data = await fetchMoviesApi()
      update((s) => ({ ...s, items: data, loading: false }))
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to load movies'
      update((s) => ({ ...s, loading: false, error: message }))
      toastStore.error(message, 'Movie Service Error')
    }
  }

  async function create(input: CreateMovieInput): Promise<Movie | null> {
    update((s) => ({ ...s, loading: true }))
    try {
      const created = await createMovieApi(input)
      update((s) => ({
        ...s,
        items: [created, ...s.items],
        loading: false
      }))
      toastStore.success(`Added "${created.title}" to catalog`, 'Movie Created')
      return created
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to create movie'
      update((s) => ({ ...s, loading: false }))
      toastStore.error(message, 'Creation Failed')
      return null
    }
  }

  async function remove(id: string): Promise<boolean> {
    update((s) => ({ ...s, loading: true }))
    try {
      const success = await deleteMovieApi(id)
      if (success) {
        update((s) => ({
          ...s,
          items: s.items.filter((m) => m.id !== id),
          loading: false
        }))
        toastStore.success('Movie deleted from catalog', 'Deleted')
        return true
      } else {
        throw new Error('Movie record not found or could not be deleted')
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to delete movie'
      update((s) => ({ ...s, loading: false }))
      toastStore.error(message, 'Delete Failed')
      return false
    }
  }

  return {
    subscribe,
    load,
    create,
    remove
  }
}

export const movieStore = createMovieStore()
