package fr.airfrance.userregisterservice.controllers;


import fr.airfrance.userdata.exceptions.UserApiException;
import fr.airfrance.userdata.models.User;
import fr.airfrance.userregisterservice.services.UserRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Service that allows to register a new User.
 */
@RestController
@RequestMapping("/user-api/v1.0/register")
@Slf4j
public class UserRegisterController {

    @Autowired
    UserRegistrationService userRegistrationService;

    /**
     * Register a new user
     * @param user
     * @return
     * @throws UserApiException
     */

    @Operation(summary = "Register a new user. Has to live in france and to be older than 18 years.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid user or user already exists",
                    content = @Content),
            @ApiResponse(responseCode = "503", description = "User display service si unreachable",
                    content = @Content) })
    @PostMapping("/frenchUser")
    public ResponseEntity<User> registerFrenchUser(@RequestBody @Valid User user) throws UserApiException {

        this.userRegistrationService.saveUser(user);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
