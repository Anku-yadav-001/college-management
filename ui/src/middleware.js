import { NextResponse } from 'next/server';

export function middleware(request) {
  const { cookies, url } = request;
  const usernameCookie = cookies.get('username'); // Check if 'username' cookie is present

  console.log("usernameCookie from middleware:", usernameCookie);  // Debug to ensure token is available

  // If the user is already logged in, redirect them to the dashboard
  if (usernameCookie && url.includes('/login')) {
    return NextResponse.redirect(new URL('/dashboard', url));
  }

  // If the user is not logged in and trying to access the dashboard, redirect them to login
  if (!usernameCookie && url.includes('/dashboard')) {
    return NextResponse.redirect(new URL('/login', url));
  }

  // Allow access to other routes
  return NextResponse.next();
}

// Apply the middleware only to specific routes using a matcher
export const config = {
  matcher: ['/dashboard', '/login'], // Add paths where middleware should run
};
