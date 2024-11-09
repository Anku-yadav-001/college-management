package com.yaduvanshi_brothers.api.controller;

import com.yaduvanshi_brothers.api.DTOs.AssignmentDTO;
import com.yaduvanshi_brothers.api.DTOs.LectureDTO;
import com.yaduvanshi_brothers.api.DTOs.OnlineClassDTO;
import com.yaduvanshi_brothers.api.DTOs.StudentDTO;
import com.yaduvanshi_brothers.api.entity.BranchesEntity;
import com.yaduvanshi_brothers.api.entity.FacultyEntity;
import com.yaduvanshi_brothers.api.entity.LectureEntity;
import com.yaduvanshi_brothers.api.entity.StudentEntity;
import com.yaduvanshi_brothers.api.service.AssignmentService;
import com.yaduvanshi_brothers.api.service.LectureService;
import com.yaduvanshi_brothers.api.service.OnlineClassService;
import com.yaduvanshi_brothers.api.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private OnlineClassService onlineClassService;

    @Autowired
    private LectureService lectureService;

    @GetMapping("/get-all-students")
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentEntity> students = studentService.getAllStudentService();

        List<StudentDTO> studentDTOs = students.stream().map(student -> {
            StudentDTO dto = new StudentDTO();
            dto.setStudentId(student.getStudentId());
            dto.setRollNo(student.getRollNo());
            dto.setStudentName(student.getStudentName());
            dto.setEmail(student.getEmail());
            dto.setMobile(student.getMobile());
            dto.setAge(student.getAge());
            dto.setAddress(student.getAddress());
            dto.setYear(student.getYear());
            dto.setSemester(student.getSemester());

            BranchesEntity branch = student.getBranch();
            if (branch != null) {
                dto.setBranchCode(branch.getBranchCode());
            }

            List<LectureDTO> lectureDTOs = student.getLectures().stream().map(lecture -> {
                LectureDTO lectureDTO = new LectureDTO();
                lectureDTO.setLectureId(lecture.getLectureId());
                lectureDTO.setYear(lecture.getYear());
                lectureDTO.setSemester(lecture.getSemester());
                lectureDTO.setDepartment(lecture.getDepartment());
                lectureDTO.setSubject(lecture.getSubject());
                lectureDTO.setStartFrom(lecture.getStartFrom());
                lectureDTO.setTill(lecture.getTill());
                lectureDTO.setRoomNumber(lecture.getRoomNumber());

                FacultyEntity faculty = lecture.getFaculty();
                if (faculty != null) {
                    lectureDTO.setFacultyId(faculty.getFacultyId());
                }

                return lectureDTO;
            }).collect(Collectors.toList());

            dto.setLectures(lectureDTOs);

            // Adding Online Classes
            List<OnlineClassDTO> onlineClassDTOs = student.getOnlineClasses().stream().map(onlineClass -> {
                OnlineClassDTO onlineClassDTO = new OnlineClassDTO();
                onlineClassDTO.setOnlineLectureId(onlineClass.getOnlineLectureId());
                onlineClassDTO.setSubject(onlineClass.getSubject());
                onlineClassDTO.setBranchCode(onlineClass.getBranch().getBranchCode()); // Now this will work
                onlineClassDTO.setSemester(onlineClass.getSemester());
                onlineClassDTO.setPlatforms(onlineClass.getPlatforms());
                onlineClassDTO.setMeetingLink(onlineClass.getMeetingLink());
                onlineClassDTO.setStartFrom(onlineClass.getStartFrom());
                onlineClassDTO.setEnd(onlineClass.getEnd());

                FacultyEntity faculty = onlineClass.getFaculty();
                if (faculty != null) {
                    onlineClassDTO.setFacultyId(faculty.getFacultyId());
                }

                return onlineClassDTO;
            }).collect(Collectors.toList());

            dto.setOnlineClasses(onlineClassDTOs); // Assuming you have a corresponding setter in StudentDTO

            List<AssignmentDTO> assignmentDTOs = student.getAssignments().stream().map(assignment -> {
                AssignmentDTO assignmentDTO = new AssignmentDTO();
                assignmentDTO.setId(assignment.getId());
                assignmentDTO.setYear(assignment.getYear());
                assignmentDTO.setSemester(assignment.getSemester());
                assignmentDTO.setSubject(assignment.getSubject());
                assignmentDTO.setStartDate(assignment.getStartDate());
                assignmentDTO.setEndDate(assignment.getEndDate());
                assignmentDTO.setQuestions(assignment.getQuestions());
                assignmentDTO.setAssignmentType(assignment.getAssignmentType());
                assignmentDTO.setCategory(assignment.getCategory());
                assignmentDTO.setStatus(assignment.getStatus());
                assignmentDTO.setTotalMarks(assignment.getTotalMarks());
                assignmentDTO.setDifficultyLevel(assignment.getDifficultyLevel());
                assignmentDTO.setNotes(assignment.getNotes());

                return assignmentDTO;
            }).collect(Collectors.toList());

            dto.setAssignments(assignmentDTOs);

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(studentDTOs);
    }


    @GetMapping("/get-student/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable int id) {
        Optional<StudentEntity> studentOpt = studentService.getStudentByIdService(id);

        if (studentOpt.isPresent()) {
            StudentEntity student = studentOpt.get();
            StudentDTO dto = new StudentDTO();
            dto.setStudentId(student.getStudentId());
            dto.setRollNo(student.getRollNo());
            dto.setStudentName(student.getStudentName());
            dto.setEmail(student.getEmail());
            dto.setMobile(student.getMobile());
            dto.setAge(student.getAge());
            dto.setAddress(student.getAddress());
            dto.setYear(student.getYear());
            dto.setSemester(student.getSemester());

            BranchesEntity branch = student.getBranch();
            if (branch != null) {
                dto.setBranchCode(branch.getBranchCode());
            }

            List<LectureDTO> lectureDTOs = student.getLectures().stream().map(lecture -> {
                LectureDTO lectureDTO = new LectureDTO();
                lectureDTO.setLectureId(lecture.getLectureId());
                lectureDTO.setYear(lecture.getYear());
                lectureDTO.setSemester(lecture.getSemester());
                lectureDTO.setDepartment(lecture.getDepartment());
                lectureDTO.setSubject(lecture.getSubject());
                lectureDTO.setStartFrom(lecture.getStartFrom());
                lectureDTO.setTill(lecture.getTill());
                lectureDTO.setRoomNumber(lecture.getRoomNumber());

                FacultyEntity faculty = lecture.getFaculty();
                if (faculty != null) {
                    lectureDTO.setFacultyId(faculty.getFacultyId());
                }

                return lectureDTO;
            }).collect(Collectors.toList());

            dto.setLectures(lectureDTOs);

            // Fetching online classes
            List<OnlineClassDTO> onlineClassDTOs = student.getOnlineClasses().stream().map(onlineClass -> {
                OnlineClassDTO onlineClassDTO = new OnlineClassDTO();
                onlineClassDTO.setOnlineLectureId(onlineClass.getOnlineLectureId());
                onlineClassDTO.setSubject(onlineClass.getSubject());
                onlineClassDTO.setBranchCode(onlineClass.getBranch().getBranchCode());
                onlineClassDTO.setSemester(onlineClass.getSemester());
                onlineClassDTO.setPlatforms(onlineClass.getPlatforms());
                onlineClassDTO.setMeetingLink(onlineClass.getMeetingLink());
                onlineClassDTO.setStartFrom(onlineClass.getStartFrom());
                onlineClassDTO.setEnd(onlineClass.getEnd());

                FacultyEntity faculty = onlineClass.getFaculty();
                if (faculty != null) {
                    onlineClassDTO.setFacultyId(faculty.getFacultyId());
                }

                return onlineClassDTO;
            }).collect(Collectors.toList());

            dto.setOnlineClasses(onlineClassDTOs); // Set the online classes in the DTO

            List<AssignmentDTO> assignmentDTOs = student.getAssignments().stream().map(assignment -> {
                AssignmentDTO assignmentDTO = new AssignmentDTO();
                assignmentDTO.setId(assignment.getId());
                assignmentDTO.setYear(assignment.getYear());
                assignmentDTO.setSemester(assignment.getSemester());
                assignmentDTO.setSubject(assignment.getSubject());
                assignmentDTO.setStartDate(assignment.getStartDate());
                assignmentDTO.setEndDate(assignment.getEndDate());
                assignmentDTO.setQuestions(assignment.getQuestions());
                assignmentDTO.setAssignmentType(assignment.getAssignmentType());
                assignmentDTO.setCategory(assignment.getCategory());
                assignmentDTO.setStatus(assignment.getStatus());
                assignmentDTO.setTotalMarks(assignment.getTotalMarks());
                assignmentDTO.setDifficultyLevel(assignment.getDifficultyLevel());
                assignmentDTO.setNotes(assignment.getNotes());

                return assignmentDTO;
            }).collect(Collectors.toList());

            dto.setAssignments(assignmentDTOs);

            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @PostMapping("/update-student/{studentId}")
    public ResponseEntity<StudentDTO> updateStudentById(@PathVariable int studentId, @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = studentService.updateStudentService(studentId, studentDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    // Delete student by ID
    @DeleteMapping("/delete-student/{studentId}")
    public ResponseEntity<String> deleteStudentById(@PathVariable int studentId) {
        studentService.deleteStudentService(studentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Student and all associations deleted successfully.");
    }

    @PostMapping("/add-student")
    public ResponseEntity<?> addStudent(@RequestBody StudentEntity studentEntity) {
        if (studentEntity.getBranch() == null) {
            return ResponseEntity.badRequest().body("A branch must be specified.");
        }

        BranchesEntity branch = studentEntity.getBranch();

        branch.getStudents().add(studentEntity);

        studentService.addStudentService(studentEntity);

        StudentDTO dto = new StudentDTO();
        dto.setStudentId(studentEntity.getStudentId());
        dto.setRollNo(studentEntity.getRollNo());
        dto.setStudentName(studentEntity.getStudentName());
        dto.setEmail(studentEntity.getEmail());
        dto.setMobile(studentEntity.getMobile());
        dto.setAge(studentEntity.getAge());
        dto.setAddress(studentEntity.getAddress());
        dto.setYear(studentEntity.getYear());
        dto.setSemester(studentEntity.getSemester());

        dto.setBranchCode(branch.getBranchCode());

        return ResponseEntity.status(HttpStatus.CREATED).body("Student added successfully");
    }


    @GetMapping("/online-classes-for-today")
    public ResponseEntity<List<OnlineClassDTO>> getClassesForToday() {
        List<OnlineClassDTO> onlineClassesForToday = onlineClassService.getOnlineClassesForToday();
        return ResponseEntity.ok(onlineClassesForToday);
    }

    @GetMapping("/lectures-for-today")
    public ResponseEntity<List<LectureDTO>> getLecturesForToday() {
        List<LectureEntity> lectures = lectureService.getLecturesForToday();

        // Map to DTO for returning in response
        List<LectureDTO> lectureDTOs = lectures.stream()
                .map(lecture -> {
                    LectureDTO dto = new LectureDTO();
                    dto.setLectureId(lecture.getLectureId());
                    dto.setYear(lecture.getYear());
                    dto.setSemester(lecture.getSemester());
                    dto.setDepartment(lecture.getDepartment());
                    dto.setSubject(lecture.getSubject());
                    dto.setStartFrom(lecture.getStartFrom());
                    dto.setTill(lecture.getTill());
                    dto.setRoomNumber(lecture.getRoomNumber());
                    dto.setFacultyId(lecture.getFaculty() != null ? lecture.getFaculty().getFacultyId() : null);
                    dto.setStudentIds(lecture.getStudents().stream()
                            .map(student -> student.getStudentId())
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(lectureDTOs);
    }

    @GetMapping("/get-today-assignments")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsForToday() {
        List<AssignmentDTO> assignments = assignmentService.getAssignmentsForToday();
        return ResponseEntity.ok(assignments);
    }
}
