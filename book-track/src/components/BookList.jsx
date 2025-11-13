import React from 'react'
import BookItem from './BookItem'

export default function BookList({ books = [], loading, error, prestamosLoading = {}, prestamosError = {}, onPrestar }) {
  if (loading) return <p>Cargando libros...</p>
  if (error) return <p className="error">Error: {error}</p>

  return (
    <div className="books-list">
      {books.length === 0 ? (
        <p>No hay libros disponibles.</p>
      ) : (
        <ul>
          {books.map((b) => (
            <BookItem
              key={b.idLibro}
              book={b}
              onPrestar={onPrestar}
              loading={Boolean(prestamosLoading[b.idLibro])}
              error={prestamosError[b.idLibro]}
            />
          ))}
        </ul>
      )}
    </div>
  )
}
