'use client'

import { useState, useEffect } from 'react'
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Badge } from "@/components/ui/badge"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Button } from "@/components/ui/button"
import { motion, AnimatePresence } from 'framer-motion'
import { Loader2, Search, ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from 'lucide-react'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import { useRouter } from 'next/navigation'
import axios from 'axios'
import toast from 'react-hot-toast'

export default function Students() {
    const router = useRouter()
    const [name, setName] = useState('')
    const [rollNo, setRollNo] = useState('')
    const [department, setDepartment] = useState('all')
    const [semester, setSemester] = useState('all')
    const [year, setYear] = useState('all')
    const [activeFilters, setActiveFilters] = useState(0)
    const [currentPage, setCurrentPage] = useState(1)
    const [isLoading, setIsLoading] = useState(true)
    const [students, setStudents] = useState([])

    const departments = ['Computer Science', 'Electrical Engineering', 'Mechanical Engineering', 'Civil Engineering']
    const deptValues = ["CS101", "EE101", "ME101", "CE101"]
    const semesters = Array.from({ length: 8 }, (_, i) => i + 1)
    const years = Array.from({ length: 4 }, (_, i) => i + 1)

    const [filteredStudents, setFilteredStudents] = useState([])
    const [paginatedStudents, setPaginatedStudents] = useState([])

    const itemsPerPage = 10
    const totalPages = Math.ceil(filteredStudents.length / itemsPerPage)

    useEffect(() => {
        getStudents()
    }, [])

    async function getStudents() {
        try {
            setIsLoading(true)
            const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/student/get-all-students`, {
                withCredentials: true
            })
            setStudents(response.data)
            setFilteredStudents(response.data)
            setIsLoading(false)
            console.log(response.data)
        } catch (error) {
            console.error("Error fetching students:", error)
            toast.error("Error fetching students")
            setIsLoading(false)
        }
    }

    useEffect(() => {
        const filters = [name, rollNo, department, semester, year].filter(val => val !== 'all' && val !== '').length
        setActiveFilters(filters)

        const filtered = students.filter(student =>
            student.studentName.toLowerCase().includes(name.toLowerCase()) &&
            student.rollNo.toLowerCase().includes(rollNo.toLowerCase()) &&
            (department === 'all' || student.branchCode === department) &&
            (semester === 'all' || student.semester === parseInt(semester)) &&
            (year === 'all' || student.year === parseInt(year))
        )
        setFilteredStudents(filtered)
        setCurrentPage(1)
    }, [name, rollNo, department, semester, year, students])

    useEffect(() => {
        const startIndex = (currentPage - 1) * itemsPerPage
        const endIndex = startIndex + itemsPerPage
        setPaginatedStudents(filteredStudents.slice(startIndex, endIndex))
    }, [filteredStudents, currentPage])

    const resetFilters = () => {
        setName('')
        setRollNo('')
        setDepartment('all')
        setSemester('all')
        setYear('all')
    }

    const Pagination = ({ currentPage, totalPages, onPageChange }) => {
        return (
            <div className='flex items-center justify-between'>
                <div className='mt-4'>
                    <p className="text-sm text-muted-foreground">
                        Showing {paginatedStudents.length} of {filteredStudents.length} student{filteredStudents.length !== 1 ? 's' : ''}
                    </p>
                </div>
                <div className="flex items-center justify-center space-x-2 mt-4">
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

    return (
        <div className="w-full mx-auto space-y-8">
            <Card>
                <CardHeader className="bg-gradient-to-r from-purple-500 to-blue-500 text-white p-3 rounded-t-lg">
                    <CardTitle className="text-md font-bold">Exploring Tomorrows Innovators: The Students of College</CardTitle>
                </CardHeader>
                <CardContent className='p-2'>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-4">
                        <div className="relative">
                            <Input
                                placeholder="Search by name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="pl-10"
                            />
                            <Search className="absolute left-3 top-2.5 h-5 w-5 text-muted-foreground" />
                        </div>
                        <div className="relative">
                            <Input
                                placeholder="Search by roll number"
                                value={rollNo}
                                onChange={(e) => setRollNo(e.target.value)}
                                className="pl-10"
                            />
                            <Search className="absolute left-3 top-2.5 h-5 w-5 text-muted-foreground" />
                        </div>
                        <Select value={department} onValueChange={setDepartment}>
                            <SelectTrigger>
                                <SelectValue placeholder="Select department" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">All Departments</SelectItem>
                                {departments.map((dept, index) => (
                                    <SelectItem key={dept} value={deptValues[index]}>{dept}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                        <Select value={semester} onValueChange={setSemester}>
                            <SelectTrigger>
                                <SelectValue placeholder="Select semester" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">All Semesters</SelectItem>
                                {semesters.map((sem) => (
                                    <SelectItem key={sem} value={sem.toString()}>{`Semester ${sem}`}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                        <Select value={year} onValueChange={setYear}>
                            <SelectTrigger>
                                <SelectValue placeholder="Select year" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">All Years</SelectItem>
                                {years.map((y) => (
                                    <SelectItem key={y} value={y.toString()}>{`Year ${y}`}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                        <Button onClick={resetFilters} variant="outline" className="w-full">
                            Reset Filters
                        </Button>
                    </div>
                    <div className="flex items-center justify-end mb-4">
                        <Badge variant="secondary" className="ml-2">
                            {activeFilters} filter{activeFilters !== 1 ? 's' : ''} applied
                        </Badge>
                    </div>

                    <ScrollArea className="h-[450px] w-full rounded-md border p-2">
                        {isLoading ? (
                            <div className="flex items-center justify-center h-full">
                                <Loader2 className="h-8 w-8 animate-spin text-primary" />
                            </div>
                        ) : (
                            <AnimatePresence initial={false}>
                                {paginatedStudents.map((student) => (
                                    <motion.div
                                        key={student.studentId}
                                        initial={{ opacity: 0, y: 20 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        exit={{ opacity: 0, y: -20 }}
                                        transition={{ duration: 0.2 }}
                                        className="py-3 border-b last:border-b-0 hover:bg-muted/50 rounded-md px-2 transition-colors cursor-pointer"
                                        onClick={() => router.push(`/students/${(student.studentName).toLowerCase().replace(/\s+/g, '-')}/${student.studentId}`)}
                                    >
                                        <div className="flex justify-between items-center">
                                            <div>
                                                <p className="text-sm">{student.studentName}</p>
                                                <p className="text-sm text-muted-foreground">
                                                    Roll No: {student.rollNo} | {student?.branchCode} | Semester {student.semester} | Year {student.year}
                                                </p>
                                            </div>
                                        </div>
                                    </motion.div>
                                ))}
                            </AnimatePresence>
                        )}
                    </ScrollArea>
                    <Pagination
                        currentPage={currentPage}
                        totalPages={totalPages}
                        onPageChange={setCurrentPage}
                    />
                </CardContent>
            </Card>
        </div>
    )
}