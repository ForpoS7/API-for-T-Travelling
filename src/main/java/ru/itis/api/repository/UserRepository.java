package ru.itis.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.api.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("UPDATE User u SET u.refreshToken = :refreshToken WHERE u.phoneNumber = :phoneNumber")
    @Modifying
    @Transactional
    void updateRefreshToken(@Param("refreshToken") String refreshToken, @Param("phoneNumber") String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.phoneNumber IN :phoneNumbers")
    List<User> findAllByPhoneNumbers(@Param("phoneNumbers") List<String> phoneNumbers);

    @Query("UPDATE User u SET u.deviceToken = :deviceToken WHERE u.id = :id")
    @Modifying
    @Transactional
    void updateDeviceTokenById(Long id, String deviceToken);
}
