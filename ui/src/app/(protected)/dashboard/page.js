"use client"
import { useEffect } from 'react';
export default function Dashboard() {
  useEffect(() => {
    const checkAuthStatus = async () => {
      try {
        const response = await fetch('/api/auth', {
          method: 'GET',
          credentials: 'include',
        });
        const data = await response.json();
        console.log(data);
      } catch (error) {
        console.error('Not authenticated:', error);
      }
    };
    checkAuthStatus();
  }, []);
  return <div>Dashboard</div>;
}

