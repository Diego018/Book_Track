import { apiClient } from './apiClient'

const ADMIN_BASE_PATH = '/api/admin/libros'
const USER_BASE_PATH = '/api/libros'
const IMPORT_CSV_PATH = '/api/admin/libros/import-csv'

export async function getLibrosAdmin() {
  return apiClient(ADMIN_BASE_PATH)
}

export async function getLibrosPublic() {
  return apiClient(USER_BASE_PATH)
}

export async function createLibro(payload) {
  return apiClient(ADMIN_BASE_PATH, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function deleteLibro(idLibro) {
  return apiClient(`${ADMIN_BASE_PATH}/${idLibro}`, {
    method: 'DELETE',
  })
}

export async function importLibrosCsv(file) {
  if (!file) {
    throw new Error('Debes seleccionar un archivo CSV válido.')
  }

  const formData = new FormData()
  formData.append('file', file)

  return apiClient(IMPORT_CSV_PATH, {
    method: 'POST',
    body: formData,
  })
}
