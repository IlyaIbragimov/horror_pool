import { http } from "./http";

export type MessageResponse = { message: string };

export async function signIn(username: string, password: string) {
  return http<MessageResponse>("/public/signin", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
}

export async function signOut() {
  return http<MessageResponse>("/public/signout", {
    method: "POST",
  });
}

export async function getUsername() {
  return http<{ username: string }>("/username", { method: "GET" });
}