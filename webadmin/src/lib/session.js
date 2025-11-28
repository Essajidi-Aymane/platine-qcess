import { cookies } from "next/headers";

export async function getUserSession() {
    const cookieStore = await cookies();

    const token = cookieStore.get("qcess_token")?.value;

    if (!token) {
        return null;
    }

    return { token };
}
