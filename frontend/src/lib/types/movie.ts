import type { Person } from './person'

export interface Movie {
  id: string
  title: string
  releaseYear?: number | null
  artworkUrl?: string | null
  pplIds?: string[]
  people?: Person[]
}

export interface CreateMovieInput {
  title: string
  releaseYear?: number | null
  artworkUrl?: string | null
  pplIds?: string[]
}

export interface UpdateMovieInput extends CreateMovieInput {
  id: string
}
