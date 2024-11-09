package com.yaduvanshi_brothers.api.service;

import com.yaduvanshi_brothers.api.DTOs.AssignmentDTO;
import com.yaduvanshi_brothers.api.DTOs.FacultyDTO;
import com.yaduvanshi_brothers.api.entity.AssignmentEntity;
import com.yaduvanshi_brothers.api.entity.BranchesEntity;
import com.yaduvanshi_brothers.api.entity.StudentEntity;
import com.yaduvanshi_brothers.api.repository.AssignmentRepository;
import com.yaduvanshi_brothers.api.repository.BranchRepository;
import com.yaduvanshi_brothers.api.repository.FacultyRepository;
import com.yaduvanshi_brothers.api.repository.StudentRespository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AssignmentService {

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRespository studentRespository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    private static final Logger logger = LoggerFactory.getLogger(AssignmentService.class);

    public void createAssignmentService(AssignmentDTO assignmentDTO, MultipartFile file) throws IOException {
        LocalDateTime startDate = assignmentDTO.getStartDate() != null ? assignmentDTO.getStartDate() : LocalDateTime.now();
        LocalDateTime endDate = assignmentDTO.getEndDate();
        logger.debug("Received assignmentDTO: {}", assignmentDTO);

        if (endDate == null || endDate.isBefore(startDate)) {
            endDate = startDate.plusDays(7);
        }

        BranchesEntity branch = branchRepository.findByBranchCode(assignmentDTO.getBranchCode());
        if (branch == null) {
            logger.error("Branch not found for code: {}", assignmentDTO.getBranchCode());
            throw new IllegalArgumentException("Branch not found for code: " + assignmentDTO.getBranchCode());
        }

        AssignmentEntity assignment = mapDtoToEntity(assignmentDTO);
        assignment.setBranchCode(branch);


        // Additional logging to check values after mapping
        logger.debug("Mapped assignmentEntity: {}", assignment);

        if (file != null && !file.isEmpty()) {
            assignment.setAttachedFiles(file.getBytes());
            assignment.setFileName(file.getOriginalFilename());
            logger.info("Attached file: {}", file.getOriginalFilename());
        } else {
            logger.warn("No file uploaded for the assignment.");
        }

        assignment.setStartDate(startDate);
        assignment.setEndDate(endDate);
        assignmentRepository.save(assignment);
        logger.info("Assignment created successfully with ID: {}", assignment.getId());
    }

    public List<AssignmentDTO> listAllAssignments() {

        return assignmentRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    public AssignmentDTO getAssignmentByIdService(Integer id) {
        AssignmentEntity assignmentEntity = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found with ID: " + id));
        return mapEntityToDto(assignmentEntity);
    }

    public void updateAssignmentService(Integer id, AssignmentDTO assignmentDTO, MultipartFile file) throws IOException {
        AssignmentEntity existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found with ID: " + id));
        mapDtoToEntityForUpdate(existingAssignment, assignmentDTO);
        System.out.println("we are in assignment updation service");
        if (file != null && !file.isEmpty()) {
            existingAssignment.setAttachedFiles(file.getBytes());
            existingAssignment.setFileName(file.getOriginalFilename());
        }
        System.out.println("we are in assignment updation service after file checking");
        assignmentRepository.save(existingAssignment);
    }

    public void deleteAssignmentService(Integer id) {
        assignmentRepository.deleteById(id);
    }

    private AssignmentEntity mapDtoToEntity(AssignmentDTO assignmentDTO) {
        AssignmentEntity assignmentEntity = new AssignmentEntity();
        assignmentEntity.setId(assignmentDTO.getId());
        assignmentEntity.setYear(assignmentDTO.getYear());
        assignmentEntity.setSemester(assignmentDTO.getSemester());
        assignmentEntity.setTopic(assignmentDTO.getTopic());
        assignmentEntity.setBranchCode(branchRepository.findByBranchCode(assignmentDTO.getBranchCode()));
        assignmentEntity.setSubject(assignmentDTO.getSubject());

        if (assignmentDTO.getAssignedBy() != null) {
            assignmentEntity.setAssignedBy(facultyRepository.findById(assignmentDTO.getAssignedBy().getFacultyId()).orElse(null));
        }
        assignmentEntity.setStartDate(assignmentDTO.getStartDate());
        assignmentEntity.setEndDate(assignmentDTO.getEndDate());
        assignmentEntity.setQuestions(assignmentDTO.getQuestions());

        if (assignmentDTO.getStudentIds() != null) {
            List<StudentEntity> students = assignmentDTO.getStudentIds().stream()
                    .map(studentRespository::findById)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
            assignmentEntity.setStudents(students);
        }

        assignmentEntity.setAssignmentType(assignmentDTO.getAssignmentType());
        assignmentEntity.setCategory(assignmentDTO.getCategory());
        assignmentEntity.setStatus(assignmentDTO.getStatus());
        assignmentEntity.setTotalMarks(assignmentDTO.getTotalMarks());
        assignmentEntity.setDifficultyLevel(assignmentDTO.getDifficultyLevel());
        assignmentEntity.setNotes(assignmentDTO.getNotes());

        return assignmentEntity;
    }

    public List<AssignmentDTO> getAssignmentsForToday() {
        LocalDate today = LocalDate.now();
        List<AssignmentEntity> assignmentsForToday = assignmentRepository.findAll().stream()
                .filter(assignment -> !assignment.getStartDate().toLocalDate().isBefore(today) && // Ensure startDate is not before today
                        assignment.getStartDate().toLocalDate().equals(today) &&  // Ensure the startDate is exactly today
                        !assignment.getEndDate().toLocalDate().isBefore(today)) // Ensure endDate is not before today
                .collect(Collectors.toList());

        // Map the assignments to DTOs and return
        return assignmentsForToday.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }


    private void mapDtoToEntityForUpdate(AssignmentEntity entity, AssignmentDTO dto) {
        entity.setYear(dto.getYear());
        entity.setSemester(dto.getSemester());
        entity.setTopic(dto.getTopic());
        entity.setBranchCode(branchRepository.findByBranchCode(dto.getBranchCode()));
        entity.setSubject(dto.getSubject());
        if (dto.getAssignedBy() != null) {
            entity.setAssignedBy(facultyRepository.findById(dto.getAssignedBy().getFacultyId()).orElse(null));
        }
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setQuestions(dto.getQuestions());
        entity.setAssignmentType(dto.getAssignmentType());
        entity.setCategory(dto.getCategory());
        entity.setStatus(dto.getStatus());
        entity.setTotalMarks(dto.getTotalMarks());
        entity.setDifficultyLevel(dto.getDifficultyLevel());
        entity.setNotes(dto.getNotes());
    }

    private AssignmentDTO mapEntityToDto(AssignmentEntity entity) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(entity.getId());
        dto.setYear(entity.getYear());
        dto.setTopic(entity.getTopic());
        dto.setSemester(entity.getSemester());
        dto.setSubject(entity.getSubject());
        if (entity.getStudents() != null) {
            dto.setStudentIds(entity.getStudents().stream()
                    .map(StudentEntity::getStudentId)
                    .collect(Collectors.toList()));
        }

        // Set only the branch code
        dto.setBranchCode(entity.getBranchCode().getBranchCode());

        // Map FacultyEntity to FacultyDTO
        if (entity.getAssignedBy() != null) {
            FacultyDTO facultyDTO = new FacultyDTO();
            facultyDTO.setFacultyId(entity.getAssignedBy().getFacultyId());
            facultyDTO.setName(entity.getAssignedBy().getName());
            facultyDTO.setMobile(entity.getAssignedBy().getMobile());
            facultyDTO.setEmail(entity.getAssignedBy().getEmail());
            facultyDTO.setDesignation(entity.getAssignedBy().getDesignation());
            facultyDTO.setDepartmentType(entity.getAssignedBy().getDepartmentType());
            facultyDTO.setSubDepartment(entity.getAssignedBy().getSubDepartment());
            facultyDTO.setSalary(entity.getAssignedBy().getSalary());
            facultyDTO.setGender(entity.getAssignedBy().getGender());
            facultyDTO.setBranchCode(entity.getAssignedBy().getBranch().getBranchCode());
            facultyDTO.setImageUrl(entity.getAssignedBy().getImageEntity().getName());
            if (entity.getAssignedBy().getImageEntity() != null) {
                facultyDTO.setImageUrl("http://localhost:8080/image/" + entity.getAssignedBy().getImageEntity().getId());
            } else {
                 return null;
            }

            dto.setAssignedBy(facultyDTO);
        }

        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setQuestions(entity.getQuestions());
        dto.setAssignmentType(entity.getAssignmentType());
        dto.setCategory(entity.getCategory());
        dto.setStatus(entity.getStatus());
        dto.setTotalMarks(entity.getTotalMarks());
        dto.setDifficultyLevel(entity.getDifficultyLevel());
        dto.setNotes(entity.getNotes());

        if (entity.getAttachedFiles() != null) {
            dto.setDownloadUrl("/download-file/"+entity.getId());
        }
        return dto;
    }

}
