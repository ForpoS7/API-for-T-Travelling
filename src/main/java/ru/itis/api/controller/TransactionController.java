package ru.itis.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.itis.api.dto.*;
import ru.itis.api.security.details.UserDetailsImpl;
import ru.itis.api.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Operations related to managing transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @Operation(summary = "Get all transactions for a travel",
            description = "Returns list of transactions associated with a specific travel.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of transactions")
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @ApiResponse(responseCode = "404", description = "Transactions not found", content = @Content)
    public ResponseEntity<List<TransactionDto>> getTransactionsByTravelId(
            @RequestParam("travelId") Long travelId,
            @AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        return ResponseEntity.ok(transactionService
                .getTransactions(travelId, curUserDetails.getUser().getId()));
    }

    @Operation(summary = "Create a new transaction",
            description = "Creates a new transaction and assigns it to the specified travel.")
    @ApiResponse(responseCode = "201", description = "Transaction successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @ApiResponse(responseCode = "404", description = "Travel not found", content = @Content)
    @PostMapping
    public ResponseEntity<TransactionParticipantsDto> createTransaction(
            @RequestParam("travelId") Long travelId,
            @Valid @RequestBody RequestTransactionDto requestTransactionDto,
            @AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService
                        .createTransactionsWithParticipants(requestTransactionDto, travelId, curUserDetails.getUser()));
    }

    @Operation(summary = "Get transaction by ID",
            description = "Returns detailed information about a specific transaction.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved transaction details")
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionParticipantsDto> getTransactionById(
            @PathVariable Long transactionId,
            @AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        return ResponseEntity.ok(transactionService
                .getTransaction(transactionId, curUserDetails.getUser().getId()));
    }

    @Operation(summary = "Update an existing transaction",
            description = "Updates transaction details including participants and amounts.")
    @ApiResponse(responseCode = "200", description = "Transaction successfully updated")
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionParticipantsDto> updateTransaction(
            @Valid @RequestBody UpdateTransactionDto transactionDto,
            @AuthenticationPrincipal UserDetailsImpl curUserDetails,
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService
                        .updateTransaction(transactionDto, transactionId, curUserDetails.getUser().getId()));
    }

    @Operation(summary = "Delete transaction by ID",
            description = "Deletes a transaction if the current user is allowed to.")
    @ApiResponse(responseCode = "200", description = "Transaction successfully deleted")
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticationPrincipal UserDetailsImpl curUserDetails,
            @PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId, curUserDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }
}
