import { apiClient } from './apiClient'

const PUBLIC_BASE_PATH = '/api/reservas'
const ADMIN_BASE_PATH = '/api/admin/reservas'

export function getReservas() {
  return apiClient(PUBLIC_BASE_PATH)
}

export function crearReserva(payload, { admin = false } = {}) {
  const url = admin ? ADMIN_BASE_PATH : PUBLIC_BASE_PATH
  return apiClient(url, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function actualizarEstadoReserva(idReserva, estado) {
  return apiClient(`${ADMIN_BASE_PATH}/${idReserva}/estado`, {
    method: 'PUT',
    body: JSON.stringify({ estado }),
  })
}

export function cancelarReserva(idReserva) {
  return apiClient(`${PUBLIC_BASE_PATH}/${idReserva}/cancelar`, {
    method: 'PUT',
  })
}
