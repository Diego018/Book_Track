import { AUTH_STORAGE_KEY } from './storageKeys'

const baseURL = import.meta.env.VITE_API_URL

export function getStoredToken() {
  if (typeof window === 'undefined') return null
  const rawState = window.localStorage.getItem(AUTH_STORAGE_KEY)
  return rawState ? JSON.parse(rawState)?.token : null
}

export async function apiClient(url, options = {}) {
  const token = getStoredToken()
  const { responseType, headers: customHeaders, ...restOptions } = options
  const isFormData = restOptions?.body instanceof FormData
  const headers = {
    ...(!isFormData && { 'Content-Type': 'application/json' }),
    ...(token && { Authorization: `Bearer ${token}` }),
    ...customHeaders,
  }

  const response = await fetch(baseURL + url, {
    ...restOptions,
    headers,
  })

  if (!response.ok) {
    const payload = await parseErrorPayload(response)
    const message = typeof payload === 'string'
      ? payload
      : payload?.mensaje || payload?.message || `Solicitud fallida (${response.status})`
    const error = new Error(message)
    error.status = response.status
    error.payload = payload
    throw error
  }

  if (response.status === 204) {
    return null
  }

  if (responseType === 'blob') {
    return response.blob()
  }

  const text = await response.text()
  if (!text) {
    return null
  }

  try {
    return JSON.parse(text)
  } catch (error) {
    return text
  }
}

async function parseErrorPayload(response) {
  const text = await response.text()
  if (!text) {
    return {}
  }
  try {
    return JSON.parse(text)
  } catch (error) {
    return { mensaje: text }
  }
}
