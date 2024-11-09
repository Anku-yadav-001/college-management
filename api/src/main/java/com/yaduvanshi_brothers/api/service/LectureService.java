package com.yaduvanshi_brothers.api.service;

import com.yaduvanshi_brothers.api.entity.LectureEntity;
import com.yaduvanshi_brothers.api.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureService {

    @Autowired
    private LectureRepository lectureRepository;

    public Page<LectureEntity> getAllLectures(PageRequest pageRequest) {
        return lectureRepository.findAll(pageRequest);
    }

    public LectureEntity getLectureById(int id) {
        return lectureRepository.findById(id).orElse(null);
    }

    public List<LectureEntity> findLecturesBySubject(String subject) {
        return lectureRepository.findBySubject(subject);
    }

    public List<LectureEntity> sortLectures(Sort sort) {
        return lectureRepository.findAll(sort);
    }
    public LectureEntity createLecture(LectureEntity lecture) {
        return lectureRepository.save(lecture);
    }

    public void deleteLecture(int id) {
        lectureRepository.deleteById(id);
    }
    public List<LectureEntity> getLecturesForToday() {
        // Get the current date in IST (Indian Standard Time)
        ZonedDateTime currentDateInIST = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        // Fetch all lectures from the repository
        List<LectureEntity> allLectures = lectureRepository.findAll();

        // Filter lectures based on startFrom date matching today's date in IST
        return allLectures.stream()
                .filter(lecture -> {
                    // Handle Date type or Timestamp type for startFrom
                    Date startFromDate = lecture.getStartFrom();
                    ZonedDateTime startFromInIST;

                    if (startFromDate instanceof Timestamp) {
                        // If startFrom is a Timestamp, convert to ZonedDateTime
                        startFromInIST = ((Timestamp) startFromDate).toInstant()
                                .atZone(ZoneId.of("UTC"))
                                .withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
                    } else if (startFromDate instanceof java.util.Date) {
                        // If startFrom is a general Date, convert it to ZonedDateTime
                        startFromInIST = startFromDate.toInstant()
                                .atZone(ZoneId.of("UTC"))
                                .withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
                    } else {
                        // If the type is unexpected, return false
                        return false;
                    }

                    // Compare only the date part (ignoring time) with today's date in IST
                    return startFromInIST.toLocalDate().equals(currentDateInIST.toLocalDate());
                })
                .collect(Collectors.toList());
    }
}
