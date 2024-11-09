package com.yaduvanshi_brothers.api.controller;
import com.yaduvanshi_brothers.api.DTOs.AssignmentDTO;
import com.yaduvanshi_brothers.api.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    // Create Assignment with File Attachment
    @PostMapping(value = "/create-assignment", consumes = {"multipart/form-data"})
    public ResponseEntity<String> createAssignment(
            @ModelAttribute AssignmentDTO assignmentDTO,
            @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Received branchCode: " + assignmentDTO.getBranchCode());
            assignmentService.createAssignmentService(assignmentDTO, file);
            return ResponseEntity.ok("Assignment created successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while creating assignment: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating assignment.");
        }
    }

    // List All Assignments
    @GetMapping("/get-all-assignments")
    public ResponseEntity<List<AssignmentDTO>> listAssignments() {
        List<AssignmentDTO> assignments = assignmentService.listAllAssignments();
        return ResponseEntity.ok(assignments);
    }
    // Get Assignment by ID
    @GetMapping("/get-assignment/{id}")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable Integer id) {
        AssignmentDTO assignment = assignmentService.getAssignmentByIdService(id);
        return ResponseEntity.ok(assignment);
    }

    // Update Assignment with File Attachment
    @PostMapping("/update-assignment/{id}")
    public ResponseEntity<String> updateAssignment(
            @PathVariable Integer id,
            @ModelAttribute AssignmentDTO assignmentDTO,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        System.out.println("We are in assignment updation controller");

        // Call the service method to update the assignment
        assignmentService.updateAssignmentService(id, assignmentDTO, file);

        return ResponseEntity.ok("Assignment updated successfully with attachment");
    }

    // Delete Assignment
    @DeleteMapping("/delete-assignment/{id}")
    public ResponseEntity<String> deleteAssignment(@PathVariable Integer id) {
        assignmentService.deleteAssignmentService(id);
        return ResponseEntity.ok("Assignment deleted successfully");
    }

    @GetMapping("/get-today-assignments")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsForToday() {
        List<AssignmentDTO> assignments = assignmentService.getAssignmentsForToday();
        return ResponseEntity.ok(assignments);
    }
}

