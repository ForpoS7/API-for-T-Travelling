package ru.itis.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.api.dto.TravelDto;
import ru.itis.api.dto.TravelParticipantsDto;
import ru.itis.api.entity.Travel;

import java.util.List;
import java.util.Optional;

public interface TravelRepository extends JpaRepository<Travel, Long> {
    @Query("SELECT t " +
            "FROM Travel t " +
            "JOIN UserTravel ut ON t.id = ut.travel.id " +
            "WHERE ut.user.id = :userId " +
            "AND ut.isConfirmed = :isConfirmed " +
            "AND t.isActive = :isActive")
    List<Travel> findTravelsByUserIdAndStatus(@Param("userId") Long userId,
                                              @Param("isConfirmed") Boolean isConfirmed,
                                              @Param("isActive") Boolean isActive);

    @Query("""
    SELECT new ru.itis.api.dto.TravelParticipantsDto(
        t.id,
        t.name,
        t.totalBudget,
        t.dateOfBegin,
        t.dateOfEnd,
        NEW ru.itis.api.dto.UserDto(
            t.creator.firstName,
            t.creator.lastName,
            t.creator.phoneNumber
        )
    )
    FROM Travel t
    WHERE t.id = :travelId
    """)
    Optional<TravelParticipantsDto> findTravelDetailsById(@Param("travelId") Long travelId);

    boolean existsTravelByIdAndCreatorId(Long travelId,
                                         Long creatorId);
}
