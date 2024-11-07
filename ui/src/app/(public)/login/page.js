'use client'

import { useState } from 'react'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { UserIcon, LockIcon, EyeIcon, EyeOffIcon } from 'lucide-react'
import { toast, Toaster } from 'react-hot-toast'
import axios from 'axios'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog"
import { motion, AnimatePresence } from 'framer-motion'

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [showWelcomeDialog, setShowWelcomeDialog] = useState(false);
    const [errors, setErrors] = useState({ username: '', password: '' });
  
    const handleSubmit = async (e) => {
      e.preventDefault();
      
      // Client-side validation with custom messages
      const newErrors = { username: '', password: '' };
      if (!username.trim()) newErrors.username = 'Please enter your username';
      if (!password.trim()) newErrors.password = 'Please enter your password';
      setErrors(newErrors);

      if (newErrors.username || newErrors.password) {
        toast.error('Please fill in all required fields', {
          duration: 3000,
          icon: '⚠️',
          style: {
            background: '#FFA500',
            color: '#fff',
          },
          position:"bottom-right"
        });
        return;
      }
      console.log("username", username);
        console.log("password", password);
      
      try {
        const response = await axios.post(`${process.env.NEXT_PUBLIC_API_URL}/public/login`, {
          username,
          password,
        });
      
        console.log(response);
        if (response.status === 200) {
          toast.success('Login successful!', {
            duration: 3000,
            icon: '🎉',
            style: {
              background: '#4CAF50',
              color: '#fff',
            },
            position:"bottom-right"
          });
          setShowWelcomeDialog(true);
        } else {
          const errorMessage = await response.text();
          toast.error(`${errorMessage}`, {
            duration: 4000,
            icon: '❌',
            style: {
              background: '#F44336',
              color: '#fff',
            },
            position:"bottom-right"
          });
        }
      } catch (error) {
        console.log(error);
        toast.error('Login failed, please try again.', {
          duration: 4000,
          icon: '⚠️',
          style: {
            background: '#FF9800',
            color: '#fff',
          },
          position:"bottom-right"
        });
      }
    };
  
    const toggleShowPassword = () => {
      setShowPassword(!showPassword);
    };
  
    const handleCloseWelcomeDialog = () => {
      setShowWelcomeDialog(false);
      window.location.href = '/dashboard';
    };
  
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-800 relative overflow-hidden">
        <Toaster position="top-right" reverseOrder={false} />

        <div className="absolute inset-0">
          <div className="absolute inset-0 bg-gradient-to-br from-gray-700 to-gray-900"></div>
          <div className="absolute inset-0 opacity-50">
            {[...Array(3)].map((_, i) => (
              <div
                key={i}
                className="absolute bg-blue-500 rounded-full blur-3xl"
                style={{
                  width: '40vw',
                  height: '40vw',
                  top: `${30 * i}%`,
                  left: `${30 * i}%`,
                  animation: `pulse 15s ease-in-out infinite alternate-reverse`,
                  animationDelay: `${5 * i}s`,
                }}
              ></div>
            ))}
          </div>
        </div>
  
        <style jsx global>{`
          @keyframes pulse {
            0% { transform: scale(1) translate(0, 0); opacity: 0.5; }
            50% { transform: scale(1.05) translate(2%, 2%); opacity: 0.6; }
            100% { transform: scale(1) translate(0, 0); opacity: 0.5; }
          }
        `}</style>
  
        <div className="bg-gray-800 bg-opacity-80 rounded-lg shadow-xl p-8 w-full max-w-md relative z-10 backdrop-blur-sm">
          <h2 className="text-2xl font-semibold text-center mb-6 text-gray-100">Welcome Back</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="username" className="text-sm font-medium text-gray-300">Username</Label>
              <div className="relative">
                <UserIcon className="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
                <Input
                  id="username"
                  type="text"
                  placeholder="Enter your username"
                  value={username}
                  onChange={(e) => {
                    setUsername(e.target.value);
                    setErrors(prev => ({ ...prev, username: '' }));
                  }}
                  className={`pl-10 pr-4 py-2 w-full bg-gray-700 border ${errors.username ? 'border-red-500' : 'border-gray-600'} text-gray-100 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200`}
                />
              </div>
              {errors.username && (
                <p className="text-red-500 text-xs mt-1 animate-fadeIn">
                  {errors.username}
                </p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="password" className="text-sm font-medium text-gray-300">Password</Label>
              <div className="relative">
                <LockIcon className="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => {
                    setPassword(e.target.value);
                    setErrors(prev => ({ ...prev, password: '' }));
                  }}
                  className={`pl-10 pr-12 py-2 w-full bg-gray-700 border ${errors.password ? 'border-red-500' : 'border-gray-600'} text-gray-100 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200`}
                />
                <button
                  type="button"
                  onClick={toggleShowPassword}
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-200 focus:outline-none"
                >
                  {showPassword ? <EyeOffIcon className="w-5 h-5" /> : <EyeIcon className="w-5 h-5" />}
                </button>
              </div>
              {errors.password && (
                <p className="text-red-500 text-xs mt-1 animate-fadeIn">
                  {errors.password}
                </p>
              )}
            </div>
            <Button
              type="submit"
              className="w-full bg-blue-600 text-white font-semibold py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 transition duration-300"
            >
              Sign In
            </Button>
          </form>
          <div className="mt-4 text-center">
            <a href="#" className="text-sm text-gray-400 hover:text-gray-200 transition duration-200">Forgot password?</a>
          </div>
        </div>

        <AnimatePresence>
          {showWelcomeDialog && (
            <Dialog open={showWelcomeDialog} onOpenChange={setShowWelcomeDialog}>
              <DialogContent className="sm:max-w-[425px] bg-gradient-to-br from-blue-500 to-purple-600 text-white">
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -20 }}
                  transition={{ duration: 0.3 }}
                >
                  <DialogHeader>
                    <DialogTitle className="text-2xl font-bold mb-2">Welcome Back! 🎉</DialogTitle>
                    <DialogDescription className="text-blue-100">
                      We are thrilled to see you again! 🌟 Your journey continues here.
                    </DialogDescription>
                  </DialogHeader>
                  <div className="mt-6 text-center">
                    <p className="text-lg mb-4">Ready to explore? 🚀</p>
                    <div className="flex justify-center space-x-4">
                      <Button onClick={handleCloseWelcomeDialog} className="bg-white text-blue-600 hover:bg-blue-100 transition-colors">
                        Go to Dashboard 🏠
                      </Button>
                    </div>
                  </div>
                </motion.div>
              </DialogContent>
            </Dialog>
          )}
        </AnimatePresence>
      </div>
    );
}