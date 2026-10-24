import { executeGraphQL } from './graphqlClient'
import type { Person, CreatePersonInput } from '../types/person'

const PPL_ENDPOINT = '/api/movies/graphql'

export async function fetchPeopleApi(): Promise<Person[]> {
  const query = `
    query GetAllPeople {
      people {
        id
        name
        role
      }
    }
  `
  const data = await executeGraphQL<{ people: Person[] }>(PPL_ENDPOINT, query)
  return data.people || []
}

export async function fetchPersonByIdApi(id: string): Promise<Person | null> {
  const query = `
    query GetPersonById($id: ID!) {
      person(id: $id) {
        id
        name
        role
      }
    }
  `
  const data = await executeGraphQL<{ person: Person | null }>(PPL_ENDPOINT, query, { id })
  return data.person
}

export async function createPersonApi(input: CreatePersonInput): Promise<Person> {
  const mutation = `
    mutation CreatePerson($name: String!, $role: String!) {
      createPerson(name: $name, role: $role) {
        id
        name
        role
      }
    }
  `
  const data = await executeGraphQL<{ createPerson: Person }>(PPL_ENDPOINT, mutation, {
    name: input.name,
    role: input.role
  })
  return data.createPerson
}

export async function deletePersonApi(id: string): Promise<boolean> {
  const mutation = `
    mutation DeletePerson($id: ID!) {
      deletePerson(id: $id)
    }
  `
  const data = await executeGraphQL<{ deletePerson: boolean }>(PPL_ENDPOINT, mutation, { id })
  return data.deletePerson
}
