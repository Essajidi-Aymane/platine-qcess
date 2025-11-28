import { redirect } from 'next/navigation';
import { getUserSession } from '@/lib/session';

export default async function Home() {
    const user = await getUserSession();

    if (user) redirect('/dashboard');

    redirect('/auth/login-register');
}