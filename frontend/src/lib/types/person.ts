export type PersonRole = 'ACTOR' | 'DIRECTOR' | 'PRODUCER' | 'COMPOSER' | 'CREW'

export interface Person {
  id: string
  name: string
  role: PersonRole | string
}

export interface CreatePersonInput {
  name: string
  role: string
}
