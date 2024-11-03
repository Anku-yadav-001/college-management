'use client'

import { useState, useEffect } from 'react'
import axios from 'axios'
import { format } from 'date-fns'
import { motion } from 'framer-motion'
import { Calendar, Tag, Plus, Search, Book, ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, X, Check, Clock, AlertCircle } from 'lucide-react'
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent } from "@/components/ui/card"
import Link from 'next/link'

export default function AssignmentList() {
  const [assignments, setAssignments] = useState([])
  const [filteredAssignments, setFilteredAssignments] = useState([])
  const [currentPage, setCurrentPage] = useState(1)
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedSemester, setSelectedSemester] = useState('')
  const [selectedYear, setSelectedYear] = useState('')
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [facultyName, setFacultyName] = useState('')
  const [assignmentType, setAssignmentType] = useState('')
  const [category, setCategory] = useState('')
  const [status, setStatus] = useState('')
  const [difficultyLevel, setDifficultyLevel] = useState('')
  const [appliedFilters, setAppliedFilters] = useState(0)

  const assignmentsPerPage = 10

  useEffect(() => {
    fetchAssignments()
  }, [])

  const fetchAssignments = async () => {
    try {
      const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/assignments/get-all-assignments`, { withCredentials: true })
      const sortedAssignments = response.data.sort((a, b) => new Date(b.startDate) - new Date(a.startDate))
      setAssignments(sortedAssignments)
      setFilteredAssignments(sortedAssignments)
    } catch (error) {
      console.error('Error fetching assignments:', error)
    }
  }

  const filterAssignments = () => {
    let filtered = assignments.filter(assignment =>
      assignment.subject.toLowerCase().includes(searchTerm.toLowerCase()) &&
      (selectedSemester === '' || assignment.semester.toString() === selectedSemester) &&
      (selectedYear === '' || assignment.year.toString() === selectedYear) &&
      (startDate === '' || new Date(assignment.startDate) >= new Date(startDate)) &&
      (endDate === '' || new Date(assignment.endDate) <= new Date(endDate)) &&
      (facultyName === '' || assignment.assignedBy.name.toLowerCase().includes(facultyName.toLowerCase())) &&
      (assignmentType === '' || assignment.assignmentType === assignmentType) &&
      (category === '' || assignment.category === category) &&
      (status === '' || assignment.status === status) &&
      (difficultyLevel === '' || assignment.difficultyLevel === difficultyLevel)
    )

    return filtered
  }

  useEffect(() => {
    const filtered = filterAssignments()
    setFilteredAssignments(filtered)
    setCurrentPage(1)

    let filterCount = 0
    if (searchTerm) filterCount++
    if (selectedSemester) filterCount++
    if (selectedYear) filterCount++
    if (startDate) filterCount++
    if (endDate) filterCount++
    if (facultyName) filterCount++
    if (assignmentType) filterCount++
    if (category) filterCount++
    if (status) filterCount++
    if (difficultyLevel) filterCount++
    setAppliedFilters(filterCount)
  }, [searchTerm, selectedSemester, selectedYear, startDate, endDate, facultyName, assignmentType, category, status, difficultyLevel, assignments])

  const clearFilters = () => {
    setSearchTerm('')
    setSelectedSemester('')
    setSelectedYear('')
    setStartDate('')
    setEndDate('')
    setFacultyName('')
    setAssignmentType('')
    setCategory('')
    setStatus('')
    setDifficultyLevel('')
  }

  const indexOfLastAssignment = currentPage * assignmentsPerPage
  const indexOfFirstAssignment = indexOfLastAssignment - assignmentsPerPage
  const currentAssignments = filteredAssignments.slice(indexOfFirstAssignment, indexOfLastAssignment)
  const totalPages = Math.ceil(filteredAssignments.length / assignmentsPerPage)

  const Pagination = ({ currentPage, totalPages, onPageChange }) => {
    return (
      <div className='flex items-center justify-between bg-white p-2 rounded-sm'>
        <div>
          <p className="text-sm text-muted-foreground">
            Showing {currentAssignments.length} of {filteredAssignments.length} assignment{filteredAssignments.length !== 1 ? 's' : ''}
          </p>
        </div>
        <div className="flex items-center justify-center space-x-2">
          <Button
            variant="outline"
            size="icon"
            onClick={() => onPageChange(1)}
            disabled={currentPage === 1}
          >
            <ChevronsLeft className="h-4 w-4" />
          </Button>
          <Button
            variant="outline"
            size="icon"
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 1}
          >
            <ChevronLeft className="h-4 w-4" />
          </Button>
          <span className="text-sm font-medium">
            Page {currentPage} of {totalPages}
          </span>
          <Button
            variant="outline"
            size="icon"
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
          >
            <ChevronRight className="h-4 w-4" />
          </Button>
          <Button
            variant="outline"
            size="icon"
            onClick={() => onPageChange(totalPages)}
            disabled={currentPage === totalPages}
          >
            <ChevronsRight className="h-4 w-4" />
          </Button>
        </div>
      </div>
    )
  }

  const getStatusIcon = (status) => {
    switch (status) {
      case 'Completed':
        return <Check className="w-3 h-3" />
      case 'In Progress':
        return <Clock className="w-3 h-3" />
      default:
        return <AlertCircle className="w-3 h-3" />
    }
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'Completed':
        return 'bg-green-50 text-green-700 border-green-200'
      case 'In Progress':
        return 'bg-yellow-50 text-yellow-700 border-yellow-200'
      default:
        return 'bg-red-50 text-red-700 border-red-200'
    }
  }

  return (
    <div className="z-10">
      <div className="bg-gradient-to-br from-purple-500 to-blue-600 text-white p-2 rounded-sm mb-4">
        <h1 className="text-xl font-bold">All Assignments at a Glance: Master Your Semester</h1>
      </div>

      <div className="space-y-4 h-[600px]">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-2">
          <Card className="shadow-none border-none">
            <Input
              type="text"
              placeholder="Search by subject"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full"
            />
          </Card>
          <Card className="shadow-none border-none">
            <Select value={selectedSemester} onValueChange={setSelectedSemester}>
              <SelectTrigger>
                <SelectValue placeholder="Select semester" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Semesters</SelectItem>
                {[1, 2, 3, 4, 5, 6, 7, 8].map(sem => (
                  <SelectItem key={sem} value={sem.toString()}>{`Semester ${sem}`}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Card>
          <Card className="shadow-none border-none">
            <Select value={selectedYear} onValueChange={setSelectedYear}>
              <SelectTrigger>
                <SelectValue placeholder="Select year" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Years</SelectItem>
                {[1, 2, 3, 4].map(year => (
                  <SelectItem key={year} value={year.toString()}>{`Year ${year}`}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Card>
          <Card className="shadow-none border-none">
            <Input
              type="date"
              placeholder="Start date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="w-full"
            />
          </Card>
          <Card className="shadow-none border-none">
            <Input
              type="date"
              placeholder="End date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="w-full"
            />
          </Card>
          <Card className="shadow-none border-none">
            <Input
              type="text"
              placeholder="Search by faculty name"
              value={facultyName}
              onChange={(e) => setFacultyName(e.target.value)}
              className="w-full"
            />
          </Card>
          <Card className="shadow-none border-none">
            <Select value={assignmentType} onValueChange={setAssignmentType}>
              <SelectTrigger>
                <SelectValue placeholder="Assignment type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Types</SelectItem>
                <SelectItem value="Assignment">Assignment</SelectItem>
                <SelectItem value="Important Questions">Important Questions</SelectItem>
                <SelectItem value="Unit Test">Unit Test</SelectItem>
                <SelectItem value="MST">MST</SelectItem>
              </SelectContent>
            </Select>
          </Card>
          <Card className="shadow-none border-none">
            <Select value={category} onValueChange={setCategory}>
              <SelectTrigger>
                <SelectValue placeholder="Category" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Categories</SelectItem>
                <SelectItem value="Theory">Theory</SelectItem>
                <SelectItem value="Practical">Practical</SelectItem>
                <SelectItem value="Numerical">Numerical</SelectItem>
                <SelectItem value="Hybrid">Hybrid</SelectItem>
              </SelectContent>
            </Select>
          </Card>
          <Card className="shadow-none border-none">
            <Select value={status} onValueChange={setStatus}>
              <SelectTrigger>
                <SelectValue placeholder="Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Statuses</SelectItem>
                <SelectItem value="Pending">Pending</SelectItem>
                <SelectItem value="Completed">Completed</SelectItem>
                <SelectItem value="In Progress">In Progress</SelectItem>
              </SelectContent>
            </Select>
          </Card>
          <Card className="shadow-none border-none">
            <Select value={difficultyLevel} onValueChange={setDifficultyLevel}>
              <SelectTrigger>
                <SelectValue placeholder="Difficulty Level" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Levels</SelectItem>
                <SelectItem value="High">High</SelectItem>
                <SelectItem value="Medium">Medium</SelectItem>
                <SelectItem value="Low">Low</SelectItem>
              </SelectContent>
            </Select>
          </Card>
          <Card className="shadow-none border-none">
            <div className="relative">
              <Button
                onClick={clearFilters}
                variant="outline"
                className="w-full"
              >
                Clear Filters
              </Button>
              {appliedFilters > 0 && (
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold rounded-full h-4 w-4 flex items-center justify-center">
                  {appliedFilters}
                </span>
              )}
            </div>
          </Card>
          <Card className="shadow-none border-none">
            <Link href="/create-assignment" passHref>
              <Button className="w-full text-sm" variant="outline">
                <Plus className="w-4 h-4 mr-2" /> Create Assignment
              </Button>
            </Link>
          </Card>
        </div>

        <div className="space-y-4 h-[450px] overflow-y-auto">
          {currentAssignments.map((assignment) => (
            <motion.div
              key={assignment.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3 }}
              className="bg-white shadow-md rounded-lg p-4 flex items-center justify-between relative overflow-hidden hover:shadow-lg"
            >
              <div className="flex items-center gap-4 flex-grow">
                <Avatar className="h-10 w-10">
                  <AvatarImage src={assignment.assignedBy.imageUrl} alt={assignment.assignedBy.name} />
                  <AvatarFallback>{assignment.assignedBy.name.charAt(0)}</AvatarFallback>
                </Avatar>

                <div className="flex-grow">
                  <div className='flex items-center gap-2'>
                  <h2 className="text-md font-semibold">{assignment.subject}</h2>
                  <Badge variant="outline" className="hidden sm:inline-flex bg-purple-400 text-white">
                    {assignment.assignmentType}
                  </Badge>
                  <Badge variant="outline" className="hidden sm:inline-flex bg-blue-400 text-white">
                    {assignment.category}
                  </Badge>
                  <Badge variant="outline" className="hidden sm:inline-flex bg-green-400 text-white">
                    {assignment.difficultyLevel}
                  </Badge>
                  </div>
                  <div className="flex items-center gap-4 text-sm text-gray-500">
                    <div className="flex items-center gap-1">
                      <Calendar className="w-4 h-4" />
                      {format(new Date(assignment.startDate), 'dd MMM, yyyy')} - {format(new Date(assignment.endDate), 'dd MMM, yyyy')}
                    </div>
                    <div className="flex items-center gap-1">
                      <Tag className="w-4 h-4" />
                      by {assignment.assignedBy.name}
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                {/* <div className="inline-flex flex-col items-center gap-1">
      <div className={`flex items-center gap-1.5 px-2 py-2 rounded-full border ${getStatusColor(assignment.status)}`}>
        {getStatusIcon(assignment.status)}
        <span className="text-xs">
          {assignment.questions.length} / {assignment.questions.length}
        </span>
      </div>
      <span className={`text-xs font-medium ${
        assignment.status === 'Completed' 
          ? 'text-green-600' 
          : assignment.status === 'In Progress' 
            ? 'text-yellow-600' 
            : 'text-red-600'
      }`}>
        {assignment.status.toUpperCase()}
      </span>
    </div> */}
                  <Link href={`/assignments/${assignment.id}`} passHref>
                    
                    <Button className="bg-gradient-to-br from-purple-500 to-blue-600 text-xs">
                      View Details
                    </Button>
                  </Link>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </div>

      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
      />
    </div>
  )
}