import { cookies } from "next/headers";

export async function getUserSession() {
    const cookieStore = await cookies();

    const token = cookieStore.get("qcess_token")?.value;

    if (!token) {
        return null;
    }

    const payloadBase64 = token.split(".")[1];
    const json = Buffer.from(payloadBase64, "base64").toString("utf8");
    const payload = JSON.parse(json);

    return {
        token,
        ...payload,
    };
}
