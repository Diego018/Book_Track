# Book_Track

## Ejecutar local (perfil `local` con H2)

Compilar e iniciar con el profile local (H2 en memoria):

```bash
./mvnw clean install -U -DskipTests
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Probar importación CSV

Ejemplo rápido con curl:

```bash
echo "titulo,autor,cantidad_total,cantidad_disponible,fecha" > /tmp/test.csv
echo "El Quijote,Miguel de Cervantes,5,3,2023-01-01" >> /tmp/test.csv
curl -F "file=@/tmp/test.csv" http://localhost:8080/api/admin/libros/import-csv
```

Respuesta (`ImportReport`) ejemplo:

```json
{
	"inserted": 1,
	"duplicates": 0,
	"errors": 0,
	"errorsDetail": []
}
```

Si hay filas inválidas `errors` > 0 y `errorsDetail` contiene objetos con `row` y `reason`.