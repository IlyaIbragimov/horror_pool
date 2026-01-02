import type { ReactNode } from "react";

export function AppLayout({ children }: { children: ReactNode }) {
  return <div className="container">{children}</div>;
}