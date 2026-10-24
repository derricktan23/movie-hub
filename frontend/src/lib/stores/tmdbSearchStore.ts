import { writable, get } from 'svelte/store'
import type { TmdbMovieSearchResult } from '../types/movie'
import { searchTmdbMoviesApi } from '../api/movieService'
import { toastStore } from './toastStore'

interface TmdbSearchState {
  query: string
  results: TmdbMovieSearchResult[]
  loading: boolean
  error: string | null
  /** Whether the dropdown should be visible */
  open: boolean
}

const DEBOUNCE_MS = 300

function createTmdbSearchStore() {
  const { subscribe, set, update } = writable<TmdbSearchState>({
    query: '',
    results: [],
    loading: false,
    error: null,
    open: false
  })

  // Tracks the latest debounce timer so fast typing cancels prior requests
  let debounceTimer: ReturnType<typeof setTimeout> | null = null
  // Tracks a monotonically-increasing request ID so stale responses are discarded
  let currentRequestId = 0

  /**
   * Called on every keystroke. Debounces 300ms before firing the GraphQL request.
   * Stale in-flight responses (from prior rapid keystrokes) are automatically discarded.
   */
  function search(query: string) {
    // Update query text immediately for responsive UI
    update((s) => ({ ...s, query, error: null }))

    // Cancel any pending debounce
    if (debounceTimer !== null) clearTimeout(debounceTimer)

    if (!query.trim()) {
      set({ query, results: [], loading: false, error: null, open: false })
      return
    }

    // Show spinner immediately so UX feels live
    update((s) => ({ ...s, loading: true, open: true }))

    debounceTimer = setTimeout(async () => {
      const requestId = ++currentRequestId

      try {
        const results = await searchTmdbMoviesApi(query)

        // Discard stale response if a newer request superseded this one
        if (requestId !== currentRequestId) return

        update((s) => ({
          ...s,
          results,
          loading: false,
          open: results.length > 0,
          error: null
        }))
      } catch (err) {
        if (requestId !== currentRequestId) return
        const message = err instanceof Error ? err.message : 'TMDb search failed'
        update((s) => ({ ...s, loading: false, results: [], error: message }))
        toastStore.error(message, 'TMDb Search Error')
      }
    }, DEBOUNCE_MS)
  }

  /** Called when the user picks a suggestion — closes dropdown and clears results */
  function select() {
    update((s) => ({ ...s, open: false, results: [] }))
  }

  /** Clears the entire search state — call on modal close/reset */
  function clear() {
    if (debounceTimer !== null) clearTimeout(debounceTimer)
    currentRequestId++ // Discard any in-flight request
    set({ query: '', results: [], loading: false, error: null, open: false })
  }

  /** Close the dropdown without clearing the query (e.g. click-outside) */
  function closeDropdown() {
    update((s) => ({ ...s, open: false }))
  }

  return {
    subscribe,
    search,
    select,
    clear,
    closeDropdown
  }
}

export const tmdbSearchStore = createTmdbSearchStore()
