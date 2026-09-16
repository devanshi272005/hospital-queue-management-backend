package com.hospital.hospital_queue_management.repository;

import com.hospital.hospital_queue_management.entity.appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface appointmentrepository extends JpaRepository<appointment, Integer> {
}