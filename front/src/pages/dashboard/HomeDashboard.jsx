import { useCallback, useEffect, useMemo, useState } from 'react'

import { fetchDashboardSummary } from '../../services/dashboardService'

const formatter = new Intl.NumberFormat('es-ES')

const placeholderStats = [
  { label: 'Libros totales', value: '—', helper: 'Esperando datos' },
  { label: 'Préstamos activos', value: '—', helper: 'Esperando datos' },
  { label: 'Reservas pendientes', value: '—', helper: 'Esperando datos' },
  { label: 'Usuarios activos', value: '—', helper: 'Esperando datos' },
]

function formatNumber(value) {
  return formatter.format(value ?? 0)
}

function formatDate(dateString) {
  if (!dateString) return '—'
  return new Date(dateString).toLocaleDateString('es-ES', { day: '2-digit', month: 'short' })
}

function formatDateTime(isoString) {
  if (!isoString) return '—'
  return new Date(isoString).toLocaleString('es-ES', {
    day: '2-digit',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export default function HomeDashboard() {
  const [summary, setSummary] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const loadSummary = useCallback(async () => {
    setLoading(true)
    try {
      const data = await fetchDashboardSummary()
      setSummary(data)
      setError(null)
    } catch (err) {
      const message = err?.status === 403
        ? 'No tienes permisos para ver este resumen.'
        : err?.message || err?.detail || 'No se pudo cargar el dashboard'
      setError(message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadSummary()
  }, [loadSummary])

  const stats = useMemo(() => {
    if (!summary) return placeholderStats
    return [
      {
        label: 'Libros totales',
        value: formatNumber(summary.totalLibros),
        helper: `Disponibles: ${formatNumber(summary.librosDisponibles)}`,
      },
      {
        label: 'Préstamos activos',
        value: formatNumber(summary.prestamosActivos),
        helper: `${formatNumber(summary.prestamosVencidos)} vencidos`,
      },
      {
        label: 'Reservas pendientes',
        value: formatNumber(summary.reservasPendientes),
        helper: `${formatNumber(summary.reservasPreparadas)} preparadas`,
      },
      {
        label: 'Usuarios activos',
        value: formatNumber(summary.usuariosRegistrados),
        helper: 'Registrados en total',
      },
    ]
  }, [summary])

  const reservations = summary?.reservasRecientes ?? []
  const actividad = summary?.actividadReciente ?? []

  return (
    <div className="space-y-8">
      {error && (
        <div className="rounded-3xl border border-rose-100 bg-rose-50 p-4 text-sm text-rose-700">
          <div className="flex items-center justify-between">
            <p>{error}</p>
            <button
              type="button"
              onClick={loadSummary}
              className="text-xs font-semibold text-rose-700 underline"
            >
              Reintentar
            </button>
          </div>
        </div>
      )}

      <section className="grid gap-6 md:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat) => (
          <article key={stat.label} className="rounded-3xl border border-slate-100 bg-white p-6 shadow-sm">
            <p className="text-sm font-medium uppercase tracking-wide text-slate-400">{stat.label}</p>
            <p className="mt-3 text-4xl font-semibold text-slate-900">{stat.value}</p>
            <p className="mt-2 text-sm text-emerald-600">{stat.helper}</p>
          </article>
        ))}
      </section>

      <section className="grid gap-6 lg:grid-cols-2">
        <div className="rounded-3xl border border-slate-100 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-slate-900">Reservas recientes</h2>
            <span className="text-sm font-semibold text-brand-600">
              {loading ? 'Actualizando…' : `${reservations.length} registros`}
            </span>
          </div>
          <div className="mt-4 divide-y divide-slate-100">
            {loading && <p className="py-4 text-sm text-slate-400">Cargando reservas…</p>}
            {!loading && reservations.length === 0 && (
              <p className="py-4 text-sm text-slate-400">No hay reservas recientes.</p>
            )}
            {!loading &&
              reservations.map((item) => (
                <div key={item.id} className="flex items-center justify-between py-4">
                  <div>
                    <p className="font-medium text-slate-900">
                      {item.usuarioNombre || item.usuarioEmail || 'Usuario desconocido'}
                    </p>
                    <p className="text-sm text-slate-500">{item.libroTitulo || 'Libro sin título'}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs uppercase tracking-wide text-slate-400">ID {item.id}</p>
                    <p className="text-sm font-semibold text-slate-700">{formatDate(item.fechaReserva)}</p>
                  </div>
                </div>
              ))}
          </div>
        </div>

        <div className="rounded-3xl border border-slate-100 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-slate-900">Actividad reciente</h2>
            <span className="text-sm font-semibold text-brand-600">
              {loading ? 'Actualizando…' : `${actividad.length} movimientos`}
            </span>
          </div>
          <div className="mt-4 space-y-4">
            {loading && <p className="text-sm text-slate-400">Cargando actividad…</p>}
            {!loading && actividad.length === 0 && (
              <p className="text-sm text-slate-400">Todavía no hay actividad registrada.</p>
            )}
            {!loading &&
              actividad.map((item) => (
                <div key={item.id} className="rounded-2xl border border-slate-100 p-4">
                  <p className="font-medium text-slate-900">
                    {item.usuarioNombre || item.usuarioEmail || 'Usuario desconocido'}
                  </p>
                  <p className="text-sm text-slate-500">{item.accion}</p>
                  <p className="text-xs text-slate-400">{formatDateTime(item.fechaHora)}</p>
                </div>
              ))}
          </div>
        </div>
      </section>
    </div>
  )
}
