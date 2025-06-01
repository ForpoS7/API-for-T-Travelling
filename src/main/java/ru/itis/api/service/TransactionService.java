package ru.itis.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.api.dto.*;
import ru.itis.api.entity.Transaction;
import ru.itis.api.entity.Travel;
import ru.itis.api.entity.User;
import ru.itis.api.entity.UserTransaction;
import ru.itis.api.exception.NotFoundException;
import ru.itis.api.exception.UserNotFoundException;
import ru.itis.api.mapper.TransactionMapper;
import ru.itis.api.repository.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TravelRepository travelRepository;
    private final UserTravelRepository userTravelRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    public List<TransactionDto> getTransactions(Long travelId, Long userId) {
        if (travelRepository.findById(travelId).isEmpty()) {
            throw new NotFoundException("Travel not found");
        }
        List<Transaction> transactions = transactionRepository.findAllByTravelId(travelId);
        if (transactions.isEmpty()) {
            return new ArrayList<>();
        }
        validateUserAccessToTravel(userId, travelId);
        return transactions.stream().map(transactionMapper::mapToTransactionDto).toList();
    }

    public TransactionParticipantsDto getTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));
        if (!userTravelRepository.existsByUserIdAndTravelId(userId, transaction.getTravel().getId())) {
            throw new AccessDeniedException("The user does not have permission to perform this action");
        }
        return transactionMapper.mapToTransactionParticipantsDto(transaction);
    }

    @Transactional
    public TransactionParticipantsDto createTransactionsWithParticipants(
            RequestTransactionDto requestTransactionDto,
            Long travelId,
            User creator) {
        validateUserAccessToTravel(creator.getId(), travelId);
        Travel travel = travelRepository.findById(travelId).orElseThrow(
                () -> new NotFoundException("Travel not found")
        );
        if (!travel.getIsActive()) {
            throw new AccessDeniedException("Travel is not active");
        }
        Transaction transaction = transactionMapper.mapToTransaction(requestTransactionDto)
                .setCreator(creator)
                .setTravel(travel);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.mapToTransactionParticipantsDto(
                transactionRepository.save(
                        savedTransaction.setUsers(
                                mapToUserTransactions(savedTransaction, requestTransactionDto.getParticipant())
                        )
                )
        );
    }

    @Transactional
    public TransactionParticipantsDto updateTransaction(
            UpdateTransactionDto transactionDto, Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));
        validateUserAccessToTravel(userId, transaction.getTravel().getId());
        if (!transaction.getTravel().getIsActive()) {
            throw new AccessDeniedException("Travel is not active");
        }
        transaction.setCategory(transactionDto.getCategory())
                .setDescription(transactionDto.getDescription())
                .setTotalCost(transactionDto.getTotalCost());
        return transactionMapper.mapToTransactionParticipantsDto(
                transactionRepository.save(
                        transaction.setUsers(
                                mapToUserTransactions(transaction, transactionDto.getParticipant())
                        )
                )
        );
    }

    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));
        validateUserAccessToTravel(userId, transaction.getTravel().getId());
        if (!transaction.getTravel().getIsActive()) {
            throw new AccessDeniedException("Travel is not active");
        }
        transactionRepository.delete(transaction);
    }

    private List<UserTransaction> mapToUserTransactions(
            Transaction transaction, List<RequestUserTransactionDto> userTransactionDtos) {
        List<User> participants = userRepository.findAllByPhoneNumbers(
                userTransactionDtos.stream()
                        .map(RequestUserTransactionDto::getPhoneNumber)
                        .toList());
        if (participants.isEmpty()) {
            throw new UserNotFoundException("Users not found");
        }
        Map<String, RequestUserTransactionDto> dtoMap = userTransactionDtos.stream()
                .collect(Collectors.toMap(RequestUserTransactionDto::getPhoneNumber, Function.identity()));
        return participants.stream()
                .map(participant -> {
                    RequestUserTransactionDto dto = dtoMap.get(participant.getPhoneNumber());
                    BigDecimal shareAmount = dto.getShareAmount();
                    Boolean isRepaid = BigDecimal.ZERO.compareTo(shareAmount) == 0;
                    return new UserTransaction()
                            .setTransaction(transaction)
                            .setUser(participant)
                            .setIsRepaid(isRepaid)
                            .setShareAmount(shareAmount);
                })
                .collect(Collectors.toList());
    }

    public void validateUserAccessToTravel(Long userId, Long travelId) {
        if (!userTravelRepository.existsByUserIdAndTravelId(userId, travelId)) {
            throw new AccessDeniedException("The user does not have permission to perform this action");
        }
    }
}
