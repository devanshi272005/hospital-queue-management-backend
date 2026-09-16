package com.hospital.hospital_queue_management.repository;

import com.hospital.hospital_queue_management.entity.patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface patientrepository extends JpaRepository<patient, Integer> {
}