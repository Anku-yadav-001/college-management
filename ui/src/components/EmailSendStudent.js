import React, { useState, useCallback } from 'react'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useEditor, EditorContent } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import TextStyle from '@/components/ui/text-style'
import Color from '@tiptap/extension-color'
import Link from '@tiptap/extension-link'
import Image from '@tiptap/extension-image'
import TextAlign from '@tiptap/extension-text-align'
import { Bold, Heading, Type, LinkIcon, ImageIcon, AlignLeft, AlignCenter, AlignRight, List, ListOrdered, Undo, Redo } from 'lucide-react'
import { toast } from 'react-hot-toast'
import axios from 'axios'

export default function StudentEmail({ studentEmail = '' }) {
  const [emailTo, setEmailTo] = useState(studentEmail)
  const [emailSubject, setEmailSubject] = useState('')
  const [emailSuggestions, setEmailSuggestions] = useState([])
  const [attachments, setAttachments] = useState([])
  const [emailTemplates] = useState([
    { name: 'Assignment Submission', content: '<p>Dear Professor,</p><p>I am writing to submit my assignment for [Course Name]. Please find the attached document.</p><p>Best regards,</p><p>[Your Name]</p>' },
    { name: 'Office Hours Request', content: '<p>Dear Professor,</p><p>I would like to request a meeting during your office hours to discuss [Topic]. When would be a convenient time for you?</p><p>Thank you,</p><p>[Your Name]</p>' },
  ])

  const editor = useEditor({
    extensions: [
      StarterKit,
      TextStyle,
      Color,
      Link.configure({
        openOnClick: false,
      }),
      Image,
      TextAlign.configure({
        types: ['heading', 'paragraph'],
      }),
    ],
    content: '<p>Write your email content here...</p>',
    editorProps: {
      attributes: {
        class: 'prose prose-sm sm:prose lg:prose-lg xl:prose-2xl mx-auto focus:outline-none min-h-[200px] p-4',
      },
    },
  })

  const handleEmailChange = (e) => {
    const value = e.target.value
    setEmailTo(value)
    
    if (value.includes('@')) {
      const [username, domain] = value.split('@')
      if (domain) {
        setEmailSuggestions([
          `${username}@university.edu`,
          `${username}@student.university.edu`,
          `${username}@alumni.university.edu`,
        ])
      } else {
        setEmailSuggestions([])
      }
    } else {
      setEmailSuggestions([])
    }
  }

  const handleAttachmentChange = (e) => {
    if (e.target.files) {
      setAttachments(prev => [...prev, ...Array.from(e.target.files)])
    }
  }

  const removeAttachment = (index) => {
    setAttachments(prev => prev.filter((_, i) => i !== index))
  }

  const sendEmail = async (e) => {
    e.preventDefault()
    try {
      const emailContent = editor?.getHTML()
      console.log("Sending email:", { to: emailTo, subject: emailSubject, content: emailContent, attachments })
      const response = await axios.post(`${process.env.NEXT_PUBLIC_API_URL}/send-mail`, { to: emailTo, subject: emailSubject, body: emailContent }, {
        withCredentials: true
      })
      console.log(response.data);
      toast("Email sent successfully to " + emailTo + "🎊")
    } catch (error) {
      console.error("Error sending email:", error)
      toast("Failed to send email. Please try again.")
    }
  }

  const setLink = useCallback(() => {
    const previousUrl = editor?.getAttributes('link').href
    const url = window.prompt('URL', previousUrl)

    if (url === null) {
      return
    }

    if (url === '') {
      editor?.chain().focus().extendMarkRange('link').unsetLink().run()
      return
    }

    editor?.chain().focus().extendMarkRange('link').setLink({ href: url }).run()
  }, [editor])

  const applyTemplate = useCallback((template) => {
    if (editor) {
      editor.commands.setContent(template.content)
    }
  }, [editor])

  return (
    <form onSubmit={sendEmail} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="emailTo">To (Professors Email)</Label>
        <Input id="emailTo" name="emailTo" value={emailTo} onChange={handleEmailChange} className="w-full" />
        {emailSuggestions.length > 0 && (
          <ul className="mt-1 bg-white border rounded-md shadow-sm">
            {emailSuggestions.map((suggestion, index) => (
              <li
                key={index}
                className="px-3 py-2 cursor-pointer hover:bg-gray-100"
                onClick={() => setEmailTo(suggestion)}
              >
                {suggestion}
              </li>
            ))}
          </ul>
        )}
      </div>
      <div className="space-y-2">
        <Label htmlFor="emailSubject">Subject</Label>
        <Input 
          id="emailSubject" 
          name="emailSubject"
          value={emailSubject} 
          onChange={(e) => setEmailSubject(e.target.value)} 
          className="w-full" 
          placeholder="e.g., Question about Assignment 3"
        />
      </div>
      <div className="space-y-2">
        <Label htmlFor="emailTemplate">Email Templates</Label>
        <Select onValueChange={(value) => {
          const template = emailTemplates.find(t => t.name === value)
          if (template) applyTemplate(template)
        }}>
          <SelectTrigger id="emailTemplate" className="w-full">
            <SelectValue placeholder="Select a template" />
          </SelectTrigger>
          <SelectContent>
            {emailTemplates.map((template, index) => (
              <SelectItem key={index} value={template.name}>{template.name}</SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
      <div className="space-y-2">
        <Label htmlFor="emailBody">Email Body</Label>
        <div className="border rounded-md">
          <div className="flex flex-wrap gap-2 p-2 border-b">
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => editor?.chain().focus().toggleBold().run()}
              className={editor?.isActive('bold') ? 'bg-gray-200' : ''}
            >
              <Bold className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => editor?.chain().focus().toggleHeading({ level: 2 }).run()}
              className={editor?.isActive('heading', { level: 2 }) ? 'bg-gray-200' : ''}
            >
              <Heading className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => editor?.chain().focus().setHardBreak().run()}
            >
              <Type className="h-4 w-4" />
            </Button>
            <Select onValueChange={(value) => editor?.chain().focus().setColor(value).run()}>
              <SelectTrigger className="w-[100px]">
                <SelectValue placeholder="Color" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="#000000">Black</SelectItem>
                <SelectItem value="#0000FF">Blue</SelectItem>
                <SelectItem value="#FF0000">Red</SelectItem>
                <SelectItem value="#008000">Green</SelectItem>
              </SelectContent>
            </Select>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={setLink}
              className={editor?.isActive('link') ? 'bg-gray-200' : ''}
            >
              <LinkIcon className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => {
                const url = window.prompt('Enter the image URL')
                if (url) {
                  editor?.chain().focus().setImage({ src: url }).run()
                }
              }}
            >
              <ImageIcon className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => editor?.chain().focus().setTextAlign('left').run()}
              className={editor?.isActive({ textAlign: 'left' }) ? 'bg-gray-200' : ''}
            >
              <AlignLeft className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => editor?.chain().focus().setTextAlign('center').run()}
              className={editor?.isActive({ textAlign: 'center' }) ? 'bg-gray-200' : ''}
            >
              <AlignCenter className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => editor?.chain().focus().setTextAlign('right').run()}
              className={editor?.isActive({ textAlign: 'right' }) ? 'bg-gray-200' : ''}
            >
              <AlignRight className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => editor?.chain().focus().toggleBulletList().run()}
              className={editor?.isActive('bulletList') ? 'bg-gray-200' : ''}
            >
              <List className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => editor?.chain().focus().toggleOrderedList().run()}
              className={editor?.isActive('orderedList') ? 'bg-gray-200' : ''}
            >
              <ListOrdered className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => editor?.chain().focus().undo().run()}
            >
              <Undo className="h-4 w-4" />
            </Button>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={() => editor?.chain().focus().redo().run()}
            >
              <Redo className="h-4 w-4" />
            </Button>
          </div>
          {editor && (
            <EditorContent editor={editor} />
          )}
        </div>
      </div>
      <div className="space-y-2">
        <Label htmlFor="attachment">Attachments (e.g., assignments, documents)</Label>
        <Input id="attachment" name="attachment" type="file" multiple onChange={handleAttachmentChange} className="w-full" />
        {attachments.length > 0 && (
          <div className="mt-2">
            <h4 className="text-sm font-medium">Attached Files:</h4>
            <ul className="list-disc pl-5">
              {attachments.map((file, index) => (
                <li key={index} className="text-sm">
                  {file.name}
                  <Button
                    type="button"
                    size="sm"
                    variant="ghost"
                    onClick={() => removeAttachment(index)}
                    className="ml-2 text-red-500"
                  >
                    Remove
                  </Button>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
      <Button type="submit" className="w-full mt-4">Send Email</Button>
    </form>
  )
}