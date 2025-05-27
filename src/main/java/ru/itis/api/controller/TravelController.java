package ru.itis.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.itis.api.dto.*;
import ru.itis.api.exception.NotFoundException;
import ru.itis.api.exception.OperationNotAllowedForOwnerException;
import ru.itis.api.exception.UserAlreadyExistException;
import ru.itis.api.security.details.UserDetailsImpl;
import ru.itis.api.service.TravelService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class TravelController {

    private final TravelService travelService;

    @Operation(summary = "Get active travels for the current user",
            description = "Returns a list of active travels associated with the authenticated user. "
                    + "The method uses the current user's id to filter active travels. "
                    + "Only travels where the user has confirmed their participation (isConfirmed = true) are returned.")
    @ApiResponse(responseCode = "200",
            description = "Successful retrieval of active travels",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = TravelDto.class))))
    @ApiResponse(responseCode = "401",
            description = "Unauthorized - User is not authenticated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")))
    @GetMapping("/travels/active")
    public ResponseEntity<List<TravelDto>> getActiveTravels(@AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        List<TravelDto> activeTravels = travelService.getActiveTravels(curUserDetails.getUser().getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(activeTravels);
    }

    @Operation(summary = "Get completed travels for the current user",
            description = "Returns a list of completed travels associated with the authenticated user. "
                    + "The method uses the current user's id to filter completed travels. "
                    + "Only travels where the user has confirmed their participation (isConfirmed = true) are returned.")
    @ApiResponse(responseCode = "200",
            description = "Successful retrieval of completed travels",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = TravelDto.class))))
    @ApiResponse(responseCode = "401",
            description = "Unauthorized - User is not authenticated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")))
    @GetMapping("/travels/complete")
    public ResponseEntity<List<TravelDto>> getCompletedTravels(@AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        List<TravelDto> completedTravels = travelService.getCompletedTravels(curUserDetails.getUser().getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(completedTravels);
    }

    @Operation(summary = "Get details of a specific travel by id",
            description = "Returns detailed information about a travel, including participants and other metadata. "
                    + "The travel id is provided as a path variable.")
    @ApiResponse(responseCode = "200",
            description = "Successful retrieval of travel details",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TravelParticipantsDto.class)))
    @ApiResponse(responseCode = "401",
            description = "Unauthorized - User is not authenticated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(
                            value = "{\"message\": \"Unauthorized\"}")))
    @ApiResponse(responseCode = "404",
            description = "Travel not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Travel not found\"}")))
    @GetMapping("/travels/{travelId}")
    public ResponseEntity<TravelParticipantsDto> getTravel(
            @Parameter(description = "Id of the travel to retrieve",
                    required = true,
                    example = "1")
            @PathVariable Long travelId) {
        TravelParticipantsDto travel = travelService.getTravel(travelId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(travel);
    }

    @Operation(summary = "Create a new travel",
            description = "Creates a new travel based on the provided data. "
                    + "The method requires authentication and validates the input data. "
                    + "If the input data is invalid, a 400 Bad Request error will be returned. "
                    + "The authenticated user is automatically set as the creator of the travel, "
                    + "so there is no need to provide the creator in the request. ")
    @ApiResponse(responseCode = "200",
            description = "Successful creation of the travel. "
                    + "The response includes the phone numbers of successfully added participants. "
                    + "Phone numbers that were not added indicate that no user with such a phone number was found.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TravelParticipantsDto.class)))
    @ApiResponse(responseCode = "400",
            description = "Bad Request - Invalid input data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"timestamp\": \"2025-05-16T19:09:03.280+00:00\"," +
                                    " \"status\": 400," +
                                    " \"error\": \"Bad Request\"," +
                                    " \"path\": \"/api/v1/travels/create\" }")))
    @ApiResponse(responseCode = "401",
            description = "Unauthorized - User is not authenticated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")))
    @PostMapping("/travels/create")
    public ResponseEntity<TravelParticipantsDto> createTravel(@Valid @RequestBody RequestTravelDto requestTravel,
                                               @AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        TravelParticipantsDto travelParticipantsDto = travelService.saveTravel(requestTravel,
                curUserDetails.getUser());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(travelParticipantsDto);
    }

    @Operation(summary = "Confirm participation in a travel",
            description = "Confirms the participation of the authenticated user in a specific travel. "
                    + "The method requires authentication and uses the current user's id and the provided travel id.")
    @ApiResponse(responseCode = "200",
            description = "Successful confirmation of participation in the travel")
    @ApiResponse(responseCode = "401",
            description = "Unauthorized - User is not authenticated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")))
    @ApiResponse(responseCode = "404",
            description = "Not Found - The user is not associated with the specified travel",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": " +
                            "\"UserTravel not found for userId=1, travelId=2\"}")))
    @GetMapping("/travels/confirm/{travelId}")
    public ResponseEntity<Void> confirmTravel(
            @Parameter(description = "Id of the travel to confirm participation",
                    required = true,
                    example = "1")
            @PathVariable Long travelId,
            @AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        travelService.confirmTravel(curUserDetails.getUser().getId(), travelId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove user participation from a travel",
            description = "Removes the authenticated user's participation from a specific travel. "
                    + "The method requires authentication and uses the current user's id and the provided travel id.")
    @ApiResponse(responseCode = "200",
            description = "Successful removal of user participation (or the user was not associated with the travel)")
    @ApiResponse(responseCode = "401",
            description = "Unauthorized - User is not authenticated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")))
    @ApiResponse(responseCode = "409",
            description = "Conflict - The user is the creator of the travel and cannot remove their participation",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Creator cannot deny the travel\"}")))
    @DeleteMapping("/travels/deny/{travelId}")
    public ResponseEntity<Void> denyTravel(
            @Parameter(description = "Id of the travel to deny participation",
                    required = true,
                    example = "1")
            @PathVariable Long travelId,
            @AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        travelService.denyTravel(curUserDetails.getUser().getId(), travelId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update a travel",
            description = "Updates the details of an existing travel. "
                    + "The method requires authentication and validates that the authenticated user is the creator of the travel.")
    @ApiResponse(responseCode = "200",
            description = "Successful update of the travel",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = RequestTravelParticipantsDto.class)))
    @ApiResponse(responseCode = "400",
            description = "Bad Request - Invalid input data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"timestamp\": \"2025-05-16T19:09:03.280+00:00\"," +
                            " \"status\": 400," +
                            " \"error\": \"Bad Request\"," +
                            " \"path\": \"/api/v1/travels\" }")))
    @ApiResponse(responseCode = "401",
            description = "Unauthorized - User is not authenticated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")))
    @ApiResponse(responseCode = "403",
            description = "Forbidden - The user does not have permission to update the travel",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"The user does not have permission to perform this action\"}")))
    @ApiResponse(responseCode = "404",
            description = "Not Found - The travel does not exist",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Travel not found\"}")))
    @PutMapping("/travels")
    public ResponseEntity<RequestTravelParticipantsDto> updateTravel(@Valid @RequestBody RequestTravelParticipantsDto requestTravelParticipantsDto,
                                                  @AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        RequestTravelParticipantsDto updatedTravelDto = travelService.updateTravel(requestTravelParticipantsDto,
                curUserDetails.getUser().getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedTravelDto);
    }

    @Operation(summary = "Delete a travel",
            description = "Deletes an existing travel if the authenticated user is the creator of the travel. "
                    + "If the travel does not exist, no error is returned, and the operation is considered successful.")
    @ApiResponse(responseCode = "200",
            description = "Successful deletion of the travel (or the travel did not exist)")
    @ApiResponse(responseCode = "401",
            description = "Unauthorized - User is not authenticated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")))
    @ApiResponse(responseCode = "403",
            description = "Forbidden - The user does not have permission to delete the travel",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"The user does not have permission to perform this action\"}")))
    @DeleteMapping("/travels/{travelId}")
    public ResponseEntity<Void> deleteTravel(
            @Parameter(description = "Id of the travel to delete",
                    required = true,
                    example = "1")
            @PathVariable Long travelId,
            @AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        travelService.deleteTravel(travelId,
                curUserDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a participant from a travel",
            description = "Removes a participant from a specific travel. "
                    + "The method requires authentication and validates that the authenticated user is the creator of the travel. "
                    + "If the travel or participant does not exist, no error is returned, and the operation is considered successful.")
    @ApiResponse(responseCode = "200",
            description = "Successful removal of the participant (or the travel and participant did not exist)")
    @ApiResponse(responseCode = "401",
            description = "Unauthorized - User is not authenticated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")))
    @ApiResponse(responseCode = "403",
            description = "Forbidden - The user does not have permission to remove the participant",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"The user does not have permission to perform this action\"}")))
    @ApiResponse(responseCode = "409",
            description = "Conflict - The creator cannot remove themselves from the travel",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Creator cannot remove himself from the travel\"}")))
    @DeleteMapping("/travels/remove/{travelId}")
    public ResponseEntity<Void> deleteParticipant(
            @Parameter(description = "Id of the travel",
                    required = true,
                    example = "1")
            @PathVariable Long travelId,
            @Parameter(description = "Id of the participant to remove",
                    required = true,
                    example = "2")
            @RequestParam("userId") Long participantId,
            @AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        travelService.deleteParticipant(travelId,
                participantId,
                curUserDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Leave a travel",
            description = "Allows the authenticated user to leave a specific travel. "
                    + "If the travel or user association does not exist, the operation is considered successful, and no error is returned.")
    @ApiResponse(responseCode = "200",
            description = "Successful departure from the travel (or the travel/user association did not exist)")
    @ApiResponse(responseCode = "401",
            description = "Unauthorized - User is not authenticated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")))
    @ApiResponse(responseCode = "409",
            description = "Conflict - The creator cannot leave the travel",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class),
                    examples = @ExampleObject(value = "{\"message\": \"Creator cannot leave from the travel\"}")))
    @DeleteMapping("/travels/leave/{travelId}")
    public ResponseEntity<Void> leaveTravel(
            @Parameter(description = "Id of the travel to leave",
                    required = true,
                    example = "1")
            @PathVariable Long travelId,
            @AuthenticationPrincipal UserDetailsImpl curUserDetails) {
        travelService.leaveTravel(travelId,
                curUserDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }
}
