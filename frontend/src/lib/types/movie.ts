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

/**
 * A movie result returned from the TMDb search proxy in movie-service.
 * posterUrl is pre-built by the backend: https://image.tmdb.org/t/p/w500{poster_path}
 */
export interface TmdbMovieSearchResult {
  tmdbId: string
  title: string
  releaseYear: number | null
  posterUrl: string | null
  overview: string
}

export interface TmdbCreditPerson {
  id: string
  name: string
  role: string
  characterOrJob?: string | null
  profileUrl?: string | null
}

export interface TmdbCreditsResult {
  cast: TmdbCreditPerson[]
  crew: TmdbCreditPerson[]
}

