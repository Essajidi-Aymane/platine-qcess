import { NextResponse } from 'next/server';


/** @param {import('next/server').NextRequest} request */
export function middleware(request) {
    const { nextUrl, cookies } = request;

    const token = cookies.get('qcess_token')?.value;
    const path = nextUrl.pathname;
    console.log('MIDDLEWARE QCESS → path =', path, 'token =', token ? 'PRESENT' : 'ABSENT');

    if (!token && path.startsWith('/admin')) {
        const loginUrl = new URL('/auth/login-register', request.url);
        loginUrl.searchParams.set('next', path + nextUrl.search);
        return NextResponse.redirect(loginUrl);
    }

    if (token && path === '/auth/login-register') {
        const dashboardUrl = new URL('/admin/dashboard', request.url);
        return NextResponse.redirect(dashboardUrl);
    }

    return NextResponse.next();
}

export const config = {
    matcher: ['/admin/:path*', '/auth/login-register'],


};
