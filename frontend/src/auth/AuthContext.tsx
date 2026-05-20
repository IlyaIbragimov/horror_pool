import React, {
  useEffect,
  useMemo,
  useState,
} from "react";
import { getCurrentUserInfo, signOut as apiSignOut } from "../api/auth.api";
import { AuthContext } from "./AuthContextValue";

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
