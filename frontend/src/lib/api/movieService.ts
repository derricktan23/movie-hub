import { executeGraphQL } from './graphqlClient'
import type { Movie, CreateMovieInput, TmdbMovieSearchResult, TmdbCreditsResult } from '../types/movie'

const MOVIE_ENDPOINT = '/api/movies/graphql'

export async function fetchMoviesApi(): Promise<Movie[]> {
  const query = `
    query GetAllMovies {
      movies {
        id
        title
        releaseYear
        artworkUrl
        pplIds
        people {
          id
          name
          role
        }
      }
    }
  `
  const data = await executeGraphQL<{ movies: Movie[] }>(MOVIE_ENDPOINT, query)
  return data.movies || []
}

export async function fetchMovieByIdApi(id: string): Promise<Movie | null> {
  const query = `
    query GetMovieById($id: ID!) {
      movie(id: $id) {
        id
        title
        releaseYear
        artworkUrl
        pplIds
        people {
          id
          name
          role
        }
      }
    }
  `
  const data = await executeGraphQL<{ movie: Movie | null }>(MOVIE_ENDPOINT, query, { id })
  return data.movie
}

export async function createMovieApi(input: CreateMovieInput): Promise<Movie> {
  const mutation = `
    mutation CreateMovie($title: String!, $releaseYear: Int, $artworkUrl: String, $pplIds: [ID!]) {
      createMovie(title: $title, releaseYear: $releaseYear, artworkUrl: $artworkUrl, pplIds: $pplIds) {
        id
        title
        releaseYear
        artworkUrl
        pplIds
      }
    }
  `
  const data = await executeGraphQL<{ createMovie: Movie }>(MOVIE_ENDPOINT, mutation, {
    title: input.title,
    releaseYear: input.releaseYear,
    artworkUrl: input.artworkUrl,
    pplIds: input.pplIds
  })
  return data.createMovie
}

export async function deleteMovieApi(id: string): Promise<boolean> {
  const mutation = `
    mutation DeleteMovie($id: ID!) {
      deleteMovie(id: $id)
    }
  `
  const data = await executeGraphQL<{ deleteMovie: boolean }>(MOVIE_ENDPOINT, mutation, { id })
  return data.deleteMovie
}

/**
 * Search TMDb movies via the movie-service GraphQL proxy.
 * The Bearer token is handled server-side — never exposed to the browser.
 *
 * @param query - Search term, e.g. "Inception"
 * @returns Array of TMDb movie suggestions with pre-built poster URLs
 */
export async function searchTmdbMoviesApi(query: string): Promise<TmdbMovieSearchResult[]> {
  if (!query.trim()) return []

  const gqlQuery = `
    query SearchTmdbMovies($query: String!) {
      searchTmdbMovies(query: $query) {
        tmdbId
        title
        releaseYear
        posterUrl
        overview
      }
    }
  `
  const data = await executeGraphQL<{ searchTmdbMovies: TmdbMovieSearchResult[] }>(
    MOVIE_ENDPOINT,
    gqlQuery,
    { query }
  )
  return data.searchTmdbMovies || []
}

/**
 * Fetch top cast and creators for a specific movie from TMDb via movie-service.
 */
export async function fetchTmdbMovieCreditsApi(tmdbId: string): Promise<TmdbCreditsResult> {
  if (!tmdbId.trim()) return { cast: [], crew: [] }

  const gqlQuery = `
    query GetTmdbMovieCredits($tmdbId: ID!) {
      tmdbMovieCredits(tmdbId: $tmdbId) {
        cast {
          id
          name
          role
          characterOrJob
          profileUrl
        }
        crew {
          id
          name
          role
          characterOrJob
          profileUrl
        }
      }
    }
  `
  const data = await executeGraphQL<{ tmdbMovieCredits: TmdbCreditsResult }>(
    MOVIE_ENDPOINT,
    gqlQuery,
    { tmdbId }
  )
  return data.tmdbMovieCredits || { cast: [], crew: [] }
}

