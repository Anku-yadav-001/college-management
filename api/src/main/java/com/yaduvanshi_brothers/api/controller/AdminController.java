package com.yaduvanshi_brothers.api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import com.yaduvanshi_brothers.api.DTOs.LectureDTO;
import com.yaduvanshi_brothers.api.entity.*;
import com.yaduvanshi_brothers.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "https://college-management-eight.vercel.app")
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private LectureService lectureService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private  EmailService emailService;

    String pass = null;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentHashMap<String, UserEntity> userMap = new ConcurrentHashMap<>();



    @PostMapping("/add-branch")
    public String addBranchController(@RequestBody BranchesEntity branchData){
        branchService.addBranchService(branchData);
        return "Branch added successfully";
    }

//    @PostMapping("/add-faculty")
//    public String addFacultyController(@RequestBody FacultyEntity facultyEntity){
//        facultyService.addFacultyService(facultyEntity);
//        return "Faculty added successfully";
//    }

//    @CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
    @GetMapping("/get-all-users-on-this-website")
    public ResponseEntity<List<UserEntity>> userListController(){
        List<UserEntity> allUsers = userService.allUsersService();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @PostMapping("/add-user")
    public ResponseEntity<String> userUserByAdminController(@RequestBody UserEntity userEntity) {
        // Add user by admin service
        pass = userEntity.getPassword();
        System.out.println("user adding by admin "+ userEntity.getPassword());
        userService.adduserByAdminService(userEntity);
        String username = userEntity.getUsername();
        String capitalizedUsername = username.substring(0, 1).toUpperCase() + username.substring(1);

        String to = userEntity.getEmail();
        String subject = "Welcome to College Management Application";

        String body = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.5; color: #000000; }"
                + "h3 { color: #4CAF50; font-size: 20px; }"
                + "p { font-size: 12px; color: #000000; line-height: 1.2; }"
                + "h4 { font-size: 13px; color: #000000; line-height: 1.5; }"
                + ".email-body { padding: 20px; border: 1px solid #ddd; border-radius: 5px; }"
                + ".email-footer { margin-top: 20px; padding-top: 10px; border-top: 1px solid #ddd; color: #000000; }"
                + ".logo { max-width: 100px; margin-bottom: 20px; border-radius: 50%; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='email-body'>"
                + "<img src='https://w7.pngwing.com/pngs/1005/782/png-transparent-student-college-university-term-paper-student-management-angle-people-logo.png' class='logo' alt='College Logo' />"
                + "<h3>Welcome, " + capitalizedUsername + "!</h3>"
                + "<p>We are excited to inform you that your registration has been successfully completed.</p>"
                + "<p>You will soon receive your user_id and password, which will give you access to our system.</p>"
                + "<h4>If you have any questions or need assistance, feel free to reach out to our support team.</h4>"
                + "<div class='email-footer'>"
                + "<p>Best Regards,</p>"
                + "<p><strong>College Management Team</strong></p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";


        emailService.sendMailService(to, subject, body);

        userMap.put(userEntity.getEmail(), userEntity);

        scheduleCredentialEmail(userEntity);

        System.out.println("user added by admin ");
        return new ResponseEntity<>("User added successfully", HttpStatus.CREATED);
    }

    private void scheduleCredentialEmail(UserEntity userEntity) {
        System.out.println("inside the schedule email credentials "+userEntity.getPassword());
        scheduler.schedule(() -> sendCredentialEmail(userEntity), 2, TimeUnit.MINUTES);
    }
    private void sendCredentialEmail(UserEntity userEntity) {

        System.out.println("inside the send scheduled email "+userEntity.getPassword());
        String username = userEntity.getUsername();
        String password = userEntity.getPassword();
        String role = userEntity.getRoles();

        String capitalizedUsername = username.substring(0, 1).toUpperCase() + username.substring(1);

        String credentialBody = createCredentialEmailBody(capitalizedUsername,role, username);
        String to = userEntity.getEmail();
        String subject = "Your Access Credentials";

        emailService.sendMailService(to, subject, credentialBody);
    }

    private String createCredentialEmailBody(String username, String role, String name) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.5; color: #333; background-color: #f4f4f4; margin: 0; padding: 0; }"
                + ".container { width: 100%; padding: 20px; }"
                + ".email-body { background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }"
                + "h3 { color: #4CAF50; font-size: 24px; margin-bottom: 10px; }"
                + "p { font-size: 14px; color: #555; line-height: 1.6; }"
                + "h4 { font-size: 16px; color: #333; margin: 15px 0; }"
                + "ul { list-style-type: none; padding: 0; }"
                + "li { background-color: #e7f7e8; margin: 10px 0; padding: 10px; border-radius: 5px; }"
                + ".email-footer { margin-top: 20px; padding-top: 10px; border-top: 1px solid #ddd; color: #777; font-size: 12px; text-align: center; }"
                + ".button { display: inline-block; padding: 10px 20px; color: white; background-color: #4CAF50; border: none; border-radius: 5px; text-decoration: none; font-weight: bold; }"
                + ".logo { max-width: 120px; margin-bottom: 20px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='email-body'>"
                + "<img src='https://w7.pngwing.com/pngs/1005/782/png-transparent-student-college-university-term-paper-student-management-angle-people-logo.png' class='logo' alt='College Logo' />"
                + "<h3>Hey " + username + ",</h3>"
                + "<p>We’re thrilled to welcome you to our platform! It’s time to get started.</p>"
                + "<h4>Your Credentials:</h4>"
                + "<ul>"
                + "<li><strong>Username:</strong> " + name + "</li>"
                + "<li><strong>Password:</strong> " + pass + "</li>"
                + "<li><strong>Role:</strong> " + role + "</li>"
                + "</ul>"
                + "<p style='text-align: center;'>"
                + "<a href='#' class='button'>Access Platform</a>"
                + "</p>"
                + "<div class='email-footer'>"
                + "<p>Best Regards,</p>"
                + "<p><strong>College Management Team</strong></p>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }



    @PatchMapping("/update-user-by-id/{id}")
    public ResponseEntity<String> updateUserByIdController(@RequestBody UserEntity updatedData, @PathVariable int id){
        Optional<UserEntity> user = userService.findByuserByIdService(id);
        if(user.isPresent()){
            UserEntity userInDB = user.get();
            userInDB.setUsername(updatedData.getUsername());
            userInDB.setPassword(updatedData.getPassword());
            userInDB.setRoles(updatedData.getRoles());
            userInDB.setEmail(updatedData.getEmail());
            userInDB.setPhone(updatedData.getPhone());
            userInDB.setCity(updatedData.getCity());
            userService.updateUserService(userInDB);
            return new ResponseEntity<>("User updated successfully", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete-user-by-id/{id}")
    public ResponseEntity<String> deleteUserByIdController(@PathVariable int id){
        Optional<UserEntity> user = userService.findByuserByIdService(id);
        if(user.isPresent()){
            userService.deleteUserByIdService(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("User not exist", HttpStatus.NOT_FOUND);
    }

//    @DeleteMapping("/delete-student-by-id/{id}")
//    public ResponseEntity<String> deleteStudentByIdController(@PathVariable int id){
//        Optional<StudentEntity> student = studentService.getStudentByIdService(id);
//        if (student.isPresent()) {
//            studentService.deleteStudentById(id);
//            return new ResponseEntity<>("Student deleted successfully", HttpStatus.OK);
//        }
//        return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
//    }

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

//    @PatchMapping("/update-student-by-id/{id}")
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

    @PostMapping
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
//            dto.setFacultyName(lecture.getFacultyName());
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
//            dto.setFacultyName(lecture.getFacultyName());
            dto.setRoomNumber(lecture.getRoomNumber());
            dto.setFacultyId(lecture.getFaculty() != null ? lecture.getFaculty().getFacultyId() : null);
            dto.setStudentIds(lecture.getStudents().stream().map(StudentEntity::getStudentId).collect(Collectors.toList()));
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

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
//            dto.setFacultyName(lecture.getFacultyName());
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
//            dto.setFacultyName(lecture.getFacultyName());
            dto.setRoomNumber(lecture.getRoomNumber());
            dto.setFacultyId(lecture.getFaculty() != null ? lecture.getFaculty().getFacultyId() : null);
            dto.setStudentIds(lecture.getStudents().stream().map(StudentEntity::getStudentId).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }

}
