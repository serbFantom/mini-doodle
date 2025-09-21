package com.serb.miniDoodle.repository;

import com.serb.miniDoodle.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, UUID> {

    List<Meeting> findByOrganizerIdOrParticipants(
            UUID organizerId, UUID participantId, Instant startTime, Instant endTime
    );
}
