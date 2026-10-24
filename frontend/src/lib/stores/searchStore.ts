import { writable, derived } from 'svelte/store'
import { movieStore } from './movieStore'
import { peopleStore } from './peopleStore'

export const searchQuery = writable<string>('')

export const filteredMovies = derived([movieStore, searchQuery], ([$movieStore, $query]) => {
  const q = $query.trim().toLowerCase()
  if (!q) return $movieStore.items

  return $movieStore.items.filter((movie) => {
    const titleMatch = movie.title.toLowerCase().includes(q)
    const yearMatch = movie.releaseYear ? movie.releaseYear.toString().includes(q) : false
    const castMatch = movie.people?.some((p) => p.name.toLowerCase().includes(q)) ?? false
    return titleMatch || yearMatch || castMatch
  })
})

export const filteredPeople = derived([peopleStore, searchQuery], ([$peopleStore, $query]) => {
  const q = $query.trim().toLowerCase()
  if (!q) return $peopleStore.items

  return $peopleStore.items.filter((person) => {
    const nameMatch = person.name.toLowerCase().includes(q)
    const roleMatch = person.role.toLowerCase().includes(q)
    return nameMatch || roleMatch
  })
})
