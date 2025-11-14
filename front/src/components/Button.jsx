const baseClasses = 'inline-flex items-center justify-center gap-2 rounded-2xl border border-transparent px-5 py-3 text-sm font-semibold transition focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 disabled:cursor-not-allowed disabled:opacity-60'

const variants = {
  primary:
    'bg-brand-500 text-white shadow-lg shadow-brand-500/30 hover:bg-brand-600 focus-visible:outline-brand-500',
  secondary:
    'bg-white text-brand-600 border border-brand-100 hover:border-brand-300 hover:bg-brand-50 focus-visible:outline-brand-400',
}

const Spinner = ({ variant }) => {
  const borderColor = variant === 'secondary' ? 'border-brand-500' : 'border-white'
  return (
    <span className={`h-4 w-4 animate-spin rounded-full border-2 ${borderColor} border-t-transparent`} aria-hidden="true" />
  )
}

export default function Button({ type = 'button', variant = 'primary', loading = false, disabled = false, children, className = '', ...rest }) {
  const classes = `${baseClasses} ${variants[variant]} ${className}`
  const isDisabled = disabled || loading

  return (
    <button type={type} className={classes} disabled={isDisabled} {...rest}>
      {loading && <Spinner variant={variant} />}
      <span>{loading ? 'Procesando…' : children}</span>
    </button>
  )
}
