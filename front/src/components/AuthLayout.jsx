export default function AuthLayout({ title, subtitle, children }) {
  return (
    <div className="min-h-screen w-full bg-slate-950 bg-auth-gradient px-4 py-10 text-slate-900">
      <div className="mx-auto flex max-w-6xl flex-col items-center justify-center gap-12 lg:flex-row">
        <div className="max-w-lg text-center text-white lg:text-left">
          <p className="text-sm uppercase tracking-[0.3em] text-white/70">Book_Track</p>
          <h1 className="mt-4 text-4xl font-semibold leading-snug text-white lg:text-5xl">
            Gestiona tu biblioteca<br className="hidden lg:block" /> con un flujo seguro
          </h1>
          <p className="mt-6 text-lg text-white/80">
            Mantén el control de préstamos, reservas y actividades. Inicia sesión para acceder a tu panel personalizado.
          </p>
        </div>
        <div className="w-full max-w-md rounded-3xl border border-white/20 bg-white/90 p-8 shadow-card backdrop-blur">
          <div className="mb-8 text-center">
            <h2 className="text-2xl font-semibold text-slate-900">{title}</h2>
            <p className="mt-2 text-sm text-slate-500">{subtitle}</p>
          </div>
          {children}
        </div>
      </div>
    </div>
  )
}
