import { Navigate, Route, Routes } from 'react-router-dom'
import DashboardLayout from '../layouts/DashboardLayout'
import { useAuth } from '../context/AuthContext'
import LoginPage from '../pages/LoginPage'
import RegisterPage from '../pages/RegisterPage'
import ActividadPage from '../pages/dashboard/ActividadPage'
import HomeDashboard from '../pages/dashboard/HomeDashboard'
import LibrosPage from '../pages/dashboard/LibrosPage'
import PrestamosPage from '../pages/dashboard/PrestamosPage'
import ReservasPage from '../pages/dashboard/ReservasPage'

function PrivateRoute({ children }) {
  const { isAuthenticated } = useAuth()
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }
  return children
}

function AdminRoute({ children }) {
  const { isAdmin } = useAuth()
  if (!isAdmin) {
    return <Navigate to="/" replace />
  }
  return children
}

export default function AppRouter() {
  const { isAuthenticated } = useAuth()

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/"
        element={
          <PrivateRoute>
            <DashboardLayout />
          </PrivateRoute>
        }
      >
        <Route index element={<HomeDashboard />} />
        <Route path="libros" element={<LibrosPage />} />
        <Route path="prestamos" element={<PrestamosPage />} />
        <Route path="reservas" element={<ReservasPage />} />
        <Route path="actividad" element={<ActividadPage />} />
      </Route>
      <Route path="*" element={<Navigate to={isAuthenticated ? '/' : '/login'} replace />} />
    </Routes>
  )
}
