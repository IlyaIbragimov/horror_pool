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
  const ct = res.headers.get("content-type") ?? "";
  let msg = res.statusText || "Request failed";

  try {
    if (ct.includes("application/json")) {
      const data = await res.json();

      if (data && typeof data === "object" && "message" in data && typeof data.message === "string") {
        msg = data.message;
      } else if (data && typeof data === "object" && !Array.isArray(data)) {
          msg = Object.entries(data)
          .filter(([, v]) => typeof v === "string" && v.trim().length > 0)
          .map(([k, v]) => `${k}: ${v}`)
          .join("\n");
      } else if (typeof data === "string") {
        msg = data;
      }
    } else {
      const text = await res.text();
      msg = text || msg;
    }
  } catch(e) {
    console.warn("Failed to parse error response", e)
  }
  throw new Error(msg);
}

  if (res.status === 204) return undefined as T;
  return (await res.json()) as T;
}