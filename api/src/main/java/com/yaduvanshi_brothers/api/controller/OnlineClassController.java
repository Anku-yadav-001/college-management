package com.yaduvanshi_brothers.api.controller;

import com.yaduvanshi_brothers.api.DTOs.OnlineClassDTO;
import com.yaduvanshi_brothers.api.entity.OnlineClassEntity;
import com.yaduvanshi_brothers.api.service.OnlineClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/online-classes")
public class OnlineClassController {

    @Autowired
    private OnlineClassService onlineClassService;

    @PostMapping("/create-online-class")
    public String createOnlineClass(@RequestBody OnlineClassDTO onlineClassDTO) {
        OnlineClassEntity createdClass = onlineClassService.createOnlineClass(onlineClassDTO);
        return "online lecture scheduled";
    }

    @PostMapping("/update-online-class-by-id/{id}")
    public String updateOnlineClass(@PathVariable int id, @RequestBody OnlineClassDTO onlineClassDTO) {
        OnlineClassEntity updatedClass = onlineClassService.updateOnlineClass(id, onlineClassDTO);
        return "online class updated";
    }

    @GetMapping("/get-all-online-classes")
    public ResponseEntity<List<OnlineClassDTO>> getAllOnlineClasses() {
        List<OnlineClassDTO> classes = onlineClassService.getAllOnlineClasses();
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/get-online-class-by-id/{id}")
    public ResponseEntity<OnlineClassDTO> getOnlineClassById(@PathVariable int id) {
        OnlineClassDTO onlineClassDTO = onlineClassService.getOnlineClassById(id);
        return ResponseEntity.ok(onlineClassDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOnlineClass(@PathVariable int id) {
        onlineClassService.deleteOnlineClassById(id);
        return ResponseEntity.ok("Online class deleted successfully");
    }

}
