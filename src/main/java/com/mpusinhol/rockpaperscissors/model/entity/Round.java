package com.mpusinhol.rockpaperscissors.model.entity;

import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "round")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    private Move computerMove;

    @Enumerated(EnumType.STRING)
    private Move playerMove;

    @Enumerated(EnumType.STRING)
    private Winner winner;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;
}
