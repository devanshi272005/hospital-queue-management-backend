package com.hospital.hospital_queue_management.appointmentcontroller;

import com.hospital.hospital_queue_management.entity.appointment;
import com.hospital.hospital_queue_management.repository.appointmentrepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class appointmentcontroller {

    private final appointmentrepository appointmentRepository;

    public appointmentcontroller(appointmentrepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping
    public List<appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<appointment> getAppointmentById(
            @PathVariable Integer id) {

        Optional<appointment> appointment =
                appointmentRepository.findById(id);

        return appointment
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<appointment> createAppointment(
            @RequestBody appointment appointmentData) {

        appointment savedAppointment =
                appointmentRepository.save(appointmentData);

        return ResponseEntity.ok(savedAppointment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<appointment> updateAppointment(
            @PathVariable Integer id,
            @RequestBody appointment appointmentData) {

        Optional<appointment> existingOptional =
                appointmentRepository.findById(id);

        if (existingOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        appointment existingAppointment =
                existingOptional.get();

        if (appointmentData.getPatientId() != null) {
            existingAppointment.setPatientId(
                    appointmentData.getPatientId());
        }

        if (appointmentData.getDoctorId() != null) {
            existingAppointment.setDoctorId(
                    appointmentData.getDoctorId());
        }

        if (appointmentData.getAppointmentDate() != null) {
            existingAppointment.setAppointmentDate(
                    appointmentData.getAppointmentDate());
        }

        if (appointmentData.getAppointmentTime() != null) {
            existingAppointment.setAppointmentTime(
                    appointmentData.getAppointmentTime());
        }

        if (appointmentData.getAppointmentType() != null) {
            existingAppointment.setAppointmentType(
                    appointmentData.getAppointmentType());
        }

        if (appointmentData.getStatus() != null) {
            existingAppointment.setStatus(
                    appointmentData.getStatus());
        }

        if (appointmentData.getReason() != null) {
            existingAppointment.setReason(
                    appointmentData.getReason());
        }

        appointment updatedAppointment =
                appointmentRepository.save(existingAppointment);

        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(
            @PathVariable Integer id) {

        if (!appointmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        appointmentRepository.deleteById(id);

        return ResponseEntity.ok("Appointment deleted successfully");
    }
}