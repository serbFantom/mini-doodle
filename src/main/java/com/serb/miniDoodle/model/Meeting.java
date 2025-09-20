package com.serb.miniDoodle.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Meeting {
    @Id
    private UUID id;
    private String title;
    private String description;
    private UUID organizerId;
    @ElementCollection
    private List<UUID> participants;
    private Instant start;
    private Instant end;
}
