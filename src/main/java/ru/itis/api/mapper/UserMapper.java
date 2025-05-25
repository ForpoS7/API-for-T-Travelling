package ru.itis.api.mapper;

import org.mapstruct.Mapper;
import ru.itis.api.dto.UserDto;
import ru.itis.api.dto.UserTransactionDto;
import ru.itis.api.entity.User;
import ru.itis.api.entity.UserTransaction;
import ru.itis.api.entity.UserTravel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface UserMapper {
    UserDto mapToUserDto(User user);

    default UserDto userTravelToUserDto(UserTravel userTravel) {
        if (userTravel == null || userTravel.getUser() == null) {
            return null;
        }
        return mapToUserDto(userTravel.getUser());
    }

    default List<UserDto> userTravelListToUserDtoList(List<UserTravel> list) {
        if (list == null) {
            return List.of();
        }
        return list.stream()
                .map(this::userTravelToUserDto)
                .collect(Collectors.toList());
    }

    default List<UserTransactionDto> mapUserTransactions(List<UserTransaction> userTransactions) {
        if (userTransactions == null) return new ArrayList<>();
        return userTransactions.stream()
                .map(this::mapUserTransaction)
                .collect(Collectors.toList());
    }

    default UserTransactionDto mapUserTransaction(UserTransaction userTransaction) {
        if (userTransaction == null) return null;
        return new UserTransactionDto()
                .setFirstName(userTransaction.getUser().getFirstName())
                .setLastName(userTransaction.getUser().getLastName())
                .setPhoneNumber(userTransaction.getUser().getPhoneNumber())
                .setShareAmount(userTransaction.getShareAmount())
                .setIsRepaid(userTransaction.getIsRepaid());
    }
}