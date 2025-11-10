import React, { useEffect, useState, useCallback } from 'react'
import { useUser } from '../context/UserContext'

const MisPrestamos = ({ onBackToLibros }) => {
  const [prestamos, setPrestamos] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [devolviendo, setDevolviendo] = useState({})

  const { currentUser } = useUser()

  const fetchPrestamos = useCallback(async () => {
    if (!currentUser) return
    setLoading(true)
    setError(null)
    try {
      const res = await fetch(
        `http://localhost:8080/prestamos/usuario/${currentUser.id_usuario}`
      )
      if (!res.ok) throw new Error(`HTTP error ${res.status}`)
      const data = await res.json()
      setPrestamos(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [currentUser])

  useEffect(() => {
    fetchPrestamos()
  }, [fetchPrestamos])

  const handleDevolver = async (idPrestamo) => {
    setDevolviendo(prev => ({ ...prev, [idPrestamo]: true }))
    try {
      const res = await fetch(`http://localhost:8080/prestamos/${idPrestamo}/devolver`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
      })

      if (!res.ok) throw new Error(`HTTP error ${res.status}`)
      alert('¡Libro devuelto exitosamente!')
      // Recargar préstamos
      await fetchPrestamos()
    } catch (err) {
      alert(`Error al devolver: ${err.message}`)
    } finally {
      setDevolviendo(prev => ({ ...prev, [idPrestamo]: false }))
    }
  }

  const diasRestantes = (fechaDevolucion) => {
    const hoy = new Date()
    const fecha = new Date(fechaDevolucion)
    const diff = fecha - hoy
    return Math.ceil(diff / (1000 * 60 * 60 * 24))
  }

  return (
    <div className="mis-prestamos">
      <header>
        <h1>Mis Préstamos</h1>
        <button className="btn-volver" onClick={onBackToLibros}>
          ← Volver a Libros
        </button>
      </header>

      {loading && <p>Cargando préstamos...</p>}
      {error && <p className="error">Error: {error}</p>}

      {!loading && !error && (
        <div className="prestamos-list">
          {prestamos.length === 0 ? (
            <p className="sin-prestamos">No tienes libros prestados actualmente.</p>
          ) : (
            <ul>
              {prestamos.map((p) => {
                const dias = diasRestantes(p.fechaDevolucion)
                const urgente = dias <= 3 && dias > 0
                const vencido = dias < 0

                return (
                  <li key={p.idPrestamo} className="prestamo-item">
                    <div className="prestamo-info">
                      <h3>{p.libro.titulo}</h3>
                      <p className="autor">Autor: {p.libro.autor}</p>
                      <p>Fecha de Préstamo: {new Date(p.fechaPrestamo).toLocaleDateString()}</p>
                      <p className={vencido ? 'fecha-vencida' : urgente ? 'fecha-urgente' : ''}>
                        Fecha de Devolución: {new Date(p.fechaDevolucion).toLocaleDateString()}
                      </p>

                      <p className={vencido ? 'dias-vencido' : urgente ? 'dias-urgente' : 'dias-normal'}>
                        {vencido ? `⚠️ VENCIDO hace ${Math.abs(dias)} día(s)` : `Días restantes: ${dias}`}
                      </p>
                    </div>

                    <button
                      className="btn-devolver"
                      onClick={() => handleDevolver(p.idPrestamo)}
                      disabled={devolviendo[p.idPrestamo]}
                    >
                      {devolviendo[p.idPrestamo] ? 'Devolviendo...' : 'Devolver'}
                    </button>
                  </li>
                )
              })}
            </ul>
          )}
        </div>
      )}
    </div>
  )
}

export default MisPrestamos
