const BASE_URL = "/horrorpool";

function isObject(v: unknown): v is Record<string, unknown> {
  return v !== null && typeof v === "object" && !Array.isArray(v);
}

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

      if (isObject(data) && Array.isArray(data.errors)) {
        const arr = data.errors;
        const lines = arr
          .filter((x): x is string => typeof x === "string" && x.trim().length > 0);

        if (lines.length > 0) {
          msg = lines.join("\n");
        } else if (typeof data.message === "string") {
          msg = data.message;
        }
      }
      else if (isObject(data) && typeof data.message === "string") {
        msg = data.message;
      }
      else if (typeof data === "string") {
        msg = data;
      }
      else if (isObject(data)) {
        const lines = Object.entries(data)
          .filter(([, v]) => typeof v === "string" && v.trim().length > 0)
          .map(([k, v]) => `${k}: ${v}`);
        if (lines.length > 0) msg = lines.join("\n");
      }
    } else {
      const text = await res.text();
      if (text) msg = text;
    }
  } catch (e) {
    console.warn("Failed to parse error response", e);
  }
  throw new Error(msg);
}

  if (res.status === 204) return undefined as T;
  return (await res.json()) as T;
}
