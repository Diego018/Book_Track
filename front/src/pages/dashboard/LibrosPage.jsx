import { useCallback, useEffect, useRef, useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import {
  createLibro,
  deleteLibro,
  getLibrosAdmin,
  getLibrosPublic,
  importLibrosCsv,
} from '../../services/librosService'

const INITIAL_FORM_VALUES = {
  titulo: '',
  autor: '',
  fecha: '',
  cantidad_total: '',
  cantidad_disponible: '',
  generoLibro: '',
}

export default function LibrosPage() {
  const { isAdmin } = useAuth()
  const [libros, setLibros] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [formValues, setFormValues] = useState(INITIAL_FORM_VALUES)
  const [formError, setFormError] = useState('')
  const [creating, setCreating] = useState(false)
  const [deletingId, setDeletingId] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [showImportPanel, setShowImportPanel] = useState(false)
  const [csvFile, setCsvFile] = useState(null)
  const [isImporting, setIsImporting] = useState(false)
  const [statusMessage, setStatusMessage] = useState('')
  const fileInputRef = useRef(null)

  const loadLibros = useCallback(async (options = {}) => {
    const { clearStatus = false } = options
    setLoading(true)
    setError('')
    if (clearStatus) {
      setStatusMessage('')
    }
    try {
      const fetcher = isAdmin ? getLibrosAdmin : getLibrosPublic
      const data = await fetcher()
      const lista = Array.isArray(data) ? data : data?.content ?? []
      setLibros(lista)
    } catch (err) {
      console.error(err)
      const backendMessage = err?.message || err?.mensaje
      setError(backendMessage || 'No se pudieron cargar los libros.')
    } finally {
      setLoading(false)
    }
  }, [isAdmin])

  useEffect(() => {
    loadLibros()
  }, [loadLibros])

  const handleChange = (event) => {
    const { name, value } = event.target
    setFormValues((prev) => ({
      ...prev,
      [name]: value,
    }))
    setFormError('')
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    if (!isAdmin) return
    setStatusMessage('')
    if (!formValues.titulo.trim() || !formValues.autor.trim() || !formValues.cantidad_total) {
      setFormError('Título, autor y cantidad total son obligatorios.')
      return
    }

    const payload = {
      titulo: formValues.titulo.trim(),
      autor: formValues.autor.trim(),
      fecha: formValues.fecha || null,
      cantidad_total: Number(formValues.cantidad_total) || 0,
      cantidad_disponible:
        formValues.cantidad_disponible === ''
          ? Number(formValues.cantidad_total) || 0
          : Number(formValues.cantidad_disponible) || 0,
      generoLibro: formValues.generoLibro,
    }

    setCreating(true)
    setFormError('')
    try {
      const nuevoLibro = await createLibro(payload)
      setFormValues(INITIAL_FORM_VALUES)
      setShowForm(false)
      if (nuevoLibro?.idLibro) {
        setLibros((prev) => {
          const sinDuplicados = prev.filter((libro) => libro.idLibro !== nuevoLibro.idLibro)
          return [nuevoLibro, ...sinDuplicados]
        })
      } else {
        await loadLibros({ clearStatus: true })
      }
    } catch (err) {
      console.error(err)
      const backendMessage = err?.message || err?.mensaje
      setFormError(backendMessage || 'No se pudo crear el libro.')
    } finally {
      setCreating(false)
    }
  }

  const handleDelete = async (idLibro) => {
    if (!isAdmin) return
    const confirmDelete = typeof window !== 'undefined'
      ? window.confirm('¿Seguro que deseas eliminar este libro?')
      : true
    if (!confirmDelete) return

    setDeletingId(idLibro)
    setError('')
    setStatusMessage('')
    try {
      await deleteLibro(idLibro)
      setLibros((prev) => prev.filter((libro) => libro.idLibro !== idLibro))
    } catch (err) {
      console.error(err)
      const backendMessage = err?.message || err?.mensaje
      setError(backendMessage || 'No se pudo eliminar el libro.')
    } finally {
      setDeletingId(null)
    }
  }

  const handleImportCsv = async () => {
    if (!isAdmin) return
    if (!csvFile) {
      setError('Selecciona un archivo CSV antes de importar.')
      return
    }

    const fileName = csvFile.name?.toLowerCase() ?? ''
    if (!fileName.endsWith('.csv')) {
      setError('El archivo debe tener extensión .csv.')
      return
    }

    setIsImporting(true)
    setError('')
    setStatusMessage('')
    try {
  const report = await importLibrosCsv(csvFile)
  await loadLibros({ clearStatus: true })
  const inserted = normalizarNumero(report?.inserted ?? report?.nuevos ?? report?.created)
  const duplicates = normalizarNumero(report?.duplicates ?? report?.duplicados)
  const errors = normalizarNumero(report?.errors ?? report?.errores)
      const summaryParts = []
      if (Number.isFinite(inserted)) summaryParts.push(`${inserted} nuevos`)
      if (Number.isFinite(duplicates)) summaryParts.push(`${duplicates} duplicados`)
      if (Number.isFinite(errors)) summaryParts.push(`${errors} con error`)
      const summary = summaryParts.length
        ? `Importación completada: ${summaryParts.join(', ')}.`
        : 'Importación completada correctamente.'
      setStatusMessage(report?.mensaje || summary)
      setCsvFile(null)
      if (fileInputRef.current) {
        fileInputRef.current.value = ''
      }
      setShowImportPanel(false)
    } catch (err) {
      console.error(err)
      const backendMessage = err?.message || err?.mensaje
      setError(backendMessage || 'No se pudo importar el archivo CSV.')
    } finally {
      setIsImporting(false)
    }
  }

  const toggleForm = () => {
    if (!isAdmin) return
    setShowForm((prev) => {
      if (prev) {
        setFormValues(INITIAL_FORM_VALUES)
        setFormError('')
      }
      return !prev
    })
  }

  const toggleImportPanel = () => {
    if (!isAdmin) return
    setShowImportPanel((prev) => {
      if (prev && fileInputRef.current) {
        fileInputRef.current.value = ''
      }
      if (prev) {
        setCsvFile(null)
      } else {
        setStatusMessage('')
      }
      return !prev
    })
  }

  const handleCancel = () => {
    setFormValues(INITIAL_FORM_VALUES)
    setFormError('')
    setShowForm(false)
  }

  return (
    <section className="rounded-3xl border border-slate-100 bg-white p-6 shadow-sm">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <h2 className="text-xl font-semibold text-slate-900">Inventario de libros</h2>
        {isAdmin ? (
          <div className="flex flex-wrap items-center gap-3">
            <button
              type="button"
              onClick={toggleImportPanel}
              className="rounded-2xl border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:border-slate-300 hover:text-slate-900"
            >
              {showImportPanel ? 'Cerrar importación' : 'Importar CSV'}
            </button>
            <button
              onClick={toggleForm}
              className="rounded-2xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-800"
            >
              {showForm ? 'Cerrar formulario' : 'Agregar libro'}
            </button>
          </div>
        ) : (
          <p className="text-sm text-slate-500">Solo los administradores pueden registrar libros.</p>
        )}
      </div>

      {isAdmin && showImportPanel && (
        <div className="mt-4 rounded-2xl border border-dashed border-slate-300 bg-slate-50 p-4">
          <p className="mb-2 text-sm text-slate-700">
            Selecciona un archivo CSV con las columnas
            <code className="ml-1 text-xs text-slate-500">
              titulo, autor, fecha, cantidad_total, cantidad_disponible, generoLibro
            </code>
          </p>
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
            <input
              ref={fileInputRef}
              type="file"
              accept=".csv"
              onChange={(event) => setCsvFile(event.target.files?.[0] ?? null)}
              className="text-sm"
            />
            <button
              type="button"
              disabled={!csvFile || isImporting}
              onClick={handleImportCsv}
              className="rounded-2xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isImporting ? 'Importando…' : 'Subir CSV'}
            </button>
          </div>
          {csvFile && (
            <p className="mt-2 text-xs text-slate-500">Archivo seleccionado: {csvFile.name}</p>
          )}
        </div>
      )}

      {!isAdmin && (
        <div className="mt-4 rounded-2xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-700">
          Puedes consultar el inventario, pero para crear o eliminar libros necesitas permisos de administrador.
        </div>
      )}

      {isAdmin && showForm && (
        <div className="mt-6 rounded-2xl border border-slate-200 p-6">
          <form className="space-y-4" onSubmit={handleSubmit}>
            {formError && (
              <p className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-600">
                {formError}
              </p>
            )}
            <div className="grid gap-4 md:grid-cols-2">
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="titulo">
                  Título
                </label>
                <input
                  id="titulo"
                  name="titulo"
                  type="text"
                  value={formValues.titulo}
                  onChange={handleChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                  placeholder="Ej. Cien años de soledad"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="autor">
                  Autor
                </label>
                <input
                  id="autor"
                  name="autor"
                  type="text"
                  value={formValues.autor}
                  onChange={handleChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                  placeholder="Ej. Gabriel García Márquez"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="fecha">
                  Fecha
                </label>
                <input
                  id="fecha"
                  name="fecha"
                  type="date"
                  value={formValues.fecha}
                  onChange={handleChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="generoLibro">
                  Género
                </label>
                <input
                  id="generoLibro"
                  name="generoLibro"
                  type="text"
                  value={formValues.generoLibro}
                  onChange={handleChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                  placeholder="Ej. Realismo mágico"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="cantidad_total">
                  Cantidad total
                </label>
                <input
                  id="cantidad_total"
                  name="cantidad_total"
                  type="number"
                  min="0"
                  value={formValues.cantidad_total}
                  onChange={handleChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="cantidad_disponible">
                  Cantidad disponible
                </label>
                <input
                  id="cantidad_disponible"
                  name="cantidad_disponible"
                  type="number"
                  min="0"
                  value={formValues.cantidad_disponible}
                  onChange={handleChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                />
              </div>
            </div>
            <div className="flex flex-wrap justify-end gap-3">
              <button
                type="button"
                onClick={handleCancel}
                className="rounded-2xl border border-slate-300 px-5 py-2 text-sm font-semibold text-slate-600 transition hover:border-slate-400 hover:text-slate-800"
              >
                Cancelar
              </button>
              <button
                type="submit"
                disabled={creating}
                className="rounded-2xl bg-slate-900 px-6 py-2 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-300"
              >
                {creating ? 'Guardando…' : 'Guardar libro'}
              </button>
            </div>
          </form>
        </div>
      )}

      {!loading && statusMessage && (
        <div className="mt-6 rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
          {statusMessage}
        </div>
      )}

      {!loading && error && (
        <div className="mt-6 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-600">
          {error}
        </div>
      )}

      <div className="mt-6 overflow-hidden rounded-2xl border border-slate-200">
        {loading ? (
          <div className="px-6 py-8 text-center text-sm text-slate-500">Cargando libros…</div>
        ) : libros.length === 0 ? (
          <div className="px-6 py-8 text-center text-sm text-slate-500">No hay libros registrados.</div>
        ) : (
          <table className="min-w-full divide-y divide-slate-200 text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-6 py-3">ID</th>
                <th className="px-6 py-3">Título</th>
                <th className="px-6 py-3">Autor</th>
                <th className="px-6 py-3">Género</th>
                <th className="px-6 py-3 text-center">Disponibles</th>
                <th className="px-6 py-3 text-right">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 bg-white">
              {libros.map((libro) => (
                <tr key={libro.idLibro}>
                  <td className="px-6 py-4 font-semibold text-slate-900">{libro.idLibro}</td>
                  <td className="px-6 py-4 text-slate-700">{libro.titulo}</td>
                  <td className="px-6 py-4 text-slate-700">{libro.autor}</td>
                  <td className="px-6 py-4 text-slate-500">{libro.generoLibro}</td>
                  <td className="px-6 py-4 text-center text-slate-900">{libro.cantidad_disponible}</td>
                  <td className="px-6 py-4 text-right">
                    {isAdmin ? (
                      <button
                        onClick={() => handleDelete(libro.idLibro)}
                        disabled={deletingId === libro.idLibro}
                        className="rounded-full border border-rose-200 px-4 py-1 text-xs font-semibold text-rose-600 transition hover:border-rose-500 hover:text-rose-700 disabled:cursor-not-allowed disabled:border-slate-200 disabled:text-slate-400"
                      >
                        {deletingId === libro.idLibro ? 'Eliminando…' : 'Eliminar'}
                      </button>
                    ) : (
                      <span className="text-xs font-semibold text-slate-400">Sin permisos</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </section>
  )
}

function normalizarNumero(value) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}
