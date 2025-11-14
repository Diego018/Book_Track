import { useId, useState } from 'react'

const EyeIcon = ({ open = false }) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="1.5"
    className="h-5 w-5 text-slate-500"
  >
    {open ? (
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M2.036 12.322c1.83-4.01 5.33-6.737 9.584-6.737 4.255 0 7.755 2.727 9.584 6.737-1.829 4.01-5.329 6.737-9.584 6.737-4.254 0-7.754-2.727-9.584-6.737Z"
      />
    ) : (
      <>
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          d="M3.98 8.223A10.477 10.477 0 0 0 1.934 12C3.226 15.338 6.795 18 11.62 18c1.575 0 3.057-.311 4.385-.868"
        />
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          d="M6.228 6.228A10.451 10.451 0 0 1 11.62 5c4.825 0 8.393 2.662 9.685 6-.121.32-.262.63-.421.93"
        />
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          d="M3 3l18 18"
        />
      </>
    )}
    <circle cx="11.62" cy="12" r="2.5" />
  </svg>
)

export default function TextInput({
  label,
  name,
  type = 'text',
  value,
  onChange,
  error,
  placeholder,
  autoComplete,
  icon,
  showPasswordToggle = false,
  ...rest
}) {
  const inputId = useId()
  const [showPassword, setShowPassword] = useState(false)
  const resolvedType = showPasswordToggle ? (showPassword ? 'text' : 'password') : type

  const renderIcon = typeof icon === 'function' ? icon() : icon

  return (
    <div className="flex flex-col gap-1">
      {label && (
        <label htmlFor={inputId} className="text-sm font-medium text-slate-600">
          {label}
        </label>
      )}
      <div className="relative">
        {renderIcon && <span className="pointer-events-none absolute inset-y-0 left-3 flex items-center text-slate-400">{renderIcon}</span>}
        <input
          id={inputId}
          name={name}
          type={resolvedType}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          autoComplete={autoComplete}
          className={`w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-slate-900 shadow-sm transition focus:border-brand-400 focus:outline-none focus:ring-2 focus:ring-brand-200 ${renderIcon ? 'pl-11' : ''} ${error ? 'border-red-400 focus:ring-red-100' : ''}`}
          {...rest}
        />
        {showPasswordToggle && (
          <button
            type="button"
            onClick={() => setShowPassword((prev) => !prev)}
            className="absolute inset-y-0 right-3 flex items-center text-slate-500"
            aria-label={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}
          >
            <EyeIcon open={showPassword} />
          </button>
        )}
      </div>
      {error && <p className="text-sm text-red-500">{error}</p>}
    </div>
  )
}
