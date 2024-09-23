package com.mpusinhol.rockpaperscissors.model.entity;

import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity(name = "game")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    private String id;
    private Instant startTime;
    @CreationTimestamp
    private Instant endTime;
    private Integer roundsWonByComputer;
    private Integer roundsWonByPlayer;
    private Integer roundsTied;
    private Move playerMostPlayedMove;

    @Enumerated(EnumType.STRING)
    private Winner winner;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Round> rounds;
}
