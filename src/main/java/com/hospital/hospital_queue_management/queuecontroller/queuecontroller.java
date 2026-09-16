package com.hospital.hospital_queue_management.queuecontroller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.hospital_queue_management.dto.QueueResponse;
import com.hospital.hospital_queue_management.entity.appointment;
import com.hospital.hospital_queue_management.entity.queue;
import com.hospital.hospital_queue_management.repository.appointmentrepository;
import com.hospital.hospital_queue_management.repository.patientrepository;
import com.hospital.hospital_queue_management.repository.queuerepository;

@RestController
@RequestMapping("/api/queues")
@CrossOrigin(origins = "http://localhost:5173")
public class queuecontroller {

    private final queuerepository queueRepository;
    private final appointmentrepository appointmentRepository;
    private final patientrepository patientRepository;
    public queuecontroller(
            queuerepository queueRepository,
            appointmentrepository appointmentRepository,
            patientrepository patientRepository) {
       
    this.queueRepository = queueRepository;
    this.appointmentRepository = appointmentRepository;
    this.patientRepository = patientRepository;
    }
    // Get all queues
    @GetMapping
    public List<queue> getAllQueues() {
        return queueRepository.findAll();
    }

    // Get queue by ID
    @GetMapping("/{id}")
    public ResponseEntity<queue> getQueueById(@PathVariable Integer id) {
        Optional<queue> queue = queueRepository.findById(id);

        return queue.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get doctor's queue for a particular date
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public List<queue> getDoctorQueue(
            @PathVariable Integer doctorId,
            @PathVariable LocalDate date) {

        return queueRepository.findDoctorQueue(doctorId, date);
    }

    // Get today's complete queue
    @GetMapping("/doctor/{doctorId}/today")
    public List<queue> getTodayQueue(@PathVariable Integer doctorId) {

        return queueRepository.findDoctorQueue(
                doctorId,
                LocalDate.now()
        );
    }

    // Get waiting patients
    @GetMapping("/doctor/{doctorId}/waiting")
    public List<QueueResponse> getWaitingPatients(
            @PathVariable Integer doctorId) {

        List<queue> waitingQueues = queueRepository.findWaitingQueue(
                doctorId,
                LocalDate.now()
        );

        return waitingQueues.stream()
                .map(this::convertToResponse)
                .toList();
    }
    // Get current patient
    @GetMapping("/doctor/{doctorId}/current")
    public ResponseEntity<QueueResponse> getCurrentPatient(
            @PathVariable Integer doctorId) {

        List<queue> current = queueRepository.findCurrentConsultation(
                doctorId,
                LocalDate.now()
        );

        if (current.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        QueueResponse response = convertToResponse(current.get(0));

        return ResponseEntity.ok(response);
    }

    // Get next patient
    @GetMapping("/doctor/{doctorId}/next")
    public ResponseEntity<queue> getNextPatient(
            @PathVariable Integer doctorId) {

        List<queue> waiting = queueRepository.findWaitingQueue(
                doctorId,
                LocalDate.now()
        );

        if (waiting.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(waiting.get(0));
    }

    // Get completed patients
    @GetMapping("/doctor/{doctorId}/completed")
    public List<QueueResponse> getCompletedPatients(
            @PathVariable Integer doctorId) {

        List<queue> completedQueues = queueRepository.findCompletedQueue(
                doctorId,
                LocalDate.now()
        );

        return completedQueues.stream()
                .map(this::convertToResponse)
                .toList();
    }
    // Create queue
    @PostMapping
    public ResponseEntity<?> createQueue(@RequestBody queue queueData) {

        if (queueData.getAppointmentId() == null) {
            return ResponseEntity.badRequest()
                    .body("Appointment ID is required");
        }
        if (queueRepository.existsByAppointmentId(queueData.getAppointmentId())) {
            return ResponseEntity.badRequest()
                    .body("This appointment is already added to the queue");
        }
        Optional<appointment> appointmentOptional =
                appointmentRepository.findById(queueData.getAppointmentId());

        if (appointmentOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Appointment not found");
        }

        appointment appointmentData = appointmentOptional.get();

        LocalDate queueDate = queueData.getQueueDate();

        if (queueDate == null) {
            queueDate = LocalDate.now();
            queueData.setQueueDate(queueDate);
        }

        if (queueData.getDoctorId() == null) {
            queueData.setDoctorId(appointmentData.getDoctorId());
        }

        Integer lastToken = queueRepository.findLastTokenNumber(
                queueData.getDoctorId(),
                queueDate
        );

        queueData.setTokenNumber(lastToken + 1);

        if (queueData.getStatus() == null) {
            queueData.setStatus(queue.QueueStatus.WAITING);
        }

        if (queueData.getJoinedAt() == null) {
            queueData.setJoinedAt(LocalDateTime.now());
        }

        updateAppointmentStatus(
                appointmentData,
                appointment.AppointmentStatus.WAITING
        );

        queue savedQueue = queueRepository.save(queueData);

        return ResponseEntity.ok(savedQueue);
    }

    // Call next patient
    @PostMapping("/doctor/{doctorId}/call-next")
    public ResponseEntity<?> callNextPatient(
            @PathVariable Integer doctorId) {

        List<queue> waiting = queueRepository.findWaitingQueue(
                doctorId,
                LocalDate.now()
        );

        if (waiting.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        queue nextPatient = waiting.get(0);

        nextPatient.setStatus(queue.QueueStatus.CALLED);
        nextPatient.setCalledAt(LocalDateTime.now());

        queue savedQueue = queueRepository.save(nextPatient);

        appointmentRepository.findById(savedQueue.getAppointmentId())
                .ifPresent(appointmentData ->
                        updateAppointmentStatus(
                                appointmentData,
                                appointment.AppointmentStatus.CALLED
                        )
                );
        return ResponseEntity.ok(convertToResponse(savedQueue));
    }

    // Update queue
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQueue(
            @PathVariable Integer id,
            @RequestBody queue queueData) {

        Optional<queue> existingOptional = queueRepository.findById(id);

        if (existingOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        queue existingQueue = existingOptional.get();

        if (queueData.getStatus() != null) {
            existingQueue.setStatus(queueData.getStatus());

            if (queueData.getStatus() == queue.QueueStatus.COMPLETED) {
                existingQueue.setCompletedAt(LocalDateTime.now());

                appointmentRepository.findById(
                        existingQueue.getAppointmentId()
                ).ifPresent(appointmentData ->
                        updateAppointmentStatus(
                                appointmentData,
                                appointment.AppointmentStatus.COMPLETED
                        )
                );
            }

            if (queueData.getStatus() == queue.QueueStatus.IN_CONSULTATION) {
                appointmentRepository.findById(
                        existingQueue.getAppointmentId()
                ).ifPresent(appointmentData ->
                        updateAppointmentStatus(
                                appointmentData,
                                appointment.AppointmentStatus.IN_CONSULTATION
                        )
                );
            }

            if (queueData.getStatus() == queue.QueueStatus.CANCELLED) {
                appointmentRepository.findById(
                        existingQueue.getAppointmentId()
                ).ifPresent(appointmentData ->
                        updateAppointmentStatus(
                                appointmentData,
                                appointment.AppointmentStatus.CANCELLED
                        )
                );
            }
        }

        queue savedQueue = queueRepository.save(existingQueue);

        return ResponseEntity.ok(savedQueue);
    }

    // Delete queue
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQueue(@PathVariable Integer id) {

        if (!queueRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        queueRepository.deleteById(id);

        return ResponseEntity.ok("Queue deleted successfully");
    }

    // Update appointment status
    private void updateAppointmentStatus(
            appointment appointmentData,
            appointment.AppointmentStatus status) {

        appointmentData.setStatus(status);
        appointmentRepository.save(appointmentData);
    }
    private QueueResponse convertToResponse(queue queueData) {

        QueueResponse response = new QueueResponse();

        response.setQueueId(queueData.getQueueId());
        response.setAppointmentId(queueData.getAppointmentId());
        response.setDoctorId(queueData.getDoctorId());
        response.setTokenNumber(queueData.getTokenNumber());
        response.setStatus(queueData.getStatus().name());
        response.setCalledAt(queueData.getCalledAt());
        response.setCompletedAt(queueData.getCompletedAt());
        response.setJoinedAt(queueData.getJoinedAt());
        appointmentRepository.findById(queueData.getAppointmentId())
                .ifPresent(appointmentData -> {

                    patientRepository.findById(appointmentData.getPatientId())
                            .ifPresent(patientData ->
                                    response.setPatientName(patientData.getName())
                            );
                });

        return response;
    }
    
    
    
    
}