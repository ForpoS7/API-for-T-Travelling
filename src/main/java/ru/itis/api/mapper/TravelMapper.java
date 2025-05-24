package ru.itis.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itis.api.dto.RequestTravelDto;
import ru.itis.api.dto.TravelDto;
import ru.itis.api.dto.TravelParticipantsDto;
import ru.itis.api.entity.Travel;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = UserMapper.class)
public interface TravelMapper {

    @Mapping(target = "isActive", constant = "true")
    Travel mapToTravel(RequestTravelDto requestTravel);

    @Mapping(target = "creator", source = "creator")
    @Mapping(target = "participants", source = "users")
    TravelParticipantsDto mapToTravelParticipantsDto(Travel travel);

    TravelDto mapToTravelDto(Travel travel);

    Travel mapToTravel(TravelDto travelDto);
}
