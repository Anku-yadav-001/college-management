package com.yaduvanshi_brothers.api.controller;

import com.yaduvanshi_brothers.api.DTOs.LectureDTO;
import com.yaduvanshi_brothers.api.entity.FacultyEntity;
import com.yaduvanshi_brothers.api.entity.LectureEntity;
import com.yaduvanshi_brothers.api.entity.StudentEntity;
import com.yaduvanshi_brothers.api.repository.FacultyRepository;
import com.yaduvanshi_brothers.api.repository.StudentRespository;
import com.yaduvanshi_brothers.api.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lectures")
public class LectureController {

    @Autowired
    private LectureService lectureService;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRespository studentRepository;

    @PostMapping("/create-lecture")
    public ResponseEntity<LectureDTO> createLecture(@RequestBody LectureDTO lectureDto) {
        LectureEntity lecture = new LectureEntity();
        lecture.setYear(lectureDto.getYear());
        lecture.setSemester(lectureDto.getSemester());
        lecture.setDepartment(lectureDto.getDepartment());
        lecture.setSubject(lectureDto.getSubject());
        lecture.setStartFrom(lectureDto.getStartFrom());
        lecture.setTill(lectureDto.getTill());
        lecture.setRoomNumber(lectureDto.getRoomNumber());

        if (lectureDto.getFacultyId() != null) {
            FacultyEntity faculty = facultyRepository.findById(lectureDto.getFacultyId())
                    .orElseThrow(() -> new Error("Faculty not found with ID " + lectureDto.getFacultyId()));
            lecture.setFaculty(faculty);
        }

        if (lectureDto.getStudentIds() != null && !lectureDto.getStudentIds().isEmpty()) {
            List<StudentEntity> students = studentRepository.findAllById(lectureDto.getStudentIds());
            lecture.setStudents(students);
        }

        LectureEntity createdLecture = lectureService.createLecture(lecture);

        if (lecture.getFaculty() != null) {
            lecture.getFaculty().getLectures().add(createdLecture);
            facultyRepository.save(lecture.getFaculty());
        }
     if (!lecture.getStudents().isEmpty()) {
            lecture.getStudents().forEach(student -> {
                student.getLectures().add(createdLecture);
                studentRepository.save(student);
            });
        }

        LectureDTO dto = new LectureDTO();
        dto.setLectureId(createdLecture.getLectureId());
        dto.setYear(createdLecture.getYear());
        dto.setSemester(createdLecture.getSemester());
        dto.setDepartment(createdLecture.getDepartment());
        dto.setSubject(createdLecture.getSubject());
        dto.setStartFrom(createdLecture.getStartFrom());
        dto.setTill(createdLecture.getTill());
        dto.setRoomNumber(createdLecture.getRoomNumber());
        dto.setFacultyId(createdLecture.getFaculty() != null ? createdLecture.getFaculty().getFacultyId() : null);
        dto.setStudentIds(createdLecture.getStudents().stream().map(StudentEntity::getStudentId).collect(Collectors.toList()));

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

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

    // Get lecture by ID
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

    // Sort lectures by any field in ascending/descending order
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

//GET /lectures/get-all-lectures
//GET /lectures/sort?sortBy=facultyName&sortDirection=desc
//GET /lectures/search-by-subject?subject=Mathematics
//GET /lectures/1
//GET /lectures/get-all-lectures?page=0&size=5&sortDirection=asc&sortBy=subject

