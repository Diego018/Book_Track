import Button from './Button'

const getInitial = (user) => {
  const base = user?.nombre || user?.email || 'U'
  return base.charAt(0).toUpperCase()
}

export default function Navbar({ user, onLogout }) {
  return (
    <header className="w-full border-b border-white/10 bg-slate-900/80 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4 text-white">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-white/50">Book_Track</p>
          <p className="text-lg font-semibold">Panel principal</p>
        </div>
        <div className="flex items-center gap-4 text-sm">
          <div className="text-right">
            <p className="font-medium">{user?.nombre || user?.email || 'Usuario'}</p>
            <p className="text-white/70">{user?.rol || 'USER'}</p>
          </div>
          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-white/10 font-semibold uppercase">
            {getInitial(user)}
          </div>
          <Button variant="secondary" onClick={onLogout}>
            Cerrar sesión
          </Button>
        </div>
      </div>
    </header>
  )
}
