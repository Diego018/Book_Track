import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import Button from '../components/Button'
import TextInput from '../components/TextInput'
import { register as registerUser } from '../services/authService'

const initialValues = {
  nombre: '',
  email: '',
  contrasena: '',
}

export default function RegisterPage() {
  const navigate = useNavigate()
  const [values, setValues] = useState(initialValues)
  const [errors, setErrors] = useState({})
  const [apiError, setApiError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const formInvalid = useMemo(() => {
    return !values.nombre || !values.email || !values.contrasena
  }, [values])

  const validate = () => {
    const newErrors = {}
    if (!values.nombre.trim()) {
      newErrors.nombre = 'El nombre es obligatorio'
    }
    if (!values.email.trim()) {
      newErrors.email = 'El correo es obligatorio'
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email)) {
      newErrors.email = 'El correo no es válido'
    }
    if (!values.contrasena) {
      newErrors.contrasena = 'La contraseña es obligatoria'
    } else if (values.contrasena.length < 6) {
      newErrors.contrasena = 'La contraseña debe tener al menos 6 caracteres'
    }
    return newErrors
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
    setIsSubmitting(true)
    setApiError('')
    try {
      const response = await registerUser(values)
      navigate('/login', {
        replace: true,
        state: {
          registeredMessage: response.mensaje || 'Registro exitoso, inicia sesión',
        },
      })
    } catch (error) {
      const message = error?.mensaje || error?.message || 'No se pudo completar el registro'
      setApiError(message)
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <AuthLayout title="Crear cuenta" subtitle="Regístrate para gestionar Book_Track">
      <form onSubmit={handleSubmit} className="flex flex-col gap-6">
        <TextInput
          label="Nombre completo"
          name="nombre"
          value={values.nombre}
          onChange={handleChange}
          placeholder="Ana Librera"
          autoComplete="name"
          error={errors.nombre}
        />
        <TextInput
          label="Correo institucional"
          name="email"
          type="email"
          value={values.email}
          onChange={handleChange}
          placeholder="ana@booktrack.com"
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
          autoComplete="new-password"
          error={errors.contrasena}
          showPasswordToggle
        />
        {apiError && <p className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">{apiError}</p>}
        <Button type="submit" className="w-full" loading={isSubmitting} disabled={formInvalid || isSubmitting}>
          {isSubmitting ? 'Creando cuenta…' : 'Registrarse'}
        </Button>
        <p className="text-center text-sm text-slate-500">
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" className="font-semibold text-brand-500 hover:text-brand-600">
            Inicia sesión
          </Link>
        </p>
      </form>
    </AuthLayout>
  )
}
