import React from 'react'

export default function Header({ currentUser, mockUsers = [], loginUser, onShowMisPrestamos, onShowMisReservas }) {
  return (
    <header>
      <h1>BookTrack — Libros</h1>

      <div className="user-selector">
        <label>
          Simular sesión:
          <select
            value={currentUser?.id_usuario || ''}
            onChange={(e) => {
              const usuario = mockUsers.find(u => u.id_usuario === parseInt(e.target.value))
              if (usuario) loginUser(usuario)
            }}
          >
            {mockUsers.map(user => (
              <option key={user.id_usuario} value={user.id_usuario}>
                {user.nombre} ({user.email})
              </option>
            ))}
          </select>
        </label>
        {currentUser && <span className="user-info">✓ Conectado como: {currentUser.nombre}</span>}
        <button
          className="btn-mis-prestamos"
          onClick={onShowMisPrestamos}
        >
          📚 Mis Préstamos
        </button>

        <button
          className="btn-mis-reservas"
          onClick={onShowMisReservas}
        >
          🔔 Mis Reservas
        </button>
      </div>
    </header>
  )
}
