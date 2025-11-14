import { useState } from 'react'
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const navItems = [
  { to: '/', label: 'Inicio' },
  { to: '/libros', label: 'Libros' },
  { to: '/prestamos', label: 'Préstamos' },
  { to: '/reservas', label: 'Reservas' },
  { to: '/actividad', label: 'Actividad' },
]

export default function DashboardLayout() {
  const { usuario, isAdmin, logout } = useAuth()
  const navigate = useNavigate()
  const [sidebarOpen, setSidebarOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="min-h-screen bg-slate-50">
      <div className="lg:hidden">
        <button
          className="fixed bottom-6 right-6 z-20 rounded-full bg-slate-900 p-4 text-white shadow-lg"
          onClick={() => setSidebarOpen((prev) => !prev)}
          aria-label="Abrir menú"
        >
          {sidebarOpen ? (
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M18 6 6 18M6 6l12 12" />
            </svg>
          ) : (
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M3 6h18M3 12h18M3 18h18" />
            </svg>
          )}
        </button>
      </div>

      <aside
        className={`fixed inset-y-0 left-0 z-10 w-72 transform bg-white shadow-xl transition-transform duration-300 ease-in-out lg:translate-x-0 ${
          sidebarOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex h-full flex-col border-r border-slate-100">
          <div className="border-b border-slate-100 px-6 py-5">
            <Link to="/" className="text-2xl font-extrabold tracking-wide text-slate-900">
              BOOK_TRACK
            </Link>
            <p className="mt-1 text-sm text-slate-500">
              Hola, {usuario?.nombre ?? 'usuario'} 👋
            </p>
            {usuario?.rol && (
              <span className={`mt-2 inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold ${isAdmin ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-600'}`}>
                Rol: {usuario.rol}
              </span>
            )}
          </div>
          <nav className="flex-1 space-y-1 px-4 py-6">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.to === '/'}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-medium transition hover:bg-slate-100 ${
                    isActive ? 'bg-slate-900 text-white' : 'text-slate-600'
                  }`
                }
                onClick={() => setSidebarOpen(false)}
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
          <div className="border-t border-slate-100 px-6 py-5">
            <button
              onClick={handleLogout}
              className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm font-semibold text-slate-700 transition hover:border-slate-900 hover:text-slate-900"
            >
              Cerrar sesión
            </button>
          </div>
        </div>
      </aside>

      <div className="lg:pl-72">
        <header className="sticky top-0 z-0 border-b border-slate-200 bg-white/80 backdrop-blur supports-[backdrop-filter]:backdrop-blur">
          <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
            <div>
              <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Panel principal</p>
              <h1 className="text-2xl font-semibold text-slate-900">Resumen</h1>
            </div>
            <div className="hidden items-center gap-3 lg:flex">
              <div className="text-right">
                <p className="text-sm font-semibold text-slate-900">{usuario?.nombre ?? 'Usuario'}</p>
                <p className="text-xs uppercase tracking-wider text-slate-400">{usuario?.rol ?? 'Sin rol'}</p>
              </div>
              <button
                onClick={handleLogout}
                className="rounded-2xl border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:border-slate-900 hover:text-slate-900"
              >
                Cerrar sesión
              </button>
            </div>
          </div>
        </header>

        <main className="mx-auto max-w-6xl px-6 py-8">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
