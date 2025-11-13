import React, { useEffect, useState, useCallback } from 'react'
import { useUser } from '../context/UserContext'
import { fetchReservasUsuario, aceptarReserva, rechazarReserva } from '../services/ReservaService'

export default function MisReservas({ onBackToLibros }) {
  const { currentUser } = useUser()
  const [reservas, setReservas] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [procesando, setProcesando] = useState({})

  const load = useCallback(async () => {
    if (!currentUser) return
    setLoading(true)
    setError(null)
    try {
      const data = await fetchReservasUsuario(currentUser.id_usuario)
      setReservas(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [currentUser])

  useEffect(() => {
    load()
  }, [load])

  const handleAceptar = async (idReserva) => {
    setProcesando(prev => ({ ...prev, [idReserva]: true }))
    try {
      await aceptarReserva(idReserva)
      alert('Has aceptado la reserva y se ha creado el préstamo.')
      await load()
    } catch (err) {
      alert(`Error: ${err.message}`)
    } finally {
      setProcesando(prev => ({ ...prev, [idReserva]: false }))
    }
  }

  const handleRechazar = async (idReserva) => {
    setProcesando(prev => ({ ...prev, [idReserva]: true }))
    try {
      await rechazarReserva(idReserva)
      alert('Has rechazado la reserva.')
      await load()
    } catch (err) {
      alert(`Error: ${err.message}`)
    } finally {
      setProcesando(prev => ({ ...prev, [idReserva]: false }))
    }
  }

  return (
    <div className="mis-reservas">
      <header>
        <h1>Mis Reservas</h1>
        <button className="btn-volver" onClick={onBackToLibros}>← Volver a Libros</button>
      </header>

      {loading && <p>Cargando reservas...</p>}
      {error && <p className="error">Error: {error}</p>}

      {!loading && !error && (
        <div className="reservas-list">
          {reservas.length === 0 ? (
            <p>No tienes reservas.</p>
          ) : (
            <ul>
              {reservas.map(r => (
                <li key={r.idReserva} className="reserva-item">
                  <div>
                    <h3>{r.tituloLibro || '—'}</h3>
                    <p>Estado: {r.estadoReserva}</p>
                    <p>Fecha reserva: {r.fechaReserva ? new Date(r.fechaReserva).toLocaleString() : ''}</p>
                  </div>

                  {r.estadoReserva === 'NOTIFICADO' && (
                    <div className="acciones-reserva">
                      <button disabled={procesando[r.idReserva]} onClick={() => handleAceptar(r.idReserva)}>
                        {procesando[r.idReserva] ? 'Procesando...' : 'Aceptar'}
                      </button>
                      <button disabled={procesando[r.idReserva]} onClick={() => handleRechazar(r.idReserva)}>
                        {procesando[r.idReserva] ? 'Procesando...' : 'Rechazar'}
                      </button>
                    </div>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  )
}
