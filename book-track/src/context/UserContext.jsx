import React, { createContext, useState, useContext } from 'react'

const UserContext = createContext()

export const UserProvider = ({ children }) => {
  // Usuarios de prueba (coinciden con data.sql), Usados para probar el sistema de prestamos y devueltas de libros
  const mockUsers = [
    { id_usuario: 1, nombre: 'Sebastian', email: 'sebas@example.com' },
    { id_usuario: 2, nombre: 'Carlos', email: 'carlos@example.com' },
    { id_usuario: 3, nombre: 'María', email: 'maria@example.com' },
    { id_usuario: 4, nombre: 'Juan', email: 'juan@example.com' }
  ]

  const [currentUser, setCurrentUser] = useState(mockUsers[0]) // Usuario por defecto

  const loginUser = (usuario) => {
    setCurrentUser(usuario)
  }

  const logout = () => {
    setCurrentUser(null)
  }

  return (
    <UserContext.Provider value={{ currentUser, mockUsers, loginUser, logout }}>
      {children}
    </UserContext.Provider>
  )
}

export const useUser = () => {
  const context = useContext(UserContext)
  if (!context) {
    throw new Error('useUser debe usarse dentro de UserProvider')
  }
  return context
}
