# minders-braze-backend

Backend en Spring Boot que actúa como puente de eventos entre el frontend de Minders Ecommerce y Braze. Cada endpoint recibe un evento de la taxonomía de Amplitude/Braze y lo reenvía a la API REST de Braze, fijando del lado del servidor el nombre exacto del evento (el front nunca lo arma concatenando texto).

## Endpoints

### Eventos (`/api/events`)

| Método | Path | Evento Braze |
|---|---|---|
| POST | `/api/events/page-viewed` | Page Viewed — `{ page_name }` |
| POST | `/api/events/product-viewed` | Product Viewed — `{ product_id, product_name, price }` |
| POST | `/api/events/product-added-to-cart` | Product Added to Cart — `{ product_id, quantity }` |
| POST | `/api/events/order-completed` | Order Completed — `{ order_id, revenue }` (además actualiza `lifetime_value`, `customer_type`, `first_purchase_date`) |

### Usuarios (`/api/users`)

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/users/identify` | Funde el historial anónimo del visitante con su `external_id` real llamando a `/users/identify` de Braze |

## Configuración

Variables de entorno (ver `src/main/resources/application.properties`):

- `BRAZE_API_KEY` — REST API Key de Braze (Developer Console > API Keys)
- `BRAZE_REST_ENDPOINT` — endpoint REST según el cluster (default `https://rest.iad-01.braze.com`)
- `BRAZE_DEFAULT_USER_ID` — user id por defecto cuando el front no envía uno (default `demo-user`)
- `APP_CORS_ALLOWED_ORIGINS` — orígenes permitidos por CORS (default `http://localhost:3000,http://localhost:5173`)

## Levantar el proyecto

### Maven

```bash
mvn spring-boot:run
```

El servidor queda escuchando en `http://localhost:8080`.

### Docker

```bash
docker build -t minders-braze-backend .
docker run -p 8080:8080 \
  -e BRAZE_API_KEY=... \
  -e BRAZE_REST_ENDPOINT=... \
  minders-braze-backend
```
