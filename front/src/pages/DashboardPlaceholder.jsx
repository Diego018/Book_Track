import { useNavigate } from 'react-router-dom'
import Button from '../components/Button'
import { useAuth } from '../context/AuthContext'

export default function DashboardPlaceholder() {
  const navigate = useNavigate()
  const { mensaje, logout } = useAuth()

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-950 px-4 py-10 text-white">
      <div className="w-full max-w-2xl rounded-3xl border border-white/10 bg-gradient-to-br from-white/10 via-white/5 to-transparent p-10 text-center shadow-card">
        <p className="text-sm uppercase tracking-[0.4em] text-white/60">Book_Track</p>
        <h1 className="mt-4 text-4xl font-semibold">Sesión activa ✅</h1>
        <p className="mt-4 text-lg text-white/80">
          {mensaje || 'Lograste iniciar sesión correctamente. Aquí pronto verás tu panel personalizado.'}
        </p>
        <Button className="mt-10" variant="secondary" onClick={handleLogout}>
          Cerrar sesión
        </Button>
      </div>
    </div>
  )
}
