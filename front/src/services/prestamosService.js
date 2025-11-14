import { apiClient } from './apiClient'

const PUBLIC_BASE_PATH = '/api/prestamos'
const ADMIN_BASE_PATH = '/api/admin/prestamos'

export function getPrestamos() {
  return apiClient(PUBLIC_BASE_PATH)
}

export function createPrestamo(payload) {
  return apiClient(ADMIN_BASE_PATH, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function marcarPrestamoDevuelto(idPrestamo) {
  return apiClient(`${ADMIN_BASE_PATH}/${idPrestamo}/devolver`, {
    method: 'PUT',
  })
}
