package com.epam.gym.workload.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "training")
public class TrainingEntity {

    @Id
    @Column(name = "uid", nullable = false)
    private UUID uid;

    @Column(name = "username", nullable = false, updatable = false)
    private String username;

    @Column(name = "duration", nullable = false, updatable = false)
    private Integer duration;

    @Column(name = "date", nullable = false, updatable = false)
    private LocalDate date;

}
