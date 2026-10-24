import { writable, derived } from 'svelte/store'
import type { Person, CreatePersonInput } from '../types/person'
import { fetchPeopleApi, createPersonApi, deletePersonApi } from '../api/personService'
import { toastStore } from './toastStore'

interface PeopleState {
  items: Person[]
  loading: boolean
  error: string | null
}

function createPeopleStore() {
  const { subscribe, set, update } = writable<PeopleState>({
    items: [],
    loading: false,
    error: null
  })

  async function load() {
    update((s) => ({ ...s, loading: true, error: null }))
    try {
      const data = await fetchPeopleApi()
      update((s) => ({ ...s, items: data, loading: false }))
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to load people'
      update((s) => ({ ...s, loading: false, error: message }))
      toastStore.error(message, 'People Service Error')
    }
  }

  async function create(input: CreatePersonInput): Promise<Person | null> {
    update((s) => ({ ...s, loading: true }))
    try {
      const created = await createPersonApi(input)
      update((s) => ({
        ...s,
        items: [created, ...s.items],
        loading: false
      }))
      toastStore.success(`Added ${created.name} (${created.role})`, 'Person Created')
      return created
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to create person'
      update((s) => ({ ...s, loading: false }))
      toastStore.error(message, 'Creation Failed')
      return null
    }
  }

  async function remove(id: string): Promise<boolean> {
    update((s) => ({ ...s, loading: true }))
    try {
      const success = await deletePersonApi(id)
      if (success) {
        update((s) => ({
          ...s,
          items: s.items.filter((p) => p.id !== id),
          loading: false
        }))
        toastStore.success('Person removed from directory', 'Deleted')
        return true
      } else {
        throw new Error('Person record not found or could not be deleted')
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to delete person'
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

export const peopleStore = createPeopleStore()
