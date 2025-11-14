import { createContext, useCallback, useContext, useMemo, useState } from 'react'
import { login as authLogin } from '../services/authService'
import { AUTH_STORAGE_KEY } from '../services/storageKeys'

const defaultState = {
  token: null,
  usuario: null,
  mensaje: '',
}

const AuthContext = createContext({
  token: null,
  usuario: null,
  mensaje: '',
  isAuthenticated: false,
  isAuthenticating: false,
  isAdmin: false,
  login: async () => {},
  logout: () => {},
})

const readStoredState = () => {
  if (typeof window === 'undefined') return defaultState
  try {
    const raw = window.localStorage.getItem(AUTH_STORAGE_KEY)
    return raw ? JSON.parse(raw) : defaultState
  } catch (error) {
    console.warn('No se pudo leer el estado del token', error)
    return defaultState
  }
}

const persistState = (state) => {
  if (typeof window === 'undefined') return
  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(state))
}

const clearState = () => {
  if (typeof window === 'undefined') return
  window.localStorage.removeItem(AUTH_STORAGE_KEY)
}

export function AuthProvider({ children }) {
  const [authState, setAuthState] = useState(() => readStoredState())
  const [isAuthenticating, setIsAuthenticating] = useState(false)

  const login = useCallback(async (credentials) => {
    setIsAuthenticating(true)
    try {
      const result = await authLogin(credentials)
      const nextState = {
        token: result.token,
        usuario: result.usuario ?? null,
        mensaje: result.mensaje,
      }
      setAuthState(nextState)
      persistState(nextState)
      return result
    } finally {
      setIsAuthenticating(false)
    }
  }, [])

  const logout = useCallback(() => {
    setAuthState(defaultState)
    clearState()
  }, [])

  const value = useMemo(
    () => ({
      token: authState.token,
      usuario: authState.usuario,
      mensaje: authState.mensaje,
      isAuthenticated: Boolean(authState.token),
      isAdmin: authState?.usuario?.rol === 'ADMIN',
      isAuthenticating,
      login,
      logout,
    }),
    [authState, isAuthenticating, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider')
  }
  return context
}
