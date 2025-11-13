import React, { useEffect, useState } from 'react'
import './App.css'
import { useUser } from './context/UserContext'
import { prestarLibro } from './services/PrestamoService'
import MisPrestamos from './components/MisPrestamos'
import MisReservas from './components/MisReservas'
import Header from './components/Header'
import BookList from './components/BookList'
import Footer from './components/Footer'

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

  if (currentView === 'misPrestamos') {
    return <MisPrestamos onBackToLibros={() => setCurrentView('libros')} />
  }

  if (currentView === 'reservas') {
    return <MisReservas onBackToLibros={() => setCurrentView('libros')} />
  }

  return (
    <div className="app">
      <Header
        currentUser={currentUser}
        mockUsers={mockUsers}
        loginUser={loginUser}
        onShowMisPrestamos={() => setCurrentView('misPrestamos')}
        onShowMisReservas={() => setCurrentView('reservas')}
      />

      <BookList
        books={books}
        loading={loading}
        error={error}
        prestamosLoading={prestamosLoading}
        prestamosError={prestamosError}
        onPrestar={handlePrestar}
      />

      <Footer />
    </div>
  )
}

export default App
