package ru.itis.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.api.dto.UserDto;
import ru.itis.api.entity.User;
import ru.itis.api.entity.UserTravel;

import java.util.List;

public interface UserTravelRepository extends JpaRepository<UserTravel, Long> {
    @Query("""
    SELECT NEW ru.itis.api.dto.UserDto(
        u.user.firstName,
        u.user.lastName,
        u.user.phoneNumber
    )
    FROM UserTravel u
    WHERE u.travel.id = :travelId AND u.user.phoneNumber != :creatorPhoneNumber
    """)
    List<UserDto> findUsersDtoByTravelIdWithoutCreator(@Param("travelId") Long travelId, String creatorPhoneNumber);

    @Query("FROM UserTravel ut" +
            "    WHERE ut.travel.id = :travelId AND ut.user.phoneNumber != :creatorPhoneNumber")
    List<UserTravel> findUserTravelsByTravelIdWithoutCreator(@Param("travelId") Long travelId, String creatorPhoneNumber);

    @Modifying
    @Query("UPDATE UserTravel ut " +
            "SET ut.isConfirmed = :isConfirm " +
            "WHERE ut.user.id = :userId AND ut.travel.id = :travelId")
    void updateConfirmStatus(@Param("userId") Long userId,
                            @Param("travelId") Long travelId,
                            Boolean isConfirm);

    @Modifying
    @Query("DELETE FROM UserTravel ut " +
            "WHERE ut.user.id = :participantId AND ut.travel.id = :travelId")
    void deleteByUserIdAndTravelId(@Param("travelId") Long travelId,
                                   @Param("participantId") Long participantId);

    @Modifying
    @Query("DELETE FROM UserTravel ut WHERE ut.travel.id = :travelId AND ut.user.phoneNumber != :creatorPhoneNumber")
    void deleteAllByTravelIdExceptCreator(@Param("travelId") Long travelId, @Param("creatorPhoneNumber") String creatorPhoneNumber);

    boolean existsByUserIdAndTravelId(Long userId, Long travelId);
}
