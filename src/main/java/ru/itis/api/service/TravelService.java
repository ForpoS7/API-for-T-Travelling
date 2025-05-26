package ru.itis.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.api.dto.RequestTravelDto;
import ru.itis.api.dto.RequestTravelParticipantsDto;
import ru.itis.api.dto.TravelDto;
import ru.itis.api.dto.TravelParticipantsDto;
import ru.itis.api.entity.Travel;
import ru.itis.api.entity.User;
import ru.itis.api.entity.UserTravel;
import ru.itis.api.exception.NotFoundException;
import ru.itis.api.exception.OperationNotAllowedForOwnerException;
import ru.itis.api.mapper.TravelMapper;
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
        participants.forEach(participant -> {
            UserTravel userTravel = new UserTravel()
                    .setTravel(savedTravel)
                    .setUser(participant)
                    .setIsConfirmed(false);
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
    public RequestTravelParticipantsDto updateTravel(RequestTravelParticipantsDto requestTravelParticipantsDto, Long userId) {
        if (!isCreator(requestTravelParticipantsDto.getId(), userId)) {
            throw new AccessDeniedException("The user does not have permission to perform this action");
        }
        Optional<Travel> optionalTravel = travelRepository.findById(requestTravelParticipantsDto.getId());
        if (optionalTravel.isEmpty()) {
            throw new NotFoundException("Travel not found");
        }

        Travel existingTravel = optionalTravel.get();
        List<String> newPhoneNumbers = requestTravelParticipantsDto.getParticipantPhones()
                .stream()
                .filter(phoneNumber -> existingTravel.getUsers()
                        .stream()
                        .noneMatch(userTravel -> userTravel.getUser().getPhoneNumber().equals(phoneNumber)))
                .toList();

        List<User> invitedUsers = userRepository.findAllByPhoneNumbers(newPhoneNumbers);
        Travel updatedTravel = travelMapper.mapToTravel(requestTravelParticipantsDto);
        updatedTravel.setCreator(existingTravel.getCreator());
        updatedTravel.setIsActive(existingTravel.getIsActive());
        invitedUsers.forEach(user -> {
            UserTravel userTravel = new UserTravel()
                    .setUser(user)
                    .setTravel(updatedTravel)
                    .setIsConfirmed(false);
            updatedTravel.getUsers().add(userTravel);
        });
        travelRepository.save(updatedTravel);

        return requestTravelParticipantsDto.setParticipantPhones(
                invitedUsers.stream()
                        .map(User::getPhoneNumber)
                        .toList()
        );
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

    private boolean isCreator(Long travelId, Long creatorId) {
        return travelRepository.existsTravelByIdAndCreatorId(travelId, creatorId);
    }
}
