import { NextResponse } from 'next/server';

export function middleware(request) {
    const token = request.cookies.get("username")?.value;

    console.log("token from middleware:", token);
    console.log("request.nextUrl.pathname from middleware:", request.nextUrl.pathname);
    console.log("request.url from middleware:", request.url);
    console.log("request from middleware:", request);
    if (!token && request.nextUrl.pathname === '/') {
        return NextResponse.redirect(new URL('/login', request.url));
    }

    if (token && request.nextUrl.pathname === '/') {
        return NextResponse.redirect(new URL('/dashboard', request.url));
    }

    return NextResponse.next();
}

export const config = {
    matcher: ['/', '/dashboard', '/login'],
};