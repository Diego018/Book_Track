import { useCallback, useEffect, useMemo, useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import { createPrestamo, getPrestamos, marcarPrestamoDevuelto } from '../../services/prestamosService'

const INITIAL_FORM_VALUES = {
  libroId: '',
  usuarioEmail: '',
  fechaDevolucion: '',
}

const DATE_FORMATTER = new Intl.DateTimeFormat('es-CO', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
})

const parseLocalDate = (value) => {
  if (!value) return null
  const [year, month, day] = value.split('-').map(Number)
  if (!year || !month || !day) return null
  return new Date(year, month - 1, day)
}

const formatDate = (value) => {
  const parsed = typeof value === 'string' ? parseLocalDate(value) : value instanceof Date ? value : null
  if (!parsed || Number.isNaN(parsed.getTime())) return '—'
  return DATE_FORMATTER.format(parsed)
}

const resolveEstado = (prestamo) => {
  const baseEstado = prestamo?.estado?.toUpperCase?.()
  const dueDate = prestamo?.fechaDevolucion ? parseLocalDate(prestamo.fechaDevolucion) : null
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  if (baseEstado === 'DEVUELTO') {
    return { label: 'Devuelto', style: 'bg-emerald-100 text-emerald-700' }
  }

  if (dueDate && dueDate.getTime() < today.getTime()) {
    return { label: 'Retrasado', style: 'bg-rose-100 text-rose-700' }
  }

  if (baseEstado === 'ACTIVO') {
    return { label: 'Activo', style: 'bg-amber-100 text-amber-700' }
  }

  return { label: 'En curso', style: 'bg-slate-100 text-slate-700' }
}

export default function PrestamosPage() {
  const { isAdmin, usuario } = useAuth()
  const [prestamos, setPrestamos] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [formValues, setFormValues] = useState(INITIAL_FORM_VALUES)
  const [formError, setFormError] = useState('')
  const [creating, setCreating] = useState(false)
  const [devolviendoId, setDevolviendoId] = useState(null)
  const [showForm, setShowForm] = useState(false)

  const headingSubtitle = isAdmin
    ? 'Gestiona los préstamos del catálogo y marca devoluciones en tiempo real.'
    : 'Consulta el estado de los préstamos que tienes activos en la biblioteca.'

  const emptyStateText = isAdmin ? 'No hay préstamos registrados.' : 'Aún no tienes préstamos registrados.'

  const loadPrestamos = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const data = await getPrestamos()
      setPrestamos(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error(err)
      setError('No se pudieron cargar los préstamos.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadPrestamos()
  }, [loadPrestamos])

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

  const handleChange = (event) => {
    const { name, value } = event.target
    setFormValues((prev) => ({
      ...prev,
      [name]: value,
    }))
    setFormError('')
  }

  const validateForm = () => {
    if (!formValues.libroId.trim()) {
      return 'El ID del libro es obligatorio.'
    }
    const libroIdNumber = Number(formValues.libroId)
    if (Number.isNaN(libroIdNumber) || libroIdNumber <= 0) {
      return 'Ingresa un ID de libro válido.'
    }
    if (!formValues.usuarioEmail.trim()) {
      return 'El correo del lector es obligatorio.'
    }
    if (!formValues.fechaDevolucion) {
      return 'Selecciona la fecha estimada de devolución.'
    }
    const dueDate = parseLocalDate(formValues.fechaDevolucion)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    if (!dueDate || dueDate.getTime() < today.getTime()) {
      return 'La fecha de devolución no puede estar en el pasado.'
    }
    return ''
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    if (!isAdmin) return
    const validationMessage = validateForm()
    if (validationMessage) {
      setFormError(validationMessage)
      return
    }

    const payload = {
      libroId: Number(formValues.libroId),
      usuarioEmail: formValues.usuarioEmail.trim(),
      fechaDevolucion: formValues.fechaDevolucion,
    }

    setCreating(true)
    setFormError('')
    try {
      await createPrestamo(payload)
      setFormValues(INITIAL_FORM_VALUES)
      setShowForm(false)
      await loadPrestamos()
    } catch (err) {
      console.error(err)
      setFormError(err?.mensaje || 'No se pudo registrar el préstamo.')
    } finally {
      setCreating(false)
    }
  }

  const handleMarcarDevuelto = async (idPrestamo) => {
    if (!isAdmin) return
    const confirmar = window.confirm('¿Deseas marcar este préstamo como devuelto?')
    if (!confirmar) return

    setDevolviendoId(idPrestamo)
    try {
      await marcarPrestamoDevuelto(idPrestamo)
      await loadPrestamos()
    } catch (err) {
      console.error(err)
      alert('No se pudo marcar la devolución.')
    } finally {
      setDevolviendoId(null)
    }
  }

  const minDateValue = useMemo(() => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const year = today.getFullYear()
    const month = String(today.getMonth() + 1).padStart(2, '0')
    const day = String(today.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }, [])

  return (
    <section className="rounded-3xl border border-slate-100 bg-white p-6 shadow-sm">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h2 className="text-xl font-semibold text-slate-900">Préstamos y devoluciones</h2>
          <p className="mt-1 text-sm text-slate-500">{headingSubtitle}</p>
        </div>
        {isAdmin ? (
          <button
            onClick={toggleForm}
            className="rounded-2xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-800"
          >
            {showForm ? 'Cerrar formulario' : 'Registrar préstamo'}
          </button>
        ) : (
          <p className="text-sm text-slate-500">Solo los administradores pueden registrar o cerrar préstamos.</p>
        )}
      </div>

      {!isAdmin && (
        <div className="mt-4 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-600">
          {usuario?.nombre ? `${usuario.nombre}, aquí puedes revisar tus préstamos.` : 'Aquí puedes revisar tus préstamos activos.'}
        </div>
      )}

      {isAdmin && showForm && (
        <div className="mt-6 rounded-2xl border border-slate-200 p-6">
          <form className="space-y-4" onSubmit={handleSubmit}>
            {formError && (
              <p className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-600">{formError}</p>
            )}
            <div className="grid gap-4 md:grid-cols-3">
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="libroId">
                  ID del libro
                </label>
                <input
                  id="libroId"
                  name="libroId"
                  type="number"
                  min="1"
                  value={formValues.libroId}
                  onChange={handleChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                  placeholder="Ej. 12"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="usuarioEmail">
                  Correo del lector
                </label>
                <input
                  id="usuarioEmail"
                  name="usuarioEmail"
                  type="email"
                  value={formValues.usuarioEmail}
                  onChange={handleChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                  placeholder="usuario@upb.edu.co"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="fechaDevolucion">
                  Fecha estimada de devolución
                </label>
                <input
                  id="fechaDevolucion"
                  name="fechaDevolucion"
                  type="date"
                  min={minDateValue}
                  value={formValues.fechaDevolucion}
                  onChange={handleChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                />
              </div>
            </div>
            <div className="flex flex-wrap justify-end gap-3">
              <button
                type="button"
                onClick={toggleForm}
                className="rounded-2xl border border-slate-300 px-5 py-2 text-sm font-semibold text-slate-600 transition hover:border-slate-400 hover:text-slate-800"
              >
                Cancelar
              </button>
              <button
                type="submit"
                disabled={creating}
                className="rounded-2xl bg-slate-900 px-6 py-2 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-300"
              >
                {creating ? 'Registrando…' : 'Registrar préstamo'}
              </button>
            </div>
          </form>
        </div>
      )}

      {error && !loading && (
        <div className="mt-6 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-600">{error}</div>
      )}

      <div className="mt-6 overflow-hidden rounded-2xl border border-slate-200">
        {loading ? (
          <div className="px-6 py-8 text-center text-sm text-slate-500">Cargando préstamos…</div>
        ) : prestamos.length === 0 ? (
          <div className="px-6 py-8 text-center text-sm text-slate-500">{emptyStateText}</div>
        ) : (
          <table className="min-w-full divide-y divide-slate-200 text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-6 py-3">ID</th>
                {isAdmin && <th className="px-6 py-3">Usuario</th>}
                <th className="px-6 py-3">Libro</th>
                <th className="px-6 py-3">Fecha préstamo</th>
                <th className="px-6 py-3">Fecha devolución</th>
                <th className="px-6 py-3">Estado</th>
                {isAdmin && <th className="px-6 py-3 text-right">Acciones</th>}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 bg-white">
              {prestamos.map((prestamo) => {
                const estado = resolveEstado(prestamo)
                return (
                  <tr key={prestamo.idPrestamo}>
                    <td className="px-6 py-4 font-semibold text-slate-900">{prestamo.idPrestamo}</td>
                    {isAdmin && (
                      <td className="px-6 py-4 text-slate-700">
                        <div className="flex flex-col">
                          <span className="font-medium text-slate-900">{prestamo.usuarioNombre || 'Usuario'}</span>
                          <span className="text-xs text-slate-500">{prestamo.usuarioEmail}</span>
                        </div>
                      </td>
                    )}
                    <td className="px-6 py-4 text-slate-700">{prestamo.libroTitulo || '—'}</td>
                    <td className="px-6 py-4 text-slate-600">{formatDate(prestamo.fechaPrestamo)}</td>
                    <td className="px-6 py-4 text-slate-600">{formatDate(prestamo.fechaDevolucion)}</td>
                    <td className="px-6 py-4">
                      <span className={`rounded-full px-3 py-1 text-xs font-semibold ${estado.style}`}>{estado.label}</span>
                    </td>
                    {isAdmin && (
                      <td className="px-6 py-4 text-right">
                        {estado.label === 'Devuelto' ? (
                          <span className="text-xs font-semibold text-slate-400">Cerrado</span>
                        ) : (
                          <button
                            onClick={() => handleMarcarDevuelto(prestamo.idPrestamo)}
                            disabled={devolviendoId === prestamo.idPrestamo}
                            className="rounded-full border border-emerald-200 px-4 py-1 text-xs font-semibold text-emerald-700 transition hover:border-emerald-400 hover:text-emerald-900 disabled:cursor-not-allowed disabled:border-slate-200 disabled:text-slate-400"
                          >
                            {devolviendoId === prestamo.idPrestamo ? 'Actualizando…' : 'Marcar devuelto'}
                          </button>
                        )}
                      </td>
                    )}
                  </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </div>
    </section>
  )
}
