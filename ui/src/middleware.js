import { NextResponse } from 'next/server';

export function middleware(request) {
    const token = request.cookies.get("username")?.value;
    console.log("token", token);
    console.log("request",request.cookies)
    // Redirect to login if no token is found and trying to access the home or dashboard page
    if (!token && (request.nextUrl.pathname === '/' || request.nextUrl.pathname === '/dashboard')) {
        return NextResponse.redirect(new URL('/login', request.url));
    }

    // Redirect to dashboard if token exists and trying to access the home page
    if (token && request.nextUrl.pathname === '/') {
        return NextResponse.redirect(new URL('/dashboard', request.url));
    }

    return NextResponse.next();
}

export const config = {
    matcher: ['/', '/dashboard', '/login'],
};
