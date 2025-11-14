import { useEffect, useState } from 'react'
import { downloadActividadLog, getActividad } from '../../services/actividadService'

const DATE_FORMATTER = new Intl.DateTimeFormat('es-CO', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
})

const formatFecha = (value) => {
  if (!value) return '—'
  const fecha = new Date(value)
  if (Number.isNaN(fecha.getTime())) return '—'
  return DATE_FORMATTER.format(fecha)
}

export default function ActividadPage() {
  const [actividad, setActividad] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [statusMessage, setStatusMessage] = useState('')

  useEffect(() => {
    const fetchActividad = async () => {
      setLoading(true)
      setError('')
      try {
        const data = await getActividad()
        setActividad(Array.isArray(data) ? data : [])
        setStatusMessage('')
      } catch (err) {
        console.error(err)
        const backendMessage = err?.message || err?.mensaje
        setError(backendMessage || 'No se pudo cargar la actividad reciente.')
      } finally {
        setLoading(false)
      }
    }

    fetchActividad()
  }, [])

  const handleDownloadLog = async () => {
    setError('')
    setStatusMessage('')
    try {
      const { blob, filename } = await downloadActividadLog()
      const url = URL.createObjectURL(blob)
      const anchor = document.createElement('a')
      anchor.href = url
      anchor.download = filename
      anchor.click()
      URL.revokeObjectURL(url)
      setStatusMessage('Descarga iniciada correctamente.')
    } catch (err) {
      console.error(err)
      const backendMessage = err?.message || err?.mensaje
      setError(backendMessage || 'No se pudo descargar el log.')
    }
  }

  return (
    <section className="rounded-3xl border border-slate-100 bg-white p-6 shadow-sm">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-semibold text-slate-900">Actividad del sistema</h2>
          <p className="mt-1 text-sm text-slate-500">Revisa los movimientos más recientes registrados por la biblioteca.</p>
        </div>
        <button
          className="rounded-2xl border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700"
          type="button"
          onClick={handleDownloadLog}
        >
          Descargar log
        </button>
      </div>

      {statusMessage && !loading && !error && (
        <div className="mt-6 rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
          {statusMessage}
        </div>
      )}

      {error && !loading && (
        <div className="mt-6 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-600">{error}</div>
      )}

      <div className="mt-6 divide-y divide-slate-100">
        {loading ? (
          <div className="py-8 text-center text-sm text-slate-500">Cargando actividad…</div>
        ) : actividad.length === 0 ? (
          <div className="py-8 text-center text-sm text-slate-500">No se registran movimientos recientes.</div>
        ) : (
          actividad.map((item) => (
            <article key={item.id} className="flex flex-wrap items-center justify-between gap-4 py-4">
              <div>
                <p className="font-medium text-slate-900">{item.usuarioNombre || 'Sistema'}</p>
                <p className="text-sm text-slate-500">{item.accion || 'Actividad registrada'}</p>
                {item.libroTitulo && (
                  <p className="text-xs text-slate-400">Libro: {item.libroTitulo}</p>
                )}
              </div>
              <p className="text-sm font-semibold text-slate-600">{formatFecha(item.fechaHora)}</p>
            </article>
          ))
        )}
      </div>
    </section>
  )
}
