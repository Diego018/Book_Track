import { apiClient } from './apiClient'

export async function login({ email, contrasena }) {
  if (!email || !contrasena) {
    throw new Error('Debes proporcionar correo y contraseña')
  }

  const response = await apiClient('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, contrasena }),
  })

  if (!response?.token) {
    throw new Error('La respuesta del servidor no contiene un token válido')
  }

  return {
    token: response.token,
    mensaje: response.mensaje ?? 'Login exitoso',
    usuario: response.usuario,
  }
}

export async function register({ nombre, email, contrasena }) {
  if (!nombre || !email || !contrasena) {
    throw new Error('Todos los campos obligatorios deben completarse')
  }

  const response = await apiClient('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ nombre, email, contrasena }),
  })

  if (!response?.token) {
    throw new Error('La respuesta del servidor no contiene un token válido')
  }

  return {
    token: response.token,
    mensaje: response.mensaje ?? 'Registro exitoso',
    usuario: response.usuario,
  }
}
