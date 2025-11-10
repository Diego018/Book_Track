const API_URL = 'http://localhost:8080'

export const prestarLibro = async (idLibro, idUsuario) => {
  const res = await fetch(`${API_URL}/libros/${idLibro}/prestar`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ idUsuario })
  })

  if (!res.ok) {
    const errorData = await res.json().catch(() => ({ message: res.statusText }))
    throw new Error(errorData.message || `HTTP ${res.status}`)
  }

  return await res.json()
}
