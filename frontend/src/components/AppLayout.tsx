import { Outlet } from "react-router-dom";
import Footer from "../components/Footer/Footer";
import styles from "./AppLayout.module.css";
import Header from "./Header/Header";

export function AppLayout() {
  return (
    <div className={styles.app}>
      <Header />

      <main className={styles.main}>
        <div className={styles.container}>
           <Outlet />
        </div>
      </main>
      
      <Footer />
    </div>
  );
}