"use client"

import { usePathname, useRouter } from "next/navigation"
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbSeparator } from "@/components/ui/breadcrumb"
import { Button } from "@/components/ui/button"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Bell, MoreHorizontal, User, LogOut } from "lucide-react"
import Cookies from 'js-cookie'
import { useEffect, useState } from "react"
import axios from 'axios'
import { toast, Toaster } from 'react-hot-toast'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"

export default function TopNavBar() {
  const pathname = usePathname()
  const router = useRouter()
  const [isLogoutDialogOpen, setIsLogoutDialogOpen] = useState(false)
  const paths = pathname.split("/").splice(1)
  const [username, setUsername] = useState("")

  const handleLogout = async () => {
    try {
      await axios.post(`${process.env.NEXT_PUBLIC_API_URL}/public/logout`, {}, { withCredentials: true })
      
      Cookies.remove("jwt")
      Cookies.remove("name")
      Cookies.remove("roles")

      setIsLogoutDialogOpen(false)

      toast("Logged out successfully 🎉")

      setTimeout(() => {
        router.push("/login")
      }, 3000)
    } catch (error) {
      console.error("Logout error:", error)
      toast("Logout failed 🤷‍♂️")
    }
  }

  useEffect(() => {
    const username = Cookies.get("name")
    setUsername(username)
  }, [])  

  return (
    <>
      <header className="sticky py-3 top-0 z-30 flex justify-between h-16 items-center gap-4 border-b bg-white px-6 shadow-md">
        <div className="flex items-center justify-between w-full">
          <Breadcrumb>
            <BreadcrumbList>
              <BreadcrumbItem>
                <BreadcrumbLink href="/" className="text-primary font-semibold">Home</BreadcrumbLink>
              </BreadcrumbItem>
              {paths[0] !== "home" && <BreadcrumbSeparator />}
              {paths[0] !== "home" && (
                <BreadcrumbItem>
                  <BreadcrumbLink href={`/${paths[0]}`} className="capitalize text-muted-foreground hover:text-primary transition-colors">
                    {paths[0]}
                  </BreadcrumbLink>
                </BreadcrumbItem>
              )}
            </BreadcrumbList>
          </Breadcrumb>
          <div className="flex items-center space-x-4">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                  <Avatar className="h-8 w-8 ring-2 ring-primary font-bold">
                    <AvatarImage src="" alt="" />
                    <AvatarFallback>
  {username ? username.charAt(0).toUpperCase() : ""}
</AvatarFallback>

                  </Avatar>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent className="w-56" align="end" forceMount>
                <DropdownMenuLabel className="font-normal">
                  <div className="flex flex-col space-y-1">
                    <p className="text-sm font-medium leading-none">{username?.charAt(0).toUpperCase() + username?.slice(1)}</p>
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem>
                  <User className="mr-2 h-4 w-4" />
                  <span>Profile</span>
                </DropdownMenuItem>
                <DropdownMenuItem>
                  <Bell className="mr-2 h-4 w-4" />
                  <span>Notifications</span>
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem onSelect={() => setIsLogoutDialogOpen(true)}>
                  <LogOut className="mr-2 h-4 w-4" />
                  <span>Logout</span>
                </DropdownMenuItem>
                <DropdownMenuItem>
                  <MoreHorizontal className="mr-2 h-4 w-4" />
                  <span>More</span>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </header>

      <AlertDialog open={isLogoutDialogOpen} onOpenChange={setIsLogoutDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you sure you want to logout?</AlertDialogTitle>
            <AlertDialogDescription>
              This action will end your current session and redirect you to the login page.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={handleLogout}>Logout</AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      <Toaster />
    </>
  )
}