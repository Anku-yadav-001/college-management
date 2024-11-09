package com.yaduvanshi_brothers.api.repository;

import com.yaduvanshi_brothers.api.entity.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<LectureEntity, Integer> {
    List<LectureEntity> findBySubject(String subject);
    List<LectureEntity> findByStartFromBetween(Instant start, Instant end);
}
