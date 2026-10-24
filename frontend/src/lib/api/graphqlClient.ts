interface GraphQLResponse<T> {
  data?: T
  errors?: Array<{ message: string; locations?: unknown[]; path?: string[] }>
}

export async function executeGraphQL<T>(
  endpoint: string,
  query: string,
  variables: Record<string, unknown> = {}
): Promise<T> {
  try {
    const response = await fetch(endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json'
      },
      body: JSON.stringify({ query, variables })
    })

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }

    const result: GraphQLResponse<T> = await response.json()

    if (result.errors && result.errors.length > 0) {
      throw new Error(result.errors.map((e) => e.message).join(', '))
    }

    if (!result.data) {
      throw new Error('No data received from GraphQL server')
    }

    return result.data
  } catch (error) {
    if (error instanceof Error) {
      throw error
    }
    throw new Error('An unexpected network error occurred')
  }
}
