import { apiClient, getStoredToken } from './apiClient'

const BASE_PATH = '/api/actividad'
const baseURL = import.meta.env.VITE_API_URL

export function getActividad() {
  return apiClient(BASE_PATH)
}

export async function downloadActividadLog() {
  const token = getStoredToken()
  const response = await fetch(`${baseURL}${BASE_PATH}/logs`, {
    headers: {
      ...(token && { Authorization: `Bearer ${token}` }),
    },
  })

  if (!response.ok) {
    const message = await parseDownloadError(response)
    const error = new Error(message)
    error.status = response.status
    throw error
  }

  const blob = await response.blob()
  const disposition = response.headers.get('Content-Disposition') || ''
  const filenameMatch = disposition.match(/filename="?([^";]+)"?/i)
  const filename = filenameMatch ? filenameMatch[1] : 'actividad.txt'
  return { blob, filename }
}

async function parseDownloadError(response) {
  const text = await response.text()
  if (!text) return 'No se pudo descargar el log.'
  try {
    const data = JSON.parse(text)
    return data?.mensaje || data?.message || 'No se pudo descargar el log.'
  } catch (error) {
    return text
  }
}
