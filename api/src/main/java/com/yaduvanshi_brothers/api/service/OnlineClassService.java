package com.yaduvanshi_brothers.api.service;

import com.yaduvanshi_brothers.api.DTOs.OnlineClassDTO;
import com.yaduvanshi_brothers.api.entity.OnlineClassEntity;
import com.yaduvanshi_brothers.api.entity.BranchesEntity;
import com.yaduvanshi_brothers.api.entity.FacultyEntity;
import com.yaduvanshi_brothers.api.entity.StudentEntity;
import com.yaduvanshi_brothers.api.repository.OnlineClassRepository;
import com.yaduvanshi_brothers.api.repository.BranchRepository;
import com.yaduvanshi_brothers.api.repository.FacultyRepository;
import com.yaduvanshi_brothers.api.repository.StudentRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OnlineClassService {

    @Autowired
    private OnlineClassRepository onlineClassRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRespository studentRespository;

    public OnlineClassEntity createOnlineClass(OnlineClassDTO onlineClassDTO) {
        OnlineClassEntity onlineClass = new OnlineClassEntity();

        onlineClass.setSubject(onlineClassDTO.getSubject());
        onlineClass.setSemester(onlineClassDTO.getSemester());
        onlineClass.setMeetingLink(onlineClassDTO.getMeetingLink());
        onlineClass.setPlatforms(onlineClassDTO.getPlatforms());
        onlineClass.setStartFrom(onlineClassDTO.getStartFrom());
        onlineClass.setEnd(onlineClassDTO.getEnd());

        // Set Faculty
        FacultyEntity faculty = facultyRepository.findById(onlineClassDTO.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        onlineClass.setFaculty(faculty);

        // Set Branch
        BranchesEntity branch = branchRepository.findById(onlineClassDTO.getBranchCode())
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        onlineClass.setBranch(branch);

        // Set Students
        List<StudentEntity> students = studentRespository.findAllById(onlineClassDTO.getStudentIds());
        onlineClass.setStudents(students);

        return onlineClassRepository.save(onlineClass);
    }

    public OnlineClassEntity updateOnlineClass(int id, OnlineClassDTO onlineClassDTO) {
        OnlineClassEntity existingClass = onlineClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Online class not found"));

        // Update class details
        existingClass.setSubject(onlineClassDTO.getSubject());
        existingClass.setSemester(onlineClassDTO.getSemester());
        existingClass.setMeetingLink(onlineClassDTO.getMeetingLink());
        existingClass.setPlatforms(onlineClassDTO.getPlatforms());
        existingClass.setStartFrom(onlineClassDTO.getStartFrom());
        existingClass.setEnd(onlineClassDTO.getEnd());

        // Update Faculty
        FacultyEntity faculty = facultyRepository.findById(onlineClassDTO.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        existingClass.setFaculty(faculty);

        // Update Branch
        BranchesEntity branch = branchRepository.findById(onlineClassDTO.getBranchCode())
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        existingClass.setBranch(branch);

        // Update Students
        List<StudentEntity> students = studentRespository.findAllById(onlineClassDTO.getStudentIds());
        existingClass.setStudents(students);

        return onlineClassRepository.save(existingClass);
    }

    public void deleteOnlineClassById(int id) {
        OnlineClassEntity onlineClass = onlineClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Online class not found"));

        // Remove the association with students without deleting them
        for (StudentEntity student : onlineClass.getStudents()) {
            student.getOnlineClasses().remove(onlineClass);
        }

        // Now delete the online class
        onlineClassRepository.delete(onlineClass);
    }



    public List<OnlineClassDTO> getAllOnlineClasses() {
        List<OnlineClassEntity> onlineClasses = onlineClassRepository.findAll();
        return onlineClasses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    public OnlineClassDTO getOnlineClassById(int id) {
        OnlineClassEntity onlineClass = onlineClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Online class not found"));
        return convertToDTO(onlineClass);
    }



    public List<OnlineClassDTO> getOnlineClassesForToday() {
        // Get the current date in IST (Indian Standard Time)
        ZonedDateTime currentDateInIST = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        // Define the formatter for the string date (assuming format is yyyy-MM-dd'T'HH:mm:ss)
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Fetch all online classes from the repository
        List<OnlineClassEntity> onlineClasses = onlineClassRepository.findAll();

        // Filter online classes based on startFrom date matching today's date in IST
        return onlineClasses.stream()
                .filter(onlineClass -> {
                    String startFromString = onlineClass.getStartFrom();

                    // Parse the string into LocalDateTime (no time zone)
                    LocalDateTime startFromLocalDateTime = LocalDateTime.parse(startFromString, formatter);

                    // Convert LocalDateTime to ZonedDateTime in IST
                    ZonedDateTime startFromInIST = startFromLocalDateTime.atZone(ZoneId.of("Asia/Kolkata"));

                    // Compare only the date part (ignoring time) with today's date in IST
                    return startFromInIST.toLocalDate().equals(currentDateInIST.toLocalDate());
                })
                .map(this::convertToDTO) // Convert to DTO
                .collect(Collectors.toList());
    }

    private OnlineClassDTO convertToDTO(OnlineClassEntity onlineClass) {
        OnlineClassDTO dto = new OnlineClassDTO();
        dto.setOnlineLectureId(onlineClass.getOnlineLectureId());
        dto.setSubject(onlineClass.getSubject());
        dto.setBranchCode(onlineClass.getBranch().getBranchCode()); // Assuming BranchesEntity has getBranchCode()
        dto.setSemester(onlineClass.getSemester());
        dto.setPlatforms(onlineClass.getPlatforms()); // Convert enum to string
        dto.setMeetingLink(onlineClass.getMeetingLink());
        dto.setStartFrom(onlineClass.getStartFrom());
        dto.setEnd(onlineClass.getEnd());
        dto.setFacultyId(onlineClass.getFaculty().getFacultyId()); // Assuming FacultyEntity has getFacultyId()
        dto.setStudentIds(onlineClass.getStudents().stream().map(StudentEntity::getStudentId).collect(Collectors.toList())); // Assuming StudentEntity has getStudentId()
        return dto;
    }
}
