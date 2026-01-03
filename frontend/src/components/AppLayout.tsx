import { Outlet } from "react-router-dom";
import Footer from "../components/Footer/Footer";
import styles from "./AppLayout.module.css";

export function AppLayout() {
  return (
    <div className={styles.app}>
      <main className={styles.main}>
        <div className={styles.container}>
           <Outlet />
        </div>
      </main>
      <Footer />
    </div>
  );
}