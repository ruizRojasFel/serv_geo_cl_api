<div align="center">

<h1> 🇨🇱 API GEO CL </h1>

*API REST en Spring Boot con datos geopolíticos de Chile: **regiones y sus comunas**.*

[![Website](https://img.shields.io/badge/ver_sitio-mymicroservicesfel.vercel.app-lightblue)](https://mymicroservicesfel.vercel.app/) [![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/ruizRojasFel/serv_geo_cl_api?tab=MIT-1-ov-file)

</div>

<br>

## Descripción

Expone las regiones de Chile y las comunas de cada región a través de endpoints REST. Pensada para alimentar selectores en cascada (región → comuna) en formularios web o móviles. Los datos se almacenan en PostgreSQL y la API se documenta con Swagger/OpenAPI.

## Stack

- Java 17
- Spring Boot 3.5.13 (Web, Data JPA, Validation)
- PostgreSQL
- Flyway
- OpenAPI/Swagger (`springdoc-openapi`)
- Maven
- Docker / Docker Compose

## Qué incluye hoy

- Endpoints REST bajo `/api/v1` para selectores en cascada región → comunas
- Manejo global de errores en JSON
- Esquema y datos versionados con Flyway (`src/main/resources/db/migration`):
  - 16 regiones
  - 56 provincias (usadas internamente para enlazar comuna → región)
  - 346 comunas

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/regiones` | Lista todas las regiones |
| GET | `/api/v1/regiones/{id}/comunas` | Lista las comunas de una región (ordenadas por nombre) |

## Respuestas

- `RegionDTO`: `id`, `numero`, `nombre`, `capital`
- `ComunaDTO`: `id`, `nombre`, `codigoCut`

Ejemplo de error:

```json
{
  "status": 404,
  "error": "Not Found",
  "mensaje": "Región con id 999 no encontrada",
  "timestamp": "2026-05-04T01:30:00"
}
```

## Swagger / OpenAPI

- UI: `http://localhost:8080/swagger-ui/index.html`
- Docs JSON: `http://localhost:8080/v3/api-docs`
- Publicado (GitHub Pages): https://ruizrojasfel.github.io/serv_geo_cl_api/swagger/

## Ejecutar con Docker Compose (recomendado)

1. Copia variables de entorno:

```bash
cp .env.example .env
```

2. Completa `.env` con:

```env
DB_NAME=db_api_geo_cl
DB_USER=user_api_geo_cl
DB_PASSWORD=pass_api_geo_cl_2026
```

3. Levanta servicios:

```bash
docker compose up --build
```

Servicios:
- API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

## Ejecutar local (sin Docker)

Requisitos: Java 17 y un servidor PostgreSQL en `localhost:5432` con el usuario de `.env` creado.

```bash
cp .env.example .env   # si aún no existe; completa DB_NAME, DB_USER y DB_PASSWORD
./mvnw spring-boot:run
```

La app lee `.env` automáticamente (las variables de entorno tienen prioridad). Host y puerto se pueden cambiar con `DB_HOST` y `DB_PORT`.

## Base de datos

Al arrancar, tanto con Docker Compose como con `./mvnw spring-boot:run`:

1. **Base de datos**: si `DB_NAME` no existe, se crea automáticamente (`DatabaseCreator`). En Docker la crea además el contenedor de PostgreSQL. Si el usuario no tiene permiso `CREATEDB`, se registra un warning y hay que crearla a mano.
2. **Esquema y datos**: Flyway aplica las migraciones pendientes de `db/migration` (`V1__crear_esquema.sql`, `V2__datos_iniciales.sql`). Cada una corre una sola vez, por lo que los datos persisten entre reinicios (y en el volumen `postgres_data`).
3. **Validación**: Hibernate (`ddl-auto: validate`) verifica que el esquema coincida con las entidades.

Para cambiar el esquema o los datos, agrega una nueva migración (`V3__...sql`); no edites las ya aplicadas.

## Tests y cobertura

```bash
./mvnw test
```

Reporte Jacoco:

```bash
./mvnw jacoco:report
```

Salida: `target/site/jacoco/index.html`

## CI/CD

Workflow: `.github/workflows/deploy.yml`

En `main`:
1. Compila y ejecuta tests
2. Genera y publica reporte Jacoco en GitHub Pages
3. Construye imagen Docker
4. Dispara deploy en Render vía `RENDER_DEPLOY_HOOK`

## CORS habilitado para

- `http://localhost:4200`
- `http://localhost:4300`
- `http://localhost:5173`
- `http://localhost:8080`
- `http://localhost:3001`
- `https://mymicroservicesfel.vercel.app`
- `https://ruizrojasfel.github.io`

---

## License

[![License](https://img.shields.io/badge/License-MIT-yellow)](https://github.com/ruizRojasFel/api-geo-cl?tab=MIT-1-ov-file)

<br>

---

<div align="center">

<h2> Developer </h2>

<h3> Felipe Andrés Ruiz Rojas </h3>

[![LinkedIn](https://img.shields.io/badge/LinkedIn-linkedin.com%2Fin%2Fruizrojasfel-blue)](https://www.linkedin.com/in/ruizrojasfel) [![Website](https://img.shields.io/badge/Website-felruiz--dev.netlify.app-lightblue)](https://felruiz-dev.netlify.app/)

Copyright © 2026 Fel Ruiz
</div>
