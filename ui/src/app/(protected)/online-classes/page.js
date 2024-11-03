'use client'

import { useState, useEffect } from 'react'
import axios from 'axios'
import { format } from 'date-fns'
import { motion } from 'framer-motion'
import { Calendar, Tag, Plus, Search, Book, ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, X } from 'lucide-react'
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import Link from 'next/link'
import { Card, CardContent } from "@/components/ui/card"

export default function OnlineClassesManagement() {
    const [onlineClasses, setOnlineClasses] = useState([])
    const [filteredClasses, setFilteredClasses] = useState([])
    const [currentPage, setCurrentPage] = useState(1)
    const [searchTerm, setSearchTerm] = useState('')
    const [selectedSemester, setSelectedSemester] = useState('')
    const [startDate, setStartDate] = useState('')
    const [endDate, setEndDate] = useState('')
    const [facultyName, setFacultyName] = useState('')
    const [activeTab, setActiveTab] = useState('all')
    const [appliedFilters, setAppliedFilters] = useState(0)

    const classesPerPage = 10

    useEffect(() => {
        fetchOnlineClasses()
    }, [])

    const fetchOnlineClasses = async () => {
        try {
            const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/online-classes/get-all-online-classes`, { withCredentials: true })
            const classesWithFaculty = await Promise.all(response.data.map(async (onlineClass) => {
                const facultyResponse = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/faculty/faculty-by-id/${onlineClass.facultyId}`, { withCredentials: true })
                return { ...onlineClass, faculty: facultyResponse.data }
            }))
            const sortedClasses = classesWithFaculty.sort((a, b) => new Date(b.startFrom) - new Date(a.startFrom))
            setOnlineClasses(sortedClasses)
            setFilteredClasses(sortedClasses)
        } catch (error) {
            console.error('Error fetching online classes:', error)
        }
    }

    const getInitials = (name) => {
        return name
            .split(' ')
            .map(word => word[0])
            .join('')
            .toUpperCase()
            .slice(0, 2)
    }

    const filterClasses = () => {
        const now = new Date()
        let filtered = onlineClasses.filter(onlineClass =>
            onlineClass.subject.toLowerCase().includes(searchTerm.toLowerCase()) &&
            (selectedSemester === '' || onlineClass.semester.toString() === selectedSemester) &&
            (startDate === '' || new Date(onlineClass.startFrom) >= new Date(startDate)) &&
            (endDate === '' || new Date(onlineClass.end) <= new Date(endDate)) &&
            (facultyName === '' || onlineClass.faculty.name.toLowerCase().includes(facultyName.toLowerCase()))
        )

        if (activeTab === 'past') {
            filtered = filtered.filter(onlineClass => new Date(onlineClass.end) < now)
        } else if (activeTab === 'present') {
            filtered = filtered.filter(onlineClass =>
                new Date(onlineClass.startFrom) <= now && new Date(onlineClass.end) >= now
            )
        } else if (activeTab === 'future') {
            filtered = filtered.filter(onlineClass => new Date(onlineClass.startFrom) > now)
        }

        return filtered
    }

    useEffect(() => {
        const filtered = filterClasses()
        setFilteredClasses(filtered)
        setCurrentPage(1)

        let filterCount = 0
        if (searchTerm) filterCount++
        if (selectedSemester) filterCount++
        if (startDate) filterCount++
        if (endDate) filterCount++
        if (facultyName) filterCount++
        if (activeTab !== 'all') filterCount++
        setAppliedFilters(filterCount)
    }, [searchTerm, selectedSemester, startDate, endDate, facultyName, activeTab, onlineClasses])

    const clearFilters = () => {
        setSearchTerm('')
        setSelectedSemester('')
        setStartDate('')
        setEndDate('')
        setFacultyName('')
        setActiveTab('all')
    }

    const indexOfLastClass = currentPage * classesPerPage
    const indexOfFirstClass = indexOfLastClass - classesPerPage
    const currentClasses = filteredClasses.slice(indexOfFirstClass, indexOfLastClass)
    const totalPages = Math.ceil(filteredClasses.length / classesPerPage)

    const Pagination = ({ currentPage, totalPages, onPageChange }) => {
        return (
            <div className='flex items-center justify-between bg-white p-2  rounded-sm'>
                <div>
                    <p className="text-sm text-muted-foreground">
                        Showing {currentClasses.length} of {filteredClasses.length} class{filteredClasses.length !== 1 ? 'es' : ''}
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

    const getPlatformBadge = (platform) => {
        switch (platform) {
            case 'ZOOM':
                return <Badge variant="secondary" className="bg-blue-100 text-blue-800">Zoom</Badge>
            case 'MEET':
                return <Badge variant="secondary" className="bg-green-100 text-green-800">Google Meet</Badge>
            case 'MICROSOFT MEET':
                return <Badge variant="secondary" className="bg-purple-100 text-purple-800">Microsoft Teams</Badge>
            default:
                return <Badge variant="secondary">Online</Badge>
        }
    }

    return (
        <div className="z-10">
            <div className="bg-gradient-to-br from-purple-500 to-blue-600 text-white p-2 rounded-sm mb-4 ">
                <h1 className="text-xl font-bold">Online Classes Management</h1>
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
                   <Select
                        value={selectedSemester}
                        onValueChange={setSelectedSemester}
                    >
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
                        <Link href="/create-online-class" passHref>
                            <Button className="w-full text-sm" variant="outline">
                                <Plus className="w-4 h-4" /> Schedule Online Class
                            </Button>
                        </Link>
                    </Card>
                </div>

                <Tabs defaultValue="all" value={activeTab} onValueChange={setActiveTab}>
                   <TabsList className="grid w-full grid-cols-4 mb-4">
                        <TabsTrigger value="all" className={`${activeTab === 'all' ? 'bg-gradient-to-br from-purple-500 to-blue-600 text-white' : ''}`}>All Classes</TabsTrigger>
                        <TabsTrigger value="past" className={`${activeTab === 'past' ? 'bg-gradient-to-br from-purple-500 to-blue-600 text-white' : ''}`}>Past</TabsTrigger>
                        <TabsTrigger value="present" className={`${activeTab === 'present' ? 'bg-gradient-to-br from-purple-500 to-blue-600 text-white' : ''}`}>Present</TabsTrigger>
                        <TabsTrigger value="future" className={`${activeTab === 'future' ? 'bg-gradient-to-br from-purple-500 to-blue-600 text-white' : ''}`}>Future</TabsTrigger>
                    </TabsList>

                    <TabsContent value={activeTab} className="space-y-4 h-[450px] overflow-y-scroll">
                        {currentClasses.map((onlineClass) => (
                            <motion.div
                                key={onlineClass.onlineLectureId}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ duration: 0.3 }}
                                className="bg-white shadow-md rounded-lg p-4 flex items-center justify-between relative overflow-hidden hover:shadow-lg shadow-none border-none"
                            >
                                <div className="flex items-center gap-4 flex-grow">
                                    <Avatar className="h-10 w-10">
                                        <AvatarImage src={onlineClass.faculty.imageUrl} alt={onlineClass.faculty.name} />
                                        <AvatarFallback>{getInitials(onlineClass.faculty.name)}</AvatarFallback>
                                    </Avatar>

                                    <div className="flex-grow">
                                        <h2 className="text-md font-semibold">{onlineClass.subject}</h2>
                                        <div className="flex items-center gap-4 text-sm text-gray-500">
                                            <div className="flex items-center gap-1">
                                                <Calendar className="w-4 h-4" />
                                                {format(new Date(onlineClass.startFrom), 'dd MMM, yyyy, h:mm a')}
                                            </div>
                                            <div className="flex items-center gap-1">
                                                <Tag className="w-4 h-4" />
                                                 by {onlineClass.faculty.name}
                                            </div>
                                        </div>
                                    </div>

                                    <div className="flex items-center gap-2">
                                        {getPlatformBadge(onlineClass.platforms)}
                                        <Link href={`${onlineClass.meetingLink}`} passHref target="_blank">
                                            <Button className="bg-gradient-to-br from-purple-500 to-blue-600 text-xs">
                                                Join Class
                                            </Button>
                                        </Link>
                                        <Link href={`/online-classes/${onlineClass.onlineLectureId}`} passHref>
                                            <Button className="bg-gradient-to-br from-purple-500 to-blue-600 text-xs">
                                                View Details
                                            </Button>
                                        </Link>
                                    </div>
                                </div>
                            </motion.div>
                        ))}
                    </TabsContent>
                </Tabs>
            </div>

            <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={setCurrentPage}
            />
        </div>
    )

}


  