const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/horrorpool";

export async function http<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);

  headers.set("Accept", "application/json");

  if (init.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const res = await fetch(`${BASE_URL}${path}`, {
    ...init,
    headers,
    credentials: "include",
  });

if (!res.ok) {
  const contentType = res.headers.get("content-type") ?? "";

  let message = res.statusText || `HTTP ${res.status}`;

  try {
    if (contentType.includes("application/json")) {
      const data = await res.json();
      message = data?.message ?? JSON.stringify(data);
    } else {
      const text = await res.text();
      message = text || message;
    }
  } catch {
    throw new Error("Something went wrong")
  }

  throw new Error(message);
}

  if (res.status === 204) return undefined as T;
  return (await res.json()) as T;
}