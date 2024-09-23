package com.mpusinhol.rockpaperscissors.repository;

import com.mpusinhol.rockpaperscissors.model.entity.Game;
import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, String> {

    @Query("SELECT r.playerMove FROM round r " +
    "JOIN r.game g " +
    "JOIN g.user u " +
    "WHERE u.username = :username " +
    "ORDER BY r.id DESC ")
    List<Move> findLastPlayerMovesByUsername(@Param("username") String username, Pageable pageable);
}
