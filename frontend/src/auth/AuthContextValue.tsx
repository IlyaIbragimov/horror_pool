import { createContext } from "react";

export type AuthState = {
  user: string | null;
  roles: string[];
  isAdmin: boolean;
  loading: boolean;
  refresh: () => Promise<void>;
  logout: () => Promise<void>;
  setUser: (u: string | null) => void;
};

export const AuthContext = createContext<AuthState | null>(null);
