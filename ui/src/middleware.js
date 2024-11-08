import { NextResponse } from 'next/server';
import Cookies from "js-cookie"
export function middleware(request) {
    const token = request.cookies.get('username')?.value;  // Access the JWT token
    const cookies = new Cookies();
    const newUsername = cookies.get('username')?.value;
    
    console.log("Token from middleware: new ", token);  // Debug to ensure token is available
    console.log("newUsername from middleware: new ", newUsername);  // Debug to ensure token is available
    if ((!token && !newUsername) && (request.nextUrl.pathname === '/' || request.nextUrl.pathname === '/dashboard')) {
        return NextResponse.redirect(new URL('/login', request.url));
    }

    if ((token || newUsername) && request.nextUrl.pathname === '/') {
        return NextResponse.redirect(new URL('/dashboard', request.url));
    }

    return NextResponse.next();
}

export const config = {
    matcher: ['/', '/dashboard', '/login'],
};
