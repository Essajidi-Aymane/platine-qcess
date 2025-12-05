import { cookies } from "next/headers";
import AdminLayoutClient from "./AdminLayoutClient";
import {getUserSession} from "@/lib/session";
import "../globals.css";
export default async function AdminLayout({ children }) {
    const admin = await getUserSession();
    return <AdminLayoutClient admin={admin}>{children}</AdminLayoutClient>;
}
