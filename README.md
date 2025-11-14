# 📘 BookTrack — Módulo de Catálogo, Préstamos y Reservas

Este módulo agrega al proyecto **BookTrack** la funcionalidad completa de:

* 📚 Catálogo de libros
* 🤝 Préstamos
* 🔔 Reservas
* 👤 Selección de usuario (temporal, sin autenticación real)

Fue desarrollado **antes** de que se implementara el sistema de autenticación y roles, por lo que utiliza un `UserContext` con usuarios simulados para permitir la interacción.

---

## 🧩 Estructura del Módulo

El módulo introduce los siguientes archivos principales:

### 📂 **Contexto (Simulación de usuarios)**

`src/context/UserContext.jsx`
Permite seleccionar un usuario desde un menú y usar sus datos para las operaciones del catálogo.

---

### 📁 **Componentes del catálogo**

| Archivo            | Descripción                                                      |
| ------------------ | ---------------------------------------------------------------- |
| `BookList.jsx`     | Lista de libros obtenidos desde el backend                       |
| `BookItem.jsx`     | Tarjeta individual de cada libro                                 |
| `Header.jsx`       | Muestra el selector de usuarios y accesos a préstamos / reservas |
| `Footer.jsx`       | Información del backend conectado                                |
| `MisPrestamos.jsx` | Vista con los préstamos del usuario, permite devolver            |
| `MisReservas.jsx`  | Vista con las reservas del usuario, permite aceptar/rechazar     |

---

## 🌐 **Servicios**

| Archivo              | Función                                    |
| -------------------- | ------------------------------------------ |
| `PrestamoService.js` | Prestar libro                              |
| `ReservaService.js`  | Obtener reservas, aceptarlas o rechazarlas |

Todos se conectan al backend:

```
http://localhost:8080
```

---

## 🔧 Instalación e Integración

### 1️⃣ Envolver la app con `UserProvider`

En `main.jsx` o `App.jsx`:

```jsx
import { UserProvider } from './context/UserContext'

root.render(
  <UserProvider>
    <App />
  </UserProvider>
)
```

Esto habilita el selector de usuarios y el consumo del contexto en todo el frontend.

---

### 2️⃣ Integrar las vistas en `App.jsx`

Ejemplo de navegación por estados:

```jsx
const [view, setView] = useState("libros")
```

Renderizado:

```jsx
<Header
  currentUser={currentUser}
  mockUsers={mockUsers}
  loginUser={loginUser}
  onShowMisPrestamos={() => setView("prestamos")}
  onShowMisReservas={() => setView("reservas")}
/>

{view === "libros" && (
  <BookList
    books={books}
    loading={loading}
    error={error}
    onPrestar={handlePrestar}
  />
)}

{view === "prestamos" && (
  <MisPrestamos onBackToLibros={() => setView("libros")} />
)}

{view === "reservas" && (
  <MisReservas onBackToLibros={() => setView("libros")} />
)}

<Footer />
```

---

## 🔌 Endpoints requeridos en el backend

Este módulo espera que el backend exponga:

### 📚 Libros

```
GET  /libros
POST /libros/{idLibro}/prestar
```

### 📖 Préstamos

```
GET  /prestamos/usuario/{idUsuario}
POST /prestamos/{idPrestamo}/devolver
```

### 🔔 Reservas

```
GET  /reservas/usuario/{idUsuario}
POST /reservas/{idReserva}/aceptar
POST /reservas/{idReserva}/rechazar
```

---

## 👤 Usuario simulado (hasta que exista login real)

Debido a que este módulo se desarrolló antes de implementar Spring Security:

* No usa JWT
* No usa roles
* No usa autenticación

En su lugar, se creó `UserContext` con usuarios falsos:

```jsx
const mockUsers = [
  { id_usuario: 1, nombre: 'Sebastian', email: 'sebas@example.com' },
  { id_usuario: 2, nombre: 'Carlos', email: 'carlos@example.com' },
  { id_usuario: 3, nombre: 'María', email: 'maria@example.com' },
  { id_usuario: 4, nombre: 'Juan', email: 'juan@example.com' }
]
```

Estos usuarios coinciden con los registros de `data.sql` del backend.

El Header muestra:

```
Simular sesión: [ María ▼ ]
✓ Conectado como: María
```

---

## 🚀 Flujo de uso del módulo

### ✔ 1. El usuario selecciona su perfil

### ✔ 2. Navega al catálogo

### ✔ 3. Puede:

* Prestar libros disponibles
* Reservar libros agotados
* Consultar préstamos activos
* Devolver libros
* Ver reservas cuando son notificadas
* Aceptar o rechazar la reserva

Todo sincronizado con el backend.

---

## 🔒 Nota sobre la futura integración con autenticación

Cuando el sistema de login esté listo, este módulo deberá actualizarse:

| Actual                          | Futuro                                      |
| ------------------------------- | ------------------------------------------- |
| UserContext con usuarios falsos | Contexto con datos obtenidos del login real |
| idUsuario enviado en body       | Token JWT para autenticación                |
| Envío manual de usuarios        | Obtención automática desde backend          |

Esta versión sirve como **implementación funcional para pruebas**, mientras se completa la capa de seguridad.

---

## Autenticación basada en JWT (Recomendada)

Este proyecto utiliza autenticación JWT para manejar la sesión del usuario sin necesidad de React Context.

Cómo funciona

El usuario inicia sesión y el backend genera un token JWT con:

sub: ID del usuario

role: Rol del usuario

exp: expiración

El token se almacena en localStorage.

Todas las peticiones al backend incluyen:

Authorization: Bearer <token>

El frontend obtiene el ID del usuario decodificando el token:

import jwtDecode from "jwt-decode";
const token = localStorage.getItem("token");
const { sub: userId } = jwtDecode(token);
Ventajas

* Evita el uso de Context global.

* Permite acceder al usuario desde cualquier componente sin prop‑drilling.

* Sesión persistente incluso al recargar.

* Escalable y estándar en aplicaciones reales.
