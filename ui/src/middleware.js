import { NextResponse } from 'next/server';
import Cookies from 'js-cookie';

export function middleware(request) {
    const token = Cookies.get("name")
    console.log("token from middleware:", token);

    if (!token && request.nextUrl.pathname === '/') {
        return NextResponse.redirect(new URL('/login', request.url));
    }

    if (token && request.nextUrl.pathname === '/') {
        return NextResponse.redirect(new URL('/dashboard', request.url));
    }

    return NextResponse.next();
}

export const config = {
    matcher: ['/((?!login).*)']
};