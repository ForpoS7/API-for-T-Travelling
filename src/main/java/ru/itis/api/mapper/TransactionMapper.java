package ru.itis.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itis.api.dto.RequestTransactionDto;
import ru.itis.api.dto.TransactionDto;
import ru.itis.api.dto.TransactionParticipantsDto;
import ru.itis.api.entity.Transaction;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = UserMapper.class)
public interface TransactionMapper {

    TransactionDto mapToTransactionDto(Transaction transaction);

    Transaction mapToTransaction(RequestTransactionDto requestTransactionDto);

    @Mapping(target = "creator", source = "creator")
    @Mapping(target = "participants", source = "users")
    TransactionParticipantsDto mapToTransactionParticipantsDto(Transaction transaction);
}
