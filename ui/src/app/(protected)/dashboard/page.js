'use client'

import { useState, useEffect } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Bell, Calendar, Clock, Coffee, Moon, Sun, User } from "lucide-react"
import axios from 'axios'
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar"
import Link from 'next/link'

export default function Dashboard() {
  const [lectures, setLectures] = useState([])
  const [onlineClasses, setOnlineClasses] = useState([])
  const [assignments, setAssignments] = useState([])
  const [faculty, setFaculty] = useState({})
  const [currentTime, setCurrentTime] = useState(new Date())

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000)
    return () => clearInterval(timer)
  }, [])

  useEffect(() => {
    const fetchAllData = async () => {
      try {
        axios.defaults.withCredentials = true
        
        const [lecturesRes, onlineClassesRes, assignmentsRes] = await Promise.all([
          axios.get(`${process.env.NEXT_PUBLIC_API_URL}/student/lectures-for-today`),
          axios.get(`${process.env.NEXT_PUBLIC_API_URL}/student/online-classes-for-today`),
          axios.get(`${process.env.NEXT_PUBLIC_API_URL}/student/get-today-assignments`)
        ])

        setLectures(lecturesRes.data)
        setOnlineClasses(onlineClassesRes.data)
        setAssignments(assignmentsRes.data)

        const facultyIds = new Set([
          ...lecturesRes.data.map(lecture => lecture.facultyId),
          ...onlineClassesRes.data.map(onlineClass => onlineClass.facultyId)
        ])

        const facultyData = await Promise.all(
          Array.from(facultyIds).map(id => 
            axios.get(`${process.env.NEXT_PUBLIC_API_URL}/faculty/faculty-by-id/${id}`)
          )
        )

        const facultyMap = facultyData.reduce((acc, res) => {
          acc[res.data.facultyId] = res.data
          return acc
        }, {})

        setFaculty(facultyMap)
      } catch (error) {
        console.error('Error fetching data:', error)
      }
    }

    fetchAllData()
  }, [])

  const formatDateTime = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleString('en-GB', {
      hour: '2-digit',
      minute: '2-digit',
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    }).replace(',', ' -')
  }

  const formatDuration = (milliseconds) => {
    const seconds = Math.floor(milliseconds / 1000)
    const minutes = Math.floor(seconds / 60)
    const hours = Math.floor(minutes / 60)
    return `${hours.toString().padStart(2, '0')}:${(minutes % 60).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}`
  }

  const renderAssignment = (assignment) => (
    <Card className="w-full mb-4 hover:shadow-lg transition-shadow" key={assignment.id}>
      <CardContent className="p-4 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="space-y-1">
            <h3 className="text-xl font-semibold">{assignment.subject}</h3>
            <div className="flex items-center gap-2">
              <div className="flex gap-2">
                <Badge variant="secondary" className="bg-purple-100 text-purple-700 hover:bg-purple-100">
                  {assignment.assignmentType}
                </Badge>
                <Badge variant="secondary" className="bg-blue-100 text-blue-700 hover:bg-blue-100">
                  Theory new
                </Badge>
                <Badge variant="secondary" className="bg-green-100 text-green-700 hover:bg-green-100">
                  {assignment.difficultyLevel}
                </Badge>
              </div>
            </div>

            <div className="text-sm text-muted-foreground">
             <div className="flex gap-1 items-center">
             <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4" />
                <span>{formatDateTime(assignment.startDate)}</span>
              </div>
              <span>|</span>
              <div className="flex items-center gap-1">
                {/* <Calendar className="w-4 h-4" /> */}
                <span>{formatDateTime(assignment.endDate)}</span>
              </div>
             </div>
              <div className="flex items-center gap-2">
                <User className="w-4 h-4" />
                <span>by {assignment.assignedBy.name}</span>
              </div>
            </div>
          </div>
        </div>
        <Link href={`/assignments/${assignment.id}`} passHref>
        <Button className="text-xs">View Details</Button>
        </Link>
      </CardContent>
    </Card>
  )

  const renderLecture = (lecture) => (
    <Card className="w-full mb-4 hover:shadow-lg transition-shadow" key={lecture.lectureId}>
      <CardContent className="p-4 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="space-y-1">
            <h3 className="text-xl font-semibold">{lecture.subject}</h3>
            <div className="flex items-center gap-2">
              <div className="flex gap-2">
                <Badge variant="secondary" className="bg-blue-100 text-blue-700 hover:bg-blue-100">
                  Classroom
                </Badge>
                <Badge variant="secondary" className="bg-purple-100 text-purple-700 hover:bg-purple-100"> 
                {`R-${lecture.roomNumber}`}
                </Badge>
              </div>
            </div>
            <div className="text-sm text-muted-foreground">
             <div className="flex gap-1 items-center">
             <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4" />
                <span>{formatDateTime(lecture.startFrom)}</span>
              </div>
              <span>|</span>
              <div className="flex items-center gap-1">
                {/* <Calendar className="w-4 h-4" /> */}
                <span>{formatDateTime(lecture.till)}</span>
              </div>
             </div>
              <div className="flex items-center gap-2">
                <User className="w-4 h-4" />
                <span>by {faculty[lecture.facultyId]?.name}</span>
              </div>
            </div>
          </div>
        </div>
        <Link href={`/lectures/${lecture.lectureId}`} passHref>
        <Button className="text-xs">View Details</Button>
        </Link>
      </CardContent>
    </Card>
  )

  const renderOnlineClass = (onlineClass) => {
    const startTime = new Date(onlineClass.startFrom)
    const endTime = new Date(onlineClass.end)
    const timeDiff = startTime.getTime() - currentTime.getTime()
    const minutesBefore = Math.floor(timeDiff / (1000 * 60))
    
    let buttonText = "Join Class"
    let isDisabled = true
    let showTimer = false
    let timerValue = "00:00:00"
    
    if (minutesBefore <= 5 && minutesBefore > 0) {
      isDisabled = false
      buttonText = `Join in ${minutesBefore} min`
    } else if (currentTime >= startTime && currentTime < endTime) {
      isDisabled = false
      buttonText = "Join Now"
      showTimer = true
      timerValue = formatDuration(currentTime.getTime() - startTime.getTime())
    } else if (currentTime >= endTime) {
      buttonText = "Lecture Ended"
      isDisabled = true
    }

    return (
      <Card className="w-full mb-4 hover:shadow-lg transition-shadow" key={onlineClass.onlineLectureId}>
        <CardContent className="p-4 flex items-center justify-between">
          <div className="flex items-center gap-4">
            <div className="space-y-1">
              <h3 className="text-xl font-semibold">{onlineClass.subject}</h3>
              <div className="flex items-center gap-2">
                <div className="flex gap-2">
                  <Badge variant="secondary" className="bg-green-100 text-green-700 hover:bg-green-100">
                    Online
                  </Badge>
                  <Badge variant="secondary" className="bg-purple-100 text-purple-700 hover:bg-purple-100">
                    {onlineClass.platforms}
                  </Badge>
                </div>
              </div>
      
              <div className="text-sm text-muted-foreground">
             <div className="flex gap-1 items-center">
             <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4" />
                <span>{formatDateTime(onlineClass.startFrom)}</span>
              </div>
              <span>|</span>
              <div className="flex items-center gap-1">
                {/* <Calendar className="w-4 h-4" /> */}
                <span>{formatDateTime(onlineClass.end)}</span>
              </div>
             </div>
              <div className="flex items-center gap-2">
                <User className="w-4 h-4" />
                <span>by {faculty[onlineClass.facultyId]?.name}</span>
              </div>
              <div className="flex items-center gap-2">
                  <Calendar className="w-4 h-4" />
                  <span>Branch: {onlineClass.branchCode}, Semester: {onlineClass.semester}</span>
                </div>
            </div>
            </div>
          </div>
          <div className="flex items-center gap-4">
            {showTimer && (
              <div className="flex flex-col items-end">
                <span className="text-sm text-gray-600">Lecture Live...</span>
                <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800 animate-pulse">
                  {timerValue}
                </span>
              </div>
            )}
            <div className="flex flex-col gap-2">
              {(currentTime >= endTime)?
              <Button 
                className={`text-xs bg-red-600 cursor-not-allowed`}
                // disabled={isDisabled}
              >
                {buttonText}
              </Button>
              :
              <Button 
              className={`text-xs ${isDisabled ? 'bg-gray-300' : 'bg-gradient-to-br from-purple-500 to-blue-600'}`}
                disabled={isDisabled}
            >
              {buttonText}
            </Button>
              }
              <Link href={`/online-classes/${onlineClass.onlineLectureId}`} passHref>
                <Button className="text-xs">View Details</Button>
              </Link>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }

 
const renderEmptyCards = () => (
  <div className="space-y-4">
    <Card className="w-full rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-shadow duration-300 h-[50vh]">
      <CardContent className="p-6 h-full">
        <div className="flex flex-col items-center justify-center space-y-2 h-full">
          <Bell className="w-8 h-8 text-purple-500 " />
          <h3 className="text-md font-semibold text-gray-800">No Announcements</h3>
          <p className="text-gray-600 text-center text-sm">
            Stay tuned! We'll notify you when there are new updates.
          </p>
        </div>
      </CardContent>
    </Card>

    <Card className="w-full rounded-lg overflow-hidden shadow-md hover:shadow-lg  h-[27vh]">
      <CardContent className="p-6 ">
        <div className="flex flex-col items-center justify-center space-y-4">
          <Calendar className="w-8 h-8 text-blue-500" />
          <h3 className="text-md font-semibold text-gray-800 text-center">Training & Placement <br/> Information</h3>
          <p className="text-gray-600 text-center text-sm">
            No Training and Placement Information available today here.
          </p>
        </div>
      </CardContent>
    </Card>
  </div>
)


  return (
    <div className="mx-auto">
      <div className="bg-gradient-to-br from-purple-500 to-blue-600 text-white p-2 rounded-sm mb-4">
        <h1 className="text-xl font-bold">Today's Schedule</h1>
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 h-[79vh]">
        <Card className="lg:col-span-3 space-y-6 h-full overflow-y-scroll p-2 rounded-sm">
          {(assignments.length > 0 || lectures.length > 0 || onlineClasses.length > 0) ? (
            <>
              {assignments.map(renderAssignment)}
              {lectures.map(renderLecture)}
              {onlineClasses.map(renderOnlineClass)}
            </>
          ) : (
            <div className="flex flex-col items-center justify-center h-full space-y-4">
              <Calendar className="w-16 h-16 text-purple-500" aria-hidden="true" />
              <span className="text-2xl font-semibold text-gray-700">No schedule for today</span>
              <p className="text-gray-500 text-center max-w-md">
                Enjoy your free time! Maybe catch up on some reading or work on a personal project.
              </p>
            </div>
          )}
        </Card>
        <div>
          {renderEmptyCards()}
        </div>
      </div>
    </div>
  )
}