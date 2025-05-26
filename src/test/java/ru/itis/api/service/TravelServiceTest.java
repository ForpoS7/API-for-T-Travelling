package ru.itis.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.api.AbstractConfigurationTest;
import ru.itis.api.dto.RequestTravelDto;
import ru.itis.api.dto.TravelParticipantsDto;
import ru.itis.api.entity.Travel;
import ru.itis.api.entity.User;
import ru.itis.api.entity.UserTravel;
import ru.itis.api.repository.TravelRepository;
import ru.itis.api.repository.UserRepository;
import ru.itis.api.repository.UserTravelRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TravelServiceTest extends AbstractConfigurationTest {

    @Autowired
    private TravelService travelService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TravelRepository travelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserTravelRepository userTravelRepository;

    @BeforeEach
    void setUp() {
        travelRepository.deleteAll();
        userTravelRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void saveTravelPositiveTest() {
        User creator = new User();
        creator.setPhoneNumber("89876543210");
        creator.setPassword(passwordEncoder.encode("123"));
        creator.setFirstName("creator");
        creator.setLastName("creatov");
        creator = userRepository.save(creator);

        User participant = new User();
        participant.setPhoneNumber("81234567890");
        participant.setPassword(passwordEncoder.encode("123"));
        participant.setFirstName("participant");
        participant.setLastName("participantov");
        userRepository.save(participant);

        RequestTravelDto requestTravelDto = new RequestTravelDto();
        requestTravelDto.setName("Test Travel");
        requestTravelDto.setTotalBudget(BigDecimal.valueOf(1000.0));
        requestTravelDto.setDateOfBegin(LocalDate.now());
        requestTravelDto.setDateOfEnd(LocalDate.now().plusDays(5));
        requestTravelDto.setParticipantPhones(List.of("81234567890"));

        TravelParticipantsDto travelParticipantsDto = travelService.saveTravel(requestTravelDto, creator);

        Optional<Travel> optionalTravel = travelRepository.findById(travelParticipantsDto.getId());
        assertTrue(optionalTravel.isPresent());

        Travel travel = optionalTravel.get();
        assertEquals(travel.getName(), travelParticipantsDto.getName());
        assertEquals(travel.getTotalBudget(), travelParticipantsDto.getTotalBudget());
        assertEquals(travel.getDateOfBegin(), travelParticipantsDto.getDateOfBegin());
        assertEquals(travel.getDateOfEnd(), travelParticipantsDto.getDateOfEnd());

        List<UserTravel> travelParticipants = travel.getUsers();
        assertNotNull(travelParticipants.get(1));
        assertTrue(travelParticipants.get(1).getIsConfirmed());
        assertNotNull(travelParticipants.get(0));
        assertFalse(travelParticipants.get(0).getIsConfirmed());

        User creatorUser = travelParticipants.get(1).getUser();
        User participantUser = travelParticipants.get(0).getUser();
        assertEquals(creatorUser.getPhoneNumber(), creator.getPhoneNumber());
        assertEquals(creatorUser.getPassword(), creator.getPassword());
        assertEquals(creatorUser.getFirstName(), creator.getFirstName());
        assertEquals(creatorUser.getLastName(), creator.getLastName());
        assertEquals(participantUser.getPhoneNumber(), participant.getPhoneNumber());
        assertEquals(participantUser.getPassword(), participant.getPassword());
        assertEquals(participantUser.getFirstName(), participant.getFirstName());
        assertEquals(participantUser.getLastName(), participant.getLastName());
    }
}