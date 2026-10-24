import { executeGraphQL } from './graphqlClient'
import type { Movie, CreateMovieInput } from '../types/movie'

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
