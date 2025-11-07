# BookTrack – Sistema de Biblioteca y Recomendador

## 1. Objetivo del Proyecto

El proyecto **BookTrack** busca resolver la gestión ineficiente de bibliotecas universitarias que aún dependen de procesos manuales o sistemas fragmentados. Su objetivo principal es automatizar el préstamo, devolución, búsqueda y recomendación de libros mediante un sistema web moderno y estructurado.

Además, el sistema incorpora algoritmos de ordenamiento, colas, árboles y estructuras de datos eficientes para garantizar:

- Una experiencia de usuario fluida
- Tiempos de respuesta óptimos
- Trazabilidad completa del historial de uso

En síntesis, BookTrack no solo gestiona libros, sino que también optimiza la toma de decisiones del usuario mediante recomendaciones basadas en:

- Calificaciones
- Popularidad
- Fecha de publicación

---

## 2. Idea de Negocio, Usuarios y Valor

**Usuarios objetivo:**  
Estudiantes, docentes y personal administrativo de universidades o instituciones académicas que necesitan consultar, reservar o recomendar libros.

**Valor agregado:**

- Velocidad y precisión en la búsqueda (BST y HashMap)
- Recomendaciones inteligentes (Heap y QuickSort)
- Gestión automatizada de reservas y préstamos (Queue y listas enlazadas)
- Facilidad de integración y mantenimiento (arquitectura monolítica en capas)

**Diferencial competitivo:**  
BookTrack combina conceptos teóricos de estructuras de datos con un caso realista de ingeniería, logrando un equilibrio entre eficiencia técnica y utilidad práctica.

---

## 3. Stack Tecnológico

### Frontend

- **React (con Vite):** Renderizado rápido, modularidad y compatibilidad con bibliotecas modernas.
- **TailwindCSS:** Diseño moderno, responsivo y personalizable sin escribir CSS desde cero.
- **React Router + Fetch API:** Navegación fluida y comunicación directa con el backend.

### Backend

- **Spring Boot (Java):** Estructura robusta y escalable para APIs REST, con arquitectura en capas (Controller, Service, Repository).

### Base de Datos

- **PostgreSQL:** Estabilidad, soporte para consultas complejas y compatibilidad con JPA. Ideal para manejar relaciones entre libros, usuarios y préstamos.

### Pruebas

- **JUnit y Mockito:** Pruebas unitarias y de integración para asegurar la lógica de negocio y estructuras implementadas.

### Arquitectura

- **Monolítica en capas (MVC):** Simplifica el despliegue en la fase MVP y permite evolución futura hacia microservicios.

---

