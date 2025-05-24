package ru.itis.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.api.dto.RequestTravelDto;
import ru.itis.api.dto.TravelDto;
import ru.itis.api.dto.TravelParticipantsDto;
import ru.itis.api.dto.UserDto;
import ru.itis.api.entity.Travel;
import ru.itis.api.entity.User;
import ru.itis.api.entity.UserTravel;
import ru.itis.api.exception.NotFoundException;
import ru.itis.api.exception.OperationNotAllowedForOwnerException;
import ru.itis.api.exception.UserAlreadyExistException;
import ru.itis.api.mapper.TravelMapper;
import ru.itis.api.mapper.UserMapper;
import ru.itis.api.repository.TravelRepository;
import ru.itis.api.repository.UserRepository;
import ru.itis.api.repository.UserTravelRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final UserRepository userRepository;
    private final TravelRepository travelRepository;
    private final UserTravelRepository userTravelRepository;
    private final TravelMapper travelMapper;
    private final UserMapper userMapper;

    public List<TravelDto> getActiveTravels(Long userId) {
        List<Travel> travels = travelRepository.findTravelsByUserIdAndStatus(userId, true, true);
        return travels.stream()
                .map(travelMapper::mapToTravelDto)
                .toList();
    }

    public List<TravelDto> getCompletedTravels(Long userId) {
        List<Travel> travels = travelRepository.findTravelsByUserIdAndStatus(userId, true, false);
        return travels.stream()
                .map(travelMapper::mapToTravelDto)
                .toList();
    }

    public TravelParticipantsDto getTravel(Long travelId) {
        Optional<TravelParticipantsDto> optionalTravelDetails = travelRepository.findTravelDetailsById(travelId);
        if (optionalTravelDetails.isEmpty()) {
            throw new NotFoundException("Travel not found");
        }
        TravelParticipantsDto travelParticipantsDto = optionalTravelDetails.get();
        travelParticipantsDto.setParticipants(
                userTravelRepository.findUsersByTravelId(travelId));
        return travelParticipantsDto;
    }

    @Transactional
    public TravelParticipantsDto saveTravel(RequestTravelDto requestTravel, User creator) {
        Travel travel = travelMapper.mapToTravel(requestTravel);
        travel.setCreator(creator);
        Travel savedTravel = travelRepository.save(travel);

        List<User> participants = userRepository.findAllByPhoneNumbers(
                requestTravel.getParticipantPhones());
        participants.add(creator);
        participants.forEach(participant -> {
            UserTravel userTravel = new UserTravel()
                    .setTravel(savedTravel)
                    .setUser(participant)
                    .setIsConfirmed(participant.getId().equals(creator.getId()));
            savedTravel.getUsers().add(userTravel);
        });

        Travel madeTravel = travelRepository.save(savedTravel);
        return travelMapper.mapToTravelParticipantsDto(madeTravel);
    }

    @Transactional
    public void confirmTravel(Long userId, Long travelId) {
        userTravelRepository.updateConfirmStatus(userId, travelId, true);
    }

    @Transactional
    public void denyTravel(Long userId, Long travelId) {
        if (isCreator(travelId, userId)) {
            throw new OperationNotAllowedForOwnerException("Creator cannot deny the travel");
        }
        userTravelRepository.deleteByUserIdAndTravelId(travelId, userId);
    }

    @Transactional
    public TravelDto updateTravel(TravelDto travelDto, Long userId) {
        if (!isCreator(travelDto.getId(), userId)) {
            throw new AccessDeniedException("The user does not have permission to perform this action");
        }
        Optional<Travel> optionalTravel = travelRepository.findById(travelDto.getId());
        if (optionalTravel.isEmpty()) {
            throw new NotFoundException("Travel not found");
        }
        Travel travel = travelMapper.mapToTravel(travelDto);
        travelRepository.save(travel);
        return travelDto;
    }

    @Transactional
    public void deleteTravel(Long travelId, Long userId) {
        if (!isCreator(travelId, userId)) {
            throw new AccessDeniedException("The user does not have permission to perform this action");
        }
        travelRepository.deleteById(travelId);
    }

    @Transactional
    public void deleteParticipant(Long travelId, Long participantId, Long userId) {
        if (!isCreator(travelId, userId)) {
            throw new AccessDeniedException("The user does not have permission to perform this action");
        }
        if (participantId.equals(userId)) {
            throw new OperationNotAllowedForOwnerException("Creator cannot remove himself from the travel");
        }
        userTravelRepository.deleteByUserIdAndTravelId(travelId, participantId);
    }

    @Transactional
    public void leaveTravel(Long travelId, Long userId) {
        if (isCreator(travelId, userId)) {
            throw new OperationNotAllowedForOwnerException("Creator cannot leave from the travel");
        }
        userTravelRepository.deleteByUserIdAndTravelId(travelId, userId);
    }

    @Transactional
    public UserDto addParticipant(Long travelId, String phoneNumber, Long userId) {
        if (!isCreator(travelId, userId)) {
            throw new AccessDeniedException("The user does not have permission to perform this action");
        }
        User user = userRepository.findByPhoneNumber(phoneNumber).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + phoneNumber));
        if (userTravelRepository.existsByUserIdAndTravelId(travelId, user.getId())) {
            throw new UserAlreadyExistException("Participant already exist in the travel");
        }
        UserTravel userTravel = new UserTravel();
        userTravel.setTravel(travelRepository.getReferenceById(travelId));
        userTravel.setUser(user);
        userTravel.setIsConfirmed(false);
        userTravelRepository.save(userTravel);
        return userMapper.mapToUserDto(user);
    }

//    private Travel getModified(Travel travel, TravelDto travelDto) {
//        if (!travel.getName().equals(travelDto.getName())) {
//            travel.setName(travelDto.getName());
//        }
//        if (!travel.getTotalBudget().equals(travelDto.getTotalBudget())) {
//            travel.setTotalBudget(travelDto.getTotalBudget());
//        }
//        if (!travel.getDateOfBegin().equals(travelDto.getDateOfBegin())) {
//            travel.setDateOfBegin(travelDto.getDateOfBegin());
//        }
//        if (!travel.getDateOfEnd().equals(travelDto.getDateOfEnd())) {
//            travel.setDateOfEnd(travelDto.getDateOfEnd());
//        }
//        return travel;
//    }

    private boolean isCreator(Long travelId, Long creatorId) {
        return travelRepository.existsTravelByIdAndCreatorId(travelId, creatorId);
    }
}
