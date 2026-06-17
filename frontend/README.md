# Horror Pool Frontend

React + TypeScript + Vite frontend for Horror Pool.

## Configuration

- `VITE_API_BASE_URL`: API base used by browser requests. Defaults to `/horrorpool`.
- `VITE_DEV_BACKEND_URL`: backend target for the Vite dev proxy. Defaults to `http://localhost:8080`.

Local development example:

```env
VITE_API_BASE_URL=/horrorpool
VITE_DEV_BACKEND_URL=http://localhost:8080
```

For same-origin production behind a reverse proxy, keep:

```env
VITE_API_BASE_URL=/horrorpool
```

For separate frontend/backend domains, use a full API URL:

```env
VITE_API_BASE_URL=https://api.example.com/horrorpool
```

## Scripts

```bash
npm run dev
npm run lint
npm run build
```
