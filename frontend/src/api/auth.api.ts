import { http } from "./http";
import type { MessageResponse, SignUpRequest } from "../types/auth.types";

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

export function signUp(payload: SignUpRequest) {
  return http<MessageResponse>("/public/signup", {method: "POST", body: JSON.stringify(payload)})
}

export async function getUsername() {
  return http<{ username: string }>("/username", { method: "GET" });
}