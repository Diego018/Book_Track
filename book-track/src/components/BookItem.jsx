import React from 'react'

export default function BookItem({ book, onPrestar, loading = false, error = null }) {
  return (
    <li key={book.idLibro} className="book-item">
      <h3>{book.titulo}</h3>
      <p>Autor: {book.autor}</p>
      <p>Fecha: {new Date(book.fecha).toLocaleDateString()}</p>
      <p>Cantidad disponible: {book.cantidad_disponible}</p>
      {book.generoLibro && (
        <p>Género: {book.generoLibro.descLibro}</p>
      )}

      <button
        className="btn-prestar"
        onClick={() => onPrestar(book.idLibro)}
        disabled={loading}
      >
        {loading ? 'Procesando...' : book.cantidad_disponible > 0 ? 'Prestar' : 'Reservar'}
      </button>
      {error && (
        <p className="error-small">{error}</p>
      )}
    </li>
  )
}
