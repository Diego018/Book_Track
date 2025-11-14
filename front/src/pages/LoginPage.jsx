import { useEffect, useMemo, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import Button from '../components/Button'
import TextInput from '../components/TextInput'
import { useAuth } from '../context/AuthContext'

const initialValues = {
  email: '',
  contrasena: '',
}

export default function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { login, isAuthenticated, isAuthenticating } = useAuth()
  const [values, setValues] = useState(initialValues)
  const [errors, setErrors] = useState({})
  const [apiError, setApiError] = useState('')
  const [successMessage, setSuccessMessage] = useState(location.state?.registeredMessage || '')

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/', { replace: true })
    }
  }, [isAuthenticated, navigate])

  useEffect(() => {
    if (location.state?.registeredMessage) {
      setSuccessMessage(location.state.registeredMessage)
      navigate(location.pathname, { replace: true, state: {} })
    }
  }, [location, navigate])

  const formInvalid = useMemo(() => {
    return !values.email || !values.contrasena
  }, [values])

  const validate = () => {
    const validationErrors = {}
    if (!values.email) {
      validationErrors.email = 'El correo es obligatorio'
    }
    if (!values.contrasena) {
      validationErrors.contrasena = 'La contraseña es obligatoria'
    }
    return validationErrors
  }

  const handleChange = (event) => {
    const { name, value } = event.target
    setValues((prev) => ({ ...prev, [name]: value }))
    setErrors((prev) => ({ ...prev, [name]: '' }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    const validationErrors = validate()
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors)
      return
    }
    try {
      setApiError('')
      await login(values)
      navigate('/', { replace: true })
    } catch (error) {
      const message = error?.mensaje || error?.message || 'Credenciales incorrectas'
      setApiError(message)
    }
  }

  return (
    <AuthLayout title="Inicia sesión" subtitle="Accede con tus credenciales institucionales">
      <form onSubmit={handleSubmit} className="flex flex-col gap-6">
        {successMessage && (
          <p className="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
            {successMessage}
          </p>
        )}
        <TextInput
          label="Correo institucional"
          name="email"
          type="email"
          value={values.email}
          onChange={handleChange}
          placeholder="nombre@booktrack.com"
          autoComplete="email"
          error={errors.email}
        />
        <TextInput
          label="Contraseña"
          name="contrasena"
          type="password"
          value={values.contrasena}
          onChange={handleChange}
          placeholder="••••••••"
          autoComplete="current-password"
          error={errors.contrasena}
          showPasswordToggle
        />
        {apiError && <p className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">{apiError}</p>}
        <Button type="submit" className="w-full" loading={isAuthenticating} disabled={formInvalid}>
          {isAuthenticating ? 'Iniciando…' : 'Iniciar sesión'}
        </Button>
        <p className="text-center text-sm text-slate-500">
          ¿No tienes cuenta?{' '}
          <Link to="/register" className="font-semibold text-brand-500 hover:text-brand-600">
            Regístrate
          </Link>
        </p>
      </form>
    </AuthLayout>
  )
}
