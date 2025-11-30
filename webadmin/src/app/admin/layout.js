import { cookies } from "next/headers";
import AdminLayoutClient from "./AdminLayoutClient";
import {getUserSession} from "@/lib/session";

export default async function AdminLayout({ children }) {
    const admin = await getUserSession();
    return <AdminLayoutClient admin={admin}>{children}</AdminLayoutClient>;
}
