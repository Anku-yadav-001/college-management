package com.yaduvanshi_brothers.api.controller;

import com.yaduvanshi_brothers.api.DTOs.*;
import com.yaduvanshi_brothers.api.entity.FacultyEntity;
import com.yaduvanshi_brothers.api.entity.LectureEntity;
import com.yaduvanshi_brothers.api.entity.StudentEntity;
import com.yaduvanshi_brothers.api.service.FacultyService;
import com.yaduvanshi_brothers.api.service.LectureService;
import com.yaduvanshi_brothers.api.service.StudentService;
import com.yaduvanshi_brothers.api.utils.UploadImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/faculty")
@Slf4j
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private LectureService lectureService;

    @Autowired
    private UploadImageUtil uploadImageUtil;

    private static final Logger logger = LoggerFactory.getLogger(FacultyController.class);


    @GetMapping("/get-all-faculties")
    public List<FacultyDTO> getAllFaculties() {
        List<FacultyEntity> faculties = facultyService.allFacultyService1();

        return faculties.stream().map(faculty -> {
            FacultyDTO dto = new FacultyDTO();
            dto.setFacultyId(faculty.getFacultyId());
            dto.setName(faculty.getName());
            dto.setMobile(faculty.getMobile());
            dto.setEmail(faculty.getEmail());
            dto.setSalary(faculty.getSalary());
            dto.setDesignation(faculty.getDesignation());
            dto.setGender(faculty.getGender());
            dto.setDepartmentType(faculty.getDepartmentType());
            dto.setSubDepartment(faculty.getSubDepartment());
            dto.setBranchCode(faculty.getBranch().getBranchCode());

            // Retrieve image URL
            if (faculty.getImageEntity() != null) {
                dto.setImageUrl("http://localhost:8080/image/" + faculty.getImageEntity().getId());
            } else {
                dto.setImageUrl("default/image/url");
            }

            // Set Lecture IDs
            List<Integer> lectureIds = faculty.getLectures().stream()
                    .map(LectureEntity::getLectureId)
                    .collect(Collectors.toList());
            dto.setLectureIds(lectureIds);

            // Map lectures to LectureDTO
            List<LectureDTO> lectureDTOs = faculty.getLectures().stream().map(lecture -> {
                LectureDTO lectureDTO = new LectureDTO();
                lectureDTO.setLectureId(lecture.getLectureId());
                lectureDTO.setYear(lecture.getYear());
                lectureDTO.setSemester(lecture.getSemester());
                lectureDTO.setDepartment(lecture.getDepartment());
                lectureDTO.setSubject(lecture.getSubject());
                lectureDTO.setStartFrom(lecture.getStartFrom());
                lectureDTO.setTill(lecture.getTill());
                lectureDTO.setRoomNumber(lecture.getRoomNumber());
                lectureDTO.setFacultyId(faculty.getFacultyId());

                List<Integer> studentIds = lecture.getStudents().stream()
                        .map(StudentEntity::getStudentId)
                        .collect(Collectors.toList());
                lectureDTO.setStudentIds(studentIds);

                return lectureDTO;
            }).collect(Collectors.toList());

            dto.setLectures(lectureDTOs);

            // Map online classes to OnlineClassDTO
            List<OnlineClassDTO> onlineClassDTOs = faculty.getOnlineClasses().stream().map(onlineClass -> {
                OnlineClassDTO onlineClassDTO = new OnlineClassDTO();
                onlineClassDTO.setOnlineLectureId(onlineClass.getOnlineLectureId());
                onlineClassDTO.setSubject(onlineClass.getSubject());
                onlineClassDTO.setSemester(onlineClass.getSemester());
                onlineClassDTO.setPlatforms(onlineClass.getPlatforms());
                onlineClassDTO.setMeetingLink(onlineClass.getMeetingLink());
                onlineClassDTO.setStartFrom(onlineClass.getStartFrom());
                onlineClassDTO.setEnd(onlineClass.getEnd());

                List<Integer> studentIds = onlineClass.getStudents().stream()
                        .map(StudentEntity::getStudentId)
                        .collect(Collectors.toList());
                onlineClassDTO.setStudentIds(studentIds); // Optionally include student IDs

                return onlineClassDTO;
            }).collect(Collectors.toList());

            dto.setOnlineClasses(onlineClassDTOs); // Set online classes in DTO

            // Fetch and map announcements
            List<AnnouncementDTO> announcementDTOs = faculty.getAnnouncements().stream().map(announcement -> {
                AnnouncementDTO announcementDTO = new AnnouncementDTO();
                announcementDTO.setAnnouncementId(announcement.getAnnouncementId());
                announcementDTO.setTitle(announcement.getTitle());
                announcementDTO.setDescription(announcement.getDescription());
                announcementDTO.setBranchId(announcement.getBranch().getBranchCode());
                announcementDTO.setAnnouncerId(announcement.getAnnouncer().getFacultyId());
                announcementDTO.setCreatedAt(announcement.getCreatedAt());
                announcementDTO.setIsActive(announcement.getIsActive());
                announcementDTO.setExpirationDate(announcement.getExpirationDate());
                return announcementDTO;
            }).collect(Collectors.toList());

            dto.setAnnouncements(announcementDTOs); // Set announcements in DTO

            // Map assignments to AssignmentDTO
            List<AssignmentDTO> assignmentDTOs = faculty.getAssignments().stream().map(assignment -> {
                AssignmentDTO assignmentDTO = new AssignmentDTO();
                assignmentDTO.setId(assignment.getId());
                assignmentDTO.setYear(assignment.getYear());
                assignmentDTO.setSemester(assignment.getSemester());
                assignmentDTO.setSubject(assignment.getSubject());
                assignmentDTO.setStartDate(assignment.getStartDate());
                assignmentDTO.setEndDate(assignment.getEndDate());
                assignmentDTO.setAssignmentType(assignment.getAssignmentType());
                assignmentDTO.setTotalMarks(assignment.getTotalMarks());
                // Add any other fields you want to include in AssignmentDTO
                return assignmentDTO;
            }).collect(Collectors.toList());

            dto.setAssignments(assignmentDTOs); // Set assignments in DTO

            return dto;
        }).collect(Collectors.toList());
    }


    @PostMapping("/add-faculty")
    public ResponseEntity<String> addFaculty(@ModelAttribute FacultyEntity facultyEntity,
                                             @RequestParam("image") MultipartFile imageFile) {
        System.out.println("adding faculty");
        try {
            facultyService.addFacultyService(facultyEntity, imageFile);
            return ResponseEntity.ok("Faculty added successfully");
        } catch (IOException e) {
            System.out.println("Error occurred while adding faculty: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-faculty/{facultyId}")
    public ResponseEntity<String> deleteFaculty(@PathVariable Integer facultyId) {
        try {
            facultyService.deleteFacultyService(facultyId);
            return ResponseEntity.ok("Faculty deleted successfully");
        } catch (Exception e) {
            logger.error("Error occurred while deleting faculty: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting faculty: " + e.getMessage());
        }
    }


    @GetMapping("/faculty-by-id/{id}")
    public ResponseEntity<FacultyDTO> getFacultyById(@PathVariable int id) {
        Optional<FacultyDTO> facultyDTO = facultyService.getFacultyByIdService(id);
        if (facultyDTO.isPresent()) {
            return ResponseEntity.ok(facultyDTO.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping("/update-faculty-by-id/{id}")
    public ResponseEntity<String> updateFaculty(
            @PathVariable Integer id,
            @ModelAttribute FacultyEntity facultyEntity,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            facultyService.updateFacultyService(id, facultyEntity, imageFile);
            return ResponseEntity.ok("Faculty updated successfully");
        } catch (IOException e) {
            System.out.println("Error occurred while updating faculty: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions (like branch not found, etc.)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }





    @PostMapping("/add-new-student")
    public ResponseEntity<String> addStudentController(@RequestBody StudentEntity student){
        studentService.addStudentService(student);
        return new ResponseEntity<>("Student added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/get-student-by-id/{id}")
    public ResponseEntity<Optional<StudentEntity>> getStudentByIdController(@PathVariable int id){
        Optional<StudentEntity> student = studentService.getStudentByIdService(id);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

//    @PostMapping("/update-student-by-id/{id}")
//    public ResponseEntity<String> updateStudentById(@PathVariable int id, @RequestBody StudentEntity student){
//        Optional<StudentEntity> studentInDb = studentService.getStudentByIdService(id);
//        if (studentInDb.isPresent()) {
//            StudentEntity existingStudent = studentInDb.get();
//            existingStudent.setRollNo(student.getRollNo());
//            existingStudent.setStudentName(student.getStudentName());
//            existingStudent.setEmail(student.getEmail());
//            existingStudent.setMobile(student.getMobile());
//            existingStudent.setAge(student.getAge());
//            existingStudent.setAddress(student.getAddress());
//            existingStudent.setYear(student.getYear());
//            existingStudent.setSemester(student.getSemester());
//            existingStudent.setBranch(student.getBranch());
//
//            studentService.updateStudentById(existingStudent);
//            return new ResponseEntity<>("Student details updated successfully", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
//        }
//    }

    @PostMapping("/create-lecture")
    public ResponseEntity<?> createLecture(@RequestBody LectureEntity lecture) {
        LectureEntity createdLecture = lectureService.createLecture(lecture);
        return ResponseEntity.status(HttpStatus.CREATED).body("lecture created");
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteLecture(@PathVariable int id) {
//        lectureService.deleteLecture(id);
//        return ResponseEntity.noContent().build();
//    }

    // Get all lectures with pagination and sorting
    @GetMapping("/get-all-lectures")
    public List<LectureDTO> getAllLectures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "lectureId") String sortBy
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<LectureEntity> lecturesPage = lectureService.getAllLectures(PageRequest.of(page, size, Sort.by(direction, sortBy)));

        return lecturesPage.stream().map(lecture -> {
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
            dto.setStudentIds(lecture.getStudents().stream().map(StudentEntity::getStudentId).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LectureDTO> getLectureById(@PathVariable int id) {
        LectureEntity lecture = lectureService.getLectureById(id);
        if (lecture != null) {
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
            dto.setStudentIds(lecture.getStudents().stream().map(StudentEntity::getStudentId).collect(Collectors.toList()));
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Search lecture by subject name
    @GetMapping("/search-by-subject")
    public List<LectureDTO> searchLecturesBySubject(@RequestParam String subject) {
        List<LectureEntity> lectures = lectureService.findLecturesBySubject(subject);
        return lectures.stream().map(lecture -> {
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
            dto.setStudentIds(lecture.getStudents().stream().map(StudentEntity::getStudentId).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/sort")
    public List<LectureDTO> sortLectures(
            @RequestParam(defaultValue = "lectureId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<LectureEntity> lectures = lectureService.sortLectures(Sort.by(direction, sortBy));

        return lectures.stream().map(lecture -> {
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
            dto.setStudentIds(lecture.getStudents().stream().map(StudentEntity::getStudentId).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }

}
