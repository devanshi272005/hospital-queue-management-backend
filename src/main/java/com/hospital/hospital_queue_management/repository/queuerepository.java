package com.hospital.hospital_queue_management.repository;

import com.hospital.hospital_queue_management.entity.queue;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface queuerepository extends JpaRepository<queue, Integer> {

    @Query("""
        SELECT q FROM queue q
        WHERE q.doctorId = :doctorId
        AND q.queueDate = :queueDate
        ORDER BY q.tokenNumber
    """)
    List<queue> findDoctorQueue(
            @Param("doctorId") Integer doctorId,
            @Param("queueDate") LocalDate queueDate
    );

    @Query("""
        SELECT q FROM queue q
        WHERE q.doctorId = :doctorId
        AND q.queueDate = :queueDate
        AND q.status = com.hospital.hospital_queue_management.entity.queue.QueueStatus.WAITING
        ORDER BY q.tokenNumber
    """)
    List<queue> findWaitingQueue(
            @Param("doctorId") Integer doctorId,
            @Param("queueDate") LocalDate queueDate
    );

    @Query("""
        SELECT q FROM queue q
        WHERE q.doctorId = :doctorId
        AND q.queueDate = :queueDate
        AND q.status IN (
            com.hospital.hospital_queue_management.entity.queue.QueueStatus.CALLED,
            com.hospital.hospital_queue_management.entity.queue.QueueStatus.IN_CONSULTATION
        )
        ORDER BY q.tokenNumber
    """)
    List<queue> findCurrentConsultation(
            @Param("doctorId") Integer doctorId,
            @Param("queueDate") LocalDate queueDate
    );

    @Query("""
        SELECT q FROM queue q
        WHERE q.doctorId = :doctorId
        AND q.queueDate = :queueDate
        AND q.status = com.hospital.hospital_queue_management.entity.queue.QueueStatus.COMPLETED
        ORDER BY q.completedAt DESC
    """)
    List<queue> findCompletedQueue(
            @Param("doctorId") Integer doctorId,
            @Param("queueDate") LocalDate queueDate
    );

    @Query("""
        SELECT COALESCE(MAX(q.tokenNumber), 100)
        FROM queue q
        WHERE q.doctorId = :doctorId
        AND q.queueDate = :queueDate
    """)
    Integer findLastTokenNumber(
            @Param("doctorId") Integer doctorId,
            @Param("queueDate") LocalDate queueDate
    );
    boolean existsByAppointmentId(Integer appointmentId);
}