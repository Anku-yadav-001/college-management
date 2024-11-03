'use client'

import { useState, useEffect } from 'react'
import axios from 'axios'
import { useParams } from 'next/navigation'
import { format } from 'date-fns'
import { motion } from 'framer-motion'
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { Calendar, Clock, User, Briefcase, Mail, Phone, GraduationCap, MapPin, HelpCircle, Video } from 'lucide-react'
import { Button } from "@/components/ui/button"
import { useRouter } from 'next/navigation'
import Link from 'next/link'

const fadeInUp = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0 },
  transition: { duration: 0.5 }
}

const stagger = {
  animate: {
    transition: {
      staggerChildren: 0.1
    }
  }
}

export default function OnlineClassDetails() {
  const router = useRouter()
  const [lecture, setLecture] = useState(null)
  const [faculty, setFaculty] = useState(null)
  const [students, setStudents] = useState([])
  const [loading, setLoading] = useState(true)
  const lectureId  = useParams()["class-details-id"]

  useEffect(() => {
    const fetchData = async () => {
      try {
        const lectureResponse = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/online-classes/get-online-class-by-id/${lectureId}`, { withCredentials: true })
        setLecture(lectureResponse.data)

        const facultyResponse = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/faculty/faculty-by-id/${lectureResponse.data.facultyId}`, { withCredentials: true })
        setFaculty(facultyResponse.data)

        const studentPromises = lectureResponse.data.studentIds.map(studentId =>
          axios.get(`${process.env.NEXT_PUBLIC_API_URL}/student/get-student/${studentId}`, { withCredentials: true })
        )
        const studentResponses = await Promise.all(studentPromises)
        setStudents(studentResponses.map(response => response.data))

        setLoading(false)
      } catch (error) {
        console.error('Error fetching data:', error)
        setLoading(false)
      }
    }

    fetchData()
  }, [lectureId])

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
          className="w-16 h-16 border-t-4 border-blue-500 border-solid rounded-full animate-spin"
        />
      </div>
    )
  }

  if (!lecture || !faculty) {
    return <div className="flex justify-center items-center h-screen">Lecture details not found</div>
  }

  return (
    <motion.div 
      initial="initial"
      animate="animate"
      variants={stagger}
      className="h-[86vh]"
    >
      <Card className="overflow-hidden bg-gradient-to-br from-blue-50 to-purple-50 border-blue-200 shadow-lg rounded-md h-full">
        <CardHeader className="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-4">
          <motion.div variants={fadeInUp}>
            <CardTitle className="text-xl font-bold mb-2">⭐ {lecture.topic || "No Topic"}</CardTitle>
            <div className="flex items-center mt-2 text-blue-100">
              <Calendar className="w-5 h-5 mr-2" />
              <span>
                {format(new Date(lecture.startFrom), 'PPP')} | {format(new Date(lecture.startFrom), 'p')} - {format(new Date(lecture.end), 'p')}
              </span>
            </div>
          </motion.div>
        </CardHeader>
        <CardContent className="p-4 overflow-y-scroll">
          <motion.div variants={stagger} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <motion.div variants={fadeInUp}>
              <h3 className="text-xl font-semibold mb-4 text-blue-700">Lecture Details</h3>
              <div className="space-y-3">
                <div className="flex flex-wrap items-center gap-2">
                  <Badge variant="outline" className="bg-blue-100 text-blue-700 border-blue-300">{lecture.platforms}</Badge>
                  <Badge variant="outline" className="bg-purple-100 text-purple-700 border-purple-300">Semester {lecture.semester}</Badge>
                  <Badge variant="outline" className="bg-indigo-100 text-indigo-700 border-indigo-300">{lecture.branchCode}</Badge>
                </div>
                <p className="flex items-center"><Clock className="w-4 h-4 mr-2 text-blue-600" /> Duration: {format(new Date(lecture.startFrom), 'p')} - {format(new Date(lecture.end), 'p')}</p>
                <p className="flex items-center"><MapPin className="w-4 h-4 mr-2 text-blue-600" />platforms: {lecture.subject}</p>
              </div>
            </motion.div>
            <motion.div variants={fadeInUp}>
              <h3 className="text-xl font-semibold mb-4 text-purple-700">Faculty Information</h3>
              <div className="flex items-center mb-4">
                <Avatar className="h-16 w-16 mr-4 border-2 border-purple-300">
                  <AvatarImage src={faculty.imageUrl} alt={faculty.name} />
                  <AvatarFallback>{faculty.name.charAt(0)}</AvatarFallback>
                </Avatar>
                <div>
                  <p className="font-semibold text-lg">{faculty.name}</p>
                  <p className="text-sm text-gray-600">{faculty.designation}</p>
                </div>
              </div>
              <div className="space-y-2 text-sm">
                <p className="flex items-center"><Mail className="w-4 h-4 mr-2 text-purple-600" /> {faculty.email}</p>
                <p className="flex items-center"><Phone className="w-4 h-4 mr-2 text-purple-600" /> {faculty.mobile}</p>
                <p className="flex items-center"><Briefcase className="w-4 h-4 mr-2 text-purple-600" /> {faculty.departmentType} - {faculty.subDepartment}</p>
              </div>
            </motion.div>
          </motion.div>
          
          <Separator className="my-8 bg-blue-200" />
          
          <motion.div variants={fadeInUp}>
            <h3 className="text-xl font-semibold mb-4 text-blue-700">Attending Students</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
              {students.map((student) => (
                <motion.div 
                  key={student.studentId}
                  variants={fadeInUp}
                  className="bg-white p-4 rounded-lg shadow-sm border border-blue-100"
                >
                  <div className="flex items-center mb-2">
                    <Avatar className="h-10 w-10 mr-3">
                      <AvatarFallback>{student.studentName.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <div>
                      <p className="font-semibold">{student.studentName}</p>
                      <p className="text-sm text-gray-600">{student.rollNo}</p>
                    </div>
                  </div>
                  <div className="text-sm space-y-1">
                    <p className="flex items-center"><Mail className="w-3 h-3 mr-1 text-blue-600" /> {student.email}</p>
                    <p className="flex items-center"><Phone className="w-3 h-3 mr-1 text-blue-600" /> {student.mobile}</p>
                    <p className="flex items-center"><GraduationCap className="w-3 h-3 mr-1 text-blue-600" /> Year {student.year}, Semester {student.semester}</p>
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
          <div className="flex justify-end absolute bottom-10 right-10">
            <div className="flex gap-2">
           <Button 
              className="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white"
              onClick={() => {
                router.push(`/notifications`)
              }}
            >
        
                <HelpCircle className="w-4 h-4 mr-2" />
                Get Help
             
            </Button>

            <Button 
            asChild
              className="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white"
              
            >
        
                <Link href={`${lecture.meetingLink}`} passHref target="_blank">
                  <Video className="w-4 h-4 mr-2" />
                    Join Class
                </Link>
             
            </Button>

           </div>
          </div>
        </CardContent>
      </Card>
    </motion.div>
  )
}