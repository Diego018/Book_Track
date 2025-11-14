import { useCallback, useEffect, useMemo, useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import { actualizarEstadoReserva, cancelarReserva, crearReserva, getReservas } from '../../services/reservasService'

const INITIAL_FORM_VALUES = {
  libroId: '',
  usuarioEmail: '',
  fechaReserva: '',
}

const INITIAL_USER_FORM_VALUES = {
  libroId: '',
  fechaReserva: '',
}

const DATE_FORMATTER = new Intl.DateTimeFormat('es-CO', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
})

const parseLocalDate = (value) => {
  if (!value) return null
  if (value instanceof Date) return value
  const [year, month, day] = String(value).split('-').map(Number)
  if (!year || !month || !day) return null
  return new Date(year, month - 1, day)
}

const formatDate = (value) => {
  const parsed = parseLocalDate(value)
  if (!parsed || Number.isNaN(parsed.getTime())) return '—'
  return DATE_FORMATTER.format(parsed)
}

const ESTADO_META = {
  PENDIENTE: { label: 'Pendiente', style: 'bg-amber-100 text-amber-700' },
  PREPARADA: { label: 'Preparada', style: 'bg-blue-100 text-blue-700' },
  ENTREGADA: { label: 'Entregada', style: 'bg-emerald-100 text-emerald-700' },
  CANCELADA: { label: 'Cancelada', style: 'bg-rose-100 text-rose-700' },
  DEFAULT: { label: 'Sin estado', style: 'bg-slate-100 text-slate-600' },
}

const ACCIONES_POR_ESTADO = {
  PENDIENTE: [
    { estado: 'PREPARADA', label: 'Marcar preparada', style: 'border-blue-200 text-blue-700 hover:border-blue-400 hover:text-blue-900' },
    { estado: 'CANCELADA', label: 'Cancelar', style: 'border-rose-200 text-rose-700 hover:border-rose-400 hover:text-rose-900' },
  ],
  PREPARADA: [
    {
      estado: 'ENTREGADA',
      label: 'Marcar entregada',
      style: 'border-emerald-200 text-emerald-700 hover:border-emerald-400 hover:text-emerald-900',
    },
    { estado: 'CANCELADA', label: 'Cancelar', style: 'border-rose-200 text-rose-700 hover:border-rose-400 hover:text-rose-900' },
  ],
}

const puedeCancelarEstado = (estado) => {
  const key = estado?.toUpperCase?.()
  return key === 'PENDIENTE' || key === 'PREPARADA'
}

const resolveEstadoMeta = (estado) => {
  if (!estado) return ESTADO_META.DEFAULT
  const key = estado.toUpperCase()
  return ESTADO_META[key] || ESTADO_META.DEFAULT
}

const accionesDisponibles = (estado) => {
  if (!estado) return []
  const key = estado.toUpperCase()
  return ACCIONES_POR_ESTADO[key] || []
}

export default function ReservasPage() {
  const { isAdmin, usuario } = useAuth()
  const [reservas, setReservas] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [formValues, setFormValues] = useState(INITIAL_FORM_VALUES)
  const [formError, setFormError] = useState('')
  const [creating, setCreating] = useState(false)
  const [showForm, setShowForm] = useState(false)
  const [updatingId, setUpdatingId] = useState(null)
  const [userFormValues, setUserFormValues] = useState(INITIAL_USER_FORM_VALUES)
  const [userFormError, setUserFormError] = useState('')
  const [creatingUserReserva, setCreatingUserReserva] = useState(false)
  const [cancelandoId, setCancelandoId] = useState(null)

  const upsertReserva = useCallback((reservaActualizada) => {
    if (!reservaActualizada) return
    setReservas((prev) => {
      if (!Array.isArray(prev) || prev.length === 0) {
        return [reservaActualizada]
      }
      const existe = prev.some((reserva) => reserva.idReserva === reservaActualizada.idReserva)
      if (!existe) {
        return [reservaActualizada, ...prev]
      }
      return prev.map((reserva) => (reserva.idReserva === reservaActualizada.idReserva ? reservaActualizada : reserva))
    })
  }, [setReservas])

  const headingSubtitle = isAdmin
    ? 'Gestiona las reservas del catálogo y coordina su entrega al lector.'
    : 'Aquí puedes revisar el estado de las reservas que has realizado.'

  const emptyStateText = isAdmin ? 'No hay reservas registradas.' : 'Aún no tienes reservas registradas.'

  const loadReservas = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const data = await getReservas()
      setReservas(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error(err)
      const backendMessage = err?.message || err?.mensaje
      setError(backendMessage || 'No se pudieron cargar las reservas.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadReservas()
  }, [loadReservas])

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

  const handleAdminChange = (event) => {
    const { name, value } = event.target
    setFormValues((prev) => ({
      ...prev,
      [name]: value,
    }))
    setFormError('')
  }

  const validateAdminForm = () => {
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
    if (!formValues.fechaReserva) {
      return 'Selecciona la fecha en la que se registrará la reserva.'
    }
    const reservaDate = parseLocalDate(formValues.fechaReserva)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    if (!reservaDate || reservaDate.getTime() < today.getTime()) {
      return 'La fecha de reserva no puede estar en el pasado.'
    }
    return ''
  }

  const handleAdminSubmit = async (event) => {
    event.preventDefault()
    if (!isAdmin) return
    const validationMessage = validateAdminForm()
    if (validationMessage) {
      setFormError(validationMessage)
      return
    }

    const payload = {
      libroId: Number(formValues.libroId),
      usuarioEmail: formValues.usuarioEmail.trim(),
      fechaReserva: formValues.fechaReserva,
    }

    setCreating(true)
    setFormError('')
    try {
      const nuevaReserva = await crearReserva(payload, { admin: true })
      setFormValues(INITIAL_FORM_VALUES)
      setShowForm(false)
      upsertReserva(nuevaReserva)
    } catch (err) {
      console.error(err)
      setFormError(err?.mensaje || 'No se pudo registrar la reserva.')
    } finally {
      setCreating(false)
    }
  }

  const handleUserChange = (event) => {
    const { name, value } = event.target
    setUserFormValues((prev) => ({
      ...prev,
      [name]: value,
    }))
    setUserFormError('')
  }

  const validateUserForm = () => {
    if (!userFormValues.libroId.trim()) {
      return 'El ID del libro es obligatorio.'
    }
    const libroIdNumber = Number(userFormValues.libroId)
    if (Number.isNaN(libroIdNumber) || libroIdNumber <= 0) {
      return 'Ingresa un ID de libro válido.'
    }
    if (!userFormValues.fechaReserva) {
      return 'Selecciona la fecha en la que se registrará la reserva.'
    }
    const reservaDate = parseLocalDate(userFormValues.fechaReserva)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    if (!reservaDate || reservaDate.getTime() < today.getTime()) {
      return 'La fecha de reserva no puede estar en el pasado.'
    }
    return ''
  }

  const handleUserSubmit = async (event) => {
    event.preventDefault()
    if (isAdmin) return
    const validationMessage = validateUserForm()
    if (validationMessage) {
      setUserFormError(validationMessage)
      return
    }

    const payload = {
      libroId: Number(userFormValues.libroId),
      fechaReserva: userFormValues.fechaReserva,
    }

    setCreatingUserReserva(true)
    setUserFormError('')
    try {
      const nuevaReserva = await crearReserva(payload)
      setUserFormValues(INITIAL_USER_FORM_VALUES)
      upsertReserva(nuevaReserva)
    } catch (err) {
      console.error(err)
      setUserFormError(err?.mensaje || 'No se pudo registrar la reserva.')
    } finally {
      setCreatingUserReserva(false)
    }
  }

  const handleActualizarEstado = async (reservaId, estadoObjetivo) => {
    if (!isAdmin) return
    const accion = estadoObjetivo === 'CANCELADA' ? 'cancelar' : estadoObjetivo === 'ENTREGADA' ? 'marcar como entregada' : 'actualizar'
    const confirmar = window.confirm(`¿Seguro que deseas ${accion} esta reserva?`)
    if (!confirmar) return

    setUpdatingId(reservaId)
    try {
      const reservaActualizada = await actualizarEstadoReserva(reservaId, estadoObjetivo)
      upsertReserva(reservaActualizada)
    } catch (err) {
      console.error(err)
      alert(err?.mensaje || 'No se pudo actualizar el estado de la reserva.')
    } finally {
      setUpdatingId(null)
    }
  }

  const handleCancelarReserva = async (reservaId) => {
    if (isAdmin) return
    const confirmar = window.confirm('¿Deseas cancelar esta reserva?')
    if (!confirmar) return

    setCancelandoId(reservaId)
    try {
      const reservaActualizada = await cancelarReserva(reservaId)
      upsertReserva(reservaActualizada)
    } catch (err) {
      console.error(err)
      alert(err?.mensaje || 'No se pudo cancelar la reserva.')
    } finally {
      setCancelandoId(null)
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
          <h2 className="text-xl font-semibold text-slate-900">Reservas</h2>
          <p className="mt-1 text-sm text-slate-500">{headingSubtitle}</p>
        </div>
        {isAdmin ? (
          <button
            onClick={toggleForm}
            className="rounded-2xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-800"
          >
            {showForm ? 'Cerrar formulario' : 'Registrar reserva'}
          </button>
        ) : (
          <p className="text-sm text-slate-500">Registra tus reservas mediante el formulario inferior.</p>
        )}
      </div>

      {!isAdmin && (
        <div className="mt-4 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-600">
          {usuario?.nombre
            ? `${usuario.nombre}, aquí verás tus reservas activas y su estado.`
            : 'Aquí verás las reservas que realices en la biblioteca.'}
        </div>
      )}

      {!isAdmin && (
        <div className="mt-6 rounded-2xl border border-slate-200 p-6">
          <h3 className="text-base font-semibold text-slate-900">Crear una nueva reserva</h3>
          <p className="mt-1 text-sm text-slate-500">Ingresa el ID del libro y la fecha en la que deseas recogerlo.</p>
          <form className="mt-4 space-y-4" onSubmit={handleUserSubmit}>
            {userFormError && (
              <p className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-600">{userFormError}</p>
            )}
            <div className="grid gap-4 md:grid-cols-2">
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="userLibroId">
                  ID del libro
                </label>
                <input
                  id="userLibroId"
                  name="libroId"
                  type="number"
                  min="1"
                  value={userFormValues.libroId}
                  onChange={handleUserChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                  placeholder="Ej. 42"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="userFechaReserva">
                  Fecha de reserva
                </label>
                <input
                  id="userFechaReserva"
                  name="fechaReserva"
                  type="date"
                  min={minDateValue}
                  value={userFormValues.fechaReserva}
                  onChange={handleUserChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                />
              </div>
            </div>
            <div className="flex justify-end">
              <button
                type="submit"
                disabled={creatingUserReserva}
                className="rounded-2xl bg-slate-900 px-6 py-2 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-300"
              >
                {creatingUserReserva ? 'Registrando…' : 'Reservar libro'}
              </button>
            </div>
          </form>
        </div>
      )}

      {isAdmin && showForm && (
        <div className="mt-6 rounded-2xl border border-slate-200 p-6">
          <form className="space-y-4" onSubmit={handleAdminSubmit}>
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
                  onChange={handleAdminChange}
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
                  onChange={handleAdminChange}
                  className="mt-1 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 focus:border-slate-900 focus:outline-none focus:ring-2 focus:ring-slate-100"
                  placeholder="usuario@upb.edu.co"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-slate-600" htmlFor="fechaReserva">
                  Fecha de reserva
                </label>
                <input
                  id="fechaReserva"
                  name="fechaReserva"
                  type="date"
                  min={minDateValue}
                  value={formValues.fechaReserva}
                  onChange={handleAdminChange}
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
                {creating ? 'Registrando…' : 'Registrar reserva'}
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
          <div className="px-6 py-8 text-center text-sm text-slate-500">Cargando reservas…</div>
        ) : reservas.length === 0 ? (
          <div className="px-6 py-8 text-center text-sm text-slate-500">{emptyStateText}</div>
        ) : (
          <table className="min-w-full divide-y divide-slate-200 text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-6 py-3">ID</th>
                {isAdmin && <th className="px-6 py-3">Usuario</th>}
                <th className="px-6 py-3">Libro</th>
                <th className="px-6 py-3">Fecha</th>
                <th className="px-6 py-3">Estado</th>
                <th className="px-6 py-3 text-right">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 bg-white">
              {reservas.map((reserva) => {
                const estado = resolveEstadoMeta(reserva.estado)
                const acciones = isAdmin ? accionesDisponibles(reserva.estado) : []
                const cancelable = !isAdmin && puedeCancelarEstado(reserva.estado)
                return (
                  <tr key={reserva.idReserva}>
                    <td className="px-6 py-4 font-semibold text-slate-900">{reserva.idReserva}</td>
                    {isAdmin && (
                      <td className="px-6 py-4 text-slate-700">
                        <div className="flex flex-col">
                          <span className="font-medium text-slate-900">{reserva.usuarioNombre || 'Usuario'}</span>
                          <span className="text-xs text-slate-500">{reserva.usuarioEmail}</span>
                        </div>
                      </td>
                    )}
                    <td className="px-6 py-4 text-slate-700">{reserva.libroTitulo || '—'}</td>
                    <td className="px-6 py-4 text-slate-600">{formatDate(reserva.fechaReserva)}</td>
                    <td className="px-6 py-4">
                      <span className={`rounded-full px-3 py-1 text-xs font-semibold ${estado.style}`}>{estado.label}</span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      {isAdmin ? (
                        acciones.length === 0 ? (
                          <span className="text-xs font-semibold text-slate-400">Sin acciones</span>
                        ) : (
                          <div className="flex flex-wrap justify-end gap-2">
                            {acciones.map((accion) => (
                              <button
                                key={accion.estado}
                                onClick={() => handleActualizarEstado(reserva.idReserva, accion.estado)}
                                disabled={updatingId === reserva.idReserva}
                                className={`rounded-full border px-4 py-1 text-xs font-semibold transition disabled:cursor-not-allowed disabled:border-slate-200 disabled:text-slate-400 ${accion.style}`}
                              >
                                {updatingId === reserva.idReserva ? 'Actualizando…' : accion.label}
                              </button>
                            ))}
                          </div>
                        )
                      ) : cancelable ? (
                        <button
                          onClick={() => handleCancelarReserva(reserva.idReserva)}
                          disabled={cancelandoId === reserva.idReserva}
                          className="rounded-full border border-rose-200 px-4 py-1 text-xs font-semibold text-rose-700 transition hover:border-rose-400 hover:text-rose-900 disabled:cursor-not-allowed disabled:border-slate-200 disabled:text-slate-400"
                        >
                          {cancelandoId === reserva.idReserva ? 'Cancelando…' : 'Cancelar'}
                        </button>
                      ) : (
                        <span className="text-xs font-semibold text-slate-400">Sin acciones</span>
                      )}
                    </td>
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
