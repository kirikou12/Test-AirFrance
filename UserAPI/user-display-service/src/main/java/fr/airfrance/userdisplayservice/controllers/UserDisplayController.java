package fr.airfrance.userdisplayservice.controllers;

import fr.airfrance.userdata.exceptions.UserApiException;
import fr.airfrance.userdata.models.User;
import fr.airfrance.userdisplayservice.services.UserDisplayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

/**
 * Service that allows to display a User.
 */
@RestController
@RequestMapping("/user-api/v1.0/display")
@AllArgsConstructor
@Validated
@Slf4j
public class UserDisplayController {

    private final String NO = "no";
    private final String YES = "yes";


    UserDisplayService userDisplayService;


    @Operation(summary = "Get a user by its email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid email supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    @GetMapping("/byEmail/{userEmail}")
    public ResponseEntity<User> getUser(@Parameter(description = "email of user to be searched", required = true) @PathVariable(value="userEmail")
                                           @Email(message = "{UserDisplayController.getUser.email.invalid}") String emailParm,
                                        @Parameter(description = "look only for an active user", schema = @Schema(type = "string", required = false, defaultValue = "yes", allowableValues = {"yes", "no"}))  @RequestParam(name= "onlyActif", required = false, defaultValue = YES) String onlyActiveUsers)
            throws UserApiException {

        log.info("Email input: " + emailParm + ", filter on active users only ? " + onlyActiveUsers);

        User user = NO.equals(onlyActiveUsers) ? this.userDisplayService.getUserByEmail(emailParm) : this.userDisplayService.getActifUserByEmail(emailParm) ;

        if(user == null){
            throw new UserApiException("UserNotFoundException.occurred", new Object[]{emailParm}, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user);
    }



    @Operation(summary = "Verify if a user exists by its email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found or not",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)) })
    })
    @GetMapping("/exists/{userEmail}")
    public ResponseEntity<Boolean> verifyUserExists(@PathVariable(value="userEmail")
                                        @Email(message = "{UserDisplayController.getUser.email.invalid}") @Parameter(description = "email of user to be searched") String emailParm) {

        log.info("Verification Email input: " + emailParm);

        return ResponseEntity.ok(this.userDisplayService.verifUserExists(emailParm));
    }

}
