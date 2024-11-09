import { NextResponse } from 'next/server';

export async function GET(req) {
  const jwtToken = req.cookies.get('jwt');
  const username = req.cookies.get('username');
  const role = req.cookies.get('role');

  console.log("jwtToken from auth route:", jwtToken);
  console.log("username from auth route:", username);
  console.log("role from auth route:", role);

  if (!jwtToken) {
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
  }

  // Handle the request based on jwtToken, username, and role
  return NextResponse.json({
    message: 'Authenticated successfully',
    username,
    role
  });
}
