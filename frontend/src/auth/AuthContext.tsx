import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { getCurrentUserInfo, signOut as apiSignOut } from "../api/auth.api";

type AuthState = {
  user: string | null;
  roles: string[];
  isAdmin: boolean;
  loading: boolean;
  refresh: () => Promise<void>;
  logout: () => Promise<void>;
  setUser: (u: string | null) => void;
};

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<string | null>(null);
  const [roles, setRoles] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  const refresh = async () => {
    try {
      const data = await getCurrentUserInfo();
      setUser(data.username);
      setRoles(data.roles ?? []);
    } catch {
      setUser(null);
      setRoles([]);
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      await apiSignOut();
    } finally {
      setUser(null);
      setRoles([]);
    }
  };

  useEffect(() => {
    refresh();
  }, []);

  const isAdmin = roles.includes("ROLE_ADMIN");

  const value = useMemo(
    () => ({ user, roles, isAdmin, loading, refresh, logout, setUser }),
    [user, roles, isAdmin, loading],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside <AuthProvider>");
  return ctx;
}
