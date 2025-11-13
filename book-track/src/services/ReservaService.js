const API_URL = 'http://localhost:8080'

export const fetchReservasUsuario = async (idUsuario) => {
  const res = await fetch(`${API_URL}/reservas/usuario/${idUsuario}`)
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: res.statusText }))
    throw new Error(err.message || `HTTP ${res.status}`)
  }
  return await res.json()
}

export const aceptarReserva = async (idReserva) => {
  const res = await fetch(`${API_URL}/reservas/${idReserva}/aceptar`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: res.statusText }))
    throw new Error(err.message || `HTTP ${res.status}`)
  }
  return await res.json()
}

export const rechazarReserva = async (idReserva) => {
  const res = await fetch(`${API_URL}/reservas/${idReserva}/rechazar`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: res.statusText }))
    throw new Error(err.message || `HTTP ${res.status}`)
  }
  return await res.json()
}

export default { fetchReservasUsuario, aceptarReserva, rechazarReserva }
