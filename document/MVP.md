# Requisitos para el MVP – BookTrack

## 1. Carga inicial de datos desde CSV

**Descripción:**  
El sistema debe permitir cargar un archivo CSV con libros iniciales al momento de despliegue o mediante una función específica del administrador.

**Criterios de Aceptación:**
- Se puede importar un archivo CSV válido.
- Los registros nuevos se almacenan en la base de datos sin duplicados (comparando por Título + Autor).
- Si el formato es inválido, el sistema debe notificar el error sin interrumpir la aplicación.

---

## 2. Login

**Descripción:**  
El sistema debe permitir que un usuario existente (con rol Admin o Usuario) se autentique mediante correo electrónico y contraseña.

**Criterios de Aceptación:**
- Un usuario administrador puede entrar con permisos de administrador.
- Usuario común no tiene permisos de administrador.
- Se puede registrar un nuevo usuario común con correo y contraseña.

---

## 3. CRUD de libros

**Descripción:**  
El administrador puede Crear, Leer, Actualizar y Eliminar libros.

**Criterios de Aceptación:**
- Solo administradores pueden crear, actualizar y eliminar libros.
- Usuarios pueden consultar libros sin modificar.
- La información del libro debe incluir: título, autor, género, rating, disponibilidad, año.
- El sistema valida campos obligatorios.

---

## 4. Búsqueda eficiente por título/autor

**Descripción:**  
La búsqueda debe ser rápida incluso con gran cantidad de libros, usando un BST o HashMap en memoria.

**Criterios de Aceptación:**
- La búsqueda devuelve resultados relevantes en tiempo óptimo.
- Se puede buscar por título parcial, título completo o autor.
- El algoritmo de búsqueda corresponde al definido en diseño (HashMap o BST), no consultas secuenciales lentas.

---

## 5. Préstamo y devolución de libros

**Descripción:**  
Los usuarios pueden tomar prestado un libro si hay unidades disponibles.

**Criterios de Aceptación:**
- No se puede prestar un libro con disponibilidad = 0.
- Al prestar un libro, la disponibilidad disminuye.
- Al devolver un libro, la disponibilidad aumenta.
- Registrar fecha de préstamo y devolución en el historial del usuario.

---

## 6. Sistema de reservas con Queue

**Descripción:**  
Cuando no haya copias disponibles, un usuario puede reservar el libro.

**Criterios de Aceptación:**
- Si el libro tiene disponibilidad = 0, el usuario entra a una cola de espera.
- Cuando el libro es devuelto, el primer usuario en la cola recibe prioridad para el préstamo.
- El sistema notifica cuando el préstamo está disponible (puede ser log o notificación básica para MVP).

---

## 7. Historial por usuario (LinkedList personalizada)

**Descripción:**  
Cada usuario mantiene un registro ordenado de movimientos de libros.

**Criterios de Aceptación:**
- Se registra: fecha, acción (préstamo/devolución), título del libro.
- La estructura debe ser una LinkedList implementada manualmente, no una lista nativa.
- El historial se puede consultar desde el frontend.

---

## 8. Logs avanzados

**Descripción:**  
El sistema generará logs internos para monitorear actividad y rendimiento.

**Criterios de Aceptación:**
- Se registran acciones principales: inicio de sesión, préstamo, devolución, reserva.
- Deben existir logs de rendimiento: tiempos de procesamiento en operaciones clave.
- Logs deben almacenarse en archivo y/o consola.

---

## 9. Pruebas unitarias

**Descripción:**  
Se deben asegurar pruebas unitarias mínimas en componentes clave.

**Criterios de Aceptación:**
- Deben existir pruebas para:
  - BST o HashMap de búsqueda.
  - Cola de reservas.
  - Validación de préstamos.
- Cobertura mínima sugerida: 60%.

---
