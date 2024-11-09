"use client"
import { FaTools } from 'react-icons/fa';
import { Button } from '@/components/ui/button';
import { GrServices } from "react-icons/gr";
import { useRouter } from 'next/navigation';
export default function ServiceComponent() {
    const router = useRouter();
  return (
    <div className="flex items-center justify-center h-[79vh]">
      <div className="flex flex-col items-center p-8 bg-white shadow-lg rounded-lg text-center max-w-md">
        <FaTools className="text-6xl text-yellow-500 mb-4 animate-bounce" />
        
        <h2 className="text-2xl font-bold text-gray-800 mb-2">
          Application Page Under Maintenance
        </h2>
        
        <p className="text-gray-600 mb-6">
          We're currently working on improvements. This page will be accessible shortly.
        </p>
        
        <Button className="bg-gradient-to-br from-purple-500 to-blue-600 text-white p-2 rounded-sm mb-4"
         onClick={() => router.push('/dashboard')}
        >
          Check Back Later
        </Button>
      </div>
    </div>
  );
}
