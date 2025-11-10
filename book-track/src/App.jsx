import React, { useEffect, useState } from 'react'
import './App.css'
import { useUser } from './context/UserContext'
import { prestarLibro } from './services/PrestamoService'
import MisPrestamos from './components/MisPrestamos'

function App() {
  const [books, setBooks] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [prestamosLoading, setPrestamosLoading] = useState({})
  const [prestamosError, setPrestamosError] = useState({})
  const [currentView, setCurrentView] = useState('libros') // 'libros' o 'misPrestamos'

  const { currentUser, mockUsers, loginUser } = useUser()

  useEffect(() => {
    const fetchBooks = async () => {
      setLoading(true)
      setError(null)
      try {
        const res = await fetch('http://localhost:8080/libros')
        if (!res.ok) throw new Error(`HTTP error ${res.status}`)
        const data = await res.json()
        setBooks(data)
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }
    fetchBooks()
  }, [])

  const handlePrestar = async (idLibro) => {
    if (!currentUser) {
      alert('Por favor selecciona un usuario primero')
      return
    }

    setPrestamosLoading(prev => ({ ...prev, [idLibro]: true }))
    setPrestamosError(prev => ({ ...prev, [idLibro]: null }))

    try {
      const result = await prestarLibro(idLibro, currentUser.id_usuario)
      alert(`¡Préstamo creado exitosamente! ID: ${result.idPrestamo}`)
      // Recargar libros para actualizar disponibilidad
      const res = await fetch('http://localhost:8080/libros')
      const data = await res.json()
      setBooks(data)
    } catch (err) {
      setPrestamosError(prev => ({ ...prev, [idLibro]: err.message }))
      alert(`Error al prestar: ${err.message}`)
    } finally {
      setPrestamosLoading(prev => ({ ...prev, [idLibro]: false }))
    }
  }

  // Vista de Libros
  if (currentView === 'libros') {
    return (
      <div className="app">
        <header>
          <h1>BookTrack — Libros</h1>
          
          {/* Selector de usuario mock */}
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
              onClick={() => setCurrentView('misPrestamos')}
            >
              📚 Mis Préstamos
            </button>
          </div>
        </header>

        {loading && <p>Cargando libros...</p>}
        {error && <p className="error">Error: {error}</p>}

        {!loading && !error && (
          <div className="books-list">
            {books.length === 0 ? (
              <p>No hay libros disponibles.</p>
            ) : (
              <ul>
                {books.map((b) => (
                  <li key={b.idLibro} className="book-item">
                    <h3>{b.titulo}</h3>
                    <p>Autor: {b.autor}</p>
                    <p>Fecha: {new Date(b.fecha).toLocaleDateString()}</p>
                    <p>Cantidad disponible: {b.cantidad_disponible}</p>
                    {b.generoLibro && (
                      <p>Género: {b.generoLibro.descLibro}</p>
                    )}
                    
                    {/* Botón de préstamo */}
                    <button
                      className="btn-prestar"
                      onClick={() => handlePrestar(b.idLibro)}
                      disabled={prestamosLoading[b.idLibro] || b.cantidad_disponible === 0}
                    >
                      {prestamosLoading[b.idLibro] ? 'Prestando...' : 'Prestar'}
                    </button>
                    {prestamosError[b.idLibro] && (
                      <p className="error-small">{prestamosError[b.idLibro]}</p>
                    )}
                  </li>
                ))}
              </ul>
            )}
          </div>
        )}

        <footer>
          <small>Frontend conectado a http://localhost:8080</small>
        </footer>
      </div>
    )
  }

  // Vista de Mis Préstamos
  if (currentView === 'misPrestamos') {
    return <MisPrestamos onBackToLibros={() => setCurrentView('libros')} />
  }
}

export default App
