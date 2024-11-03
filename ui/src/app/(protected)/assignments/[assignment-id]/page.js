'use client'

import { useState, useEffect } from 'react'
import axios from 'axios'
import { useParams } from 'next/navigation'
import { format } from 'date-fns'
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import { Calendar, Download, FileText, User, Briefcase, Mail, Phone, Clock, HelpCircle } from 'lucide-react'
import { useRouter } from 'next/navigation'

export default function AssignmentDetails() {
  const [assignment, setAssignment] = useState(null)
  const [loading, setLoading] = useState(true)
  const assignmentId = useParams()['assignment-id']
  const router = useRouter()

  useEffect(() => {
    const fetchAssignment = async () => {
      try {
        const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/assignments/get-assignment/${assignmentId}`, { withCredentials: true })
        setAssignment(response.data)
        setLoading(false)
      } catch (error) {
        console.error('Error fetching assignment:', error)
        setLoading(false)
      }
    }

    fetchAssignment()
  }, [assignmentId])

  if (loading) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>
  }

  if (!assignment) {
    return <div className="flex justify-center items-center h-screen">Assignment not found</div>
  }

  const downloadFile = async () => {
    try {
      const response = await axios.get(
        `${process.env.NEXT_PUBLIC_API_URL}/files/download-file/${assignmentId}`,
        {
          withCredentials: true,
          responseType: 'blob', 
        },
      );
  
      const blob = new Blob([response.data], { type: response.headers['content-type'] });
      const url = window.URL.createObjectURL(blob);
  
      const a = document.createElement('a');
      a.href = url;
  
      const contentDisposition = response.headers['content-disposition'];
      const filename = contentDisposition 
        ? contentDisposition.split('filename=')[1].replace(/"/g, '')
        : `${assignment.topic}.pdf`;
        
      a.download = filename;
  
      window.open(url, '_blank');
  
      document.body.appendChild(a);
      a.click();
  
      a.remove();
      window.URL.revokeObjectURL(url);
  
    } catch (error) {
      console.error("Error downloading file:", error);
    }
  };
  
  return (
    <div className="h-[86vh]">
      <Card className="overflow-hidden bg-gradient-to-br from-blue-50 to-purple-50 border-blue-200 rounded-md h-full">
        <CardHeader className="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-3">
          <CardTitle className="text-xl font-bold">⭐ {assignment.topic || "No Topic"}</CardTitle>
          <div className="flex items-center  text-blue-100">
            <Calendar className="w-4 h-4 mr-2" />
            <span>
              {format(new Date(assignment.startDate), 'PPP')} - {format(new Date(assignment.endDate), 'PPP')}
            </span>
          </div>
        </CardHeader>
        <CardContent className="p-4 h-full overflow-y-scroll">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="text-lg font-semibold mb-2 text-blue-700">Assignment Details</h3>
              <div className="space-y-2">
                <div className="flex items-center">
                  <Badge variant="outline" className="mr-2 bg-blue-100 text-blue-700 border-blue-300">Year {assignment.year}</Badge>
                  <Badge variant="outline" className="mr-2 bg-purple-100 text-purple-700 border-purple-300">Semester {assignment.semester}</Badge>
                  <Badge variant="outline" className="bg-indigo-100 text-indigo-700 border-indigo-300">{assignment.branchCode}</Badge>
                </div>
                <p className="text-sm"><span className="text-sm text-blue-600">Type:</span> {assignment.assignmentType}</p>
                <p className="text-sm"><span className="text-sm text-blue-600">Category:</span> {assignment.category}</p>
                <p className="text-sm"><span className="text-sm text-blue-600">Status:</span> {assignment.status}</p>
                <p className="text-sm"><span className="text-sm text-blue-600">Total Marks:</span> {assignment.totalMarks}</p>
                <p className="text-sm"><span className="text-sm text-blue-600">Difficulty:</span> {assignment.difficultyLevel}</p>
              </div>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-2 text-purple-700">Assigned By</h3>
              <div className="flex items-center mb-4">
                <Avatar className="h-16 w-16 mr-4">
                  <AvatarImage src={assignment.assignedBy.imageUrl} alt={assignment.assignedBy.name} />
                  <AvatarFallback>{assignment.assignedBy.name.charAt(0)}</AvatarFallback>
                </Avatar>
                <div>
                  <p className="font-semibold text-lg">{assignment.assignedBy.name}</p>
                  <p className="text-sm text-gray-600">{assignment.assignedBy.designation}</p>
                </div>
              </div>
              <div className="space-y-2 text-sm">
                <p className="flex items-center"><Mail className="w-4 h-4 mr-2 text-purple-600" /> {assignment.assignedBy.email}</p>
                <p className="flex items-center"><Phone className="w-4 h-4 mr-2 text-purple-600" /> {assignment.assignedBy.mobile}</p>
                <p className="flex items-center"><Briefcase className="w-4 h-4 mr-2 text-purple-600" /> {assignment.assignedBy.departmentType} - {assignment.assignedBy.subDepartment}</p>
              </div>
            </div>
          </div>
          
          <Separator className="my-6 bg-blue-200" />
          
          <div>
            <h3 className="text-lg font-semibold mb-2 text-blue-700">Questions</h3>
            <ol className="list-decimal list-inside space-y-2 text-gray-700">
              {assignment.questions.map((question, index) => (
                <li key={index}>{question}</li>
              ))}
            </ol>
          </div>
          
          {assignment.notes && (
            <>
              <Separator className="my-6 bg-blue-200" />
              <div>
                <h3 className="text-lg font-semibold mb-2 text-purple-700">Notes</h3>
                <p className="text-gray-700">{assignment.notes}</p>
              </div>
            </>
          )}
          
          <Separator className="my-6 bg-blue-200" />
          
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
              className="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white"
              onClick={downloadFile}
            >
                <Download className="w-4 h-4 mr-2" />
                Download Notes PDF
              
            </Button>
           </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
