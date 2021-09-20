package fr.airfrance.userregisterservice.services;

import fr.airfrance.userdata.exceptions.UserApiException;
import fr.airfrance.userdata.models.User;
import fr.airfrance.userdata.repository.UserRepository;
import fr.airfrance.userdata.validators.OnCreate;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@Service
@AllArgsConstructor
@Validated
public class UserRegistrationService {

    private UserRepository userRepository;

    private RestTemplate restTemplate;

    /**
     * @see UserRepository#save(User)
     * @param user
     * @return User
     */
    @Validated(OnCreate.class)
    public User saveUser(@Valid User user) throws UserApiException {

        //Verify if the user exists already
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://USER-DISPLAY-SERVICE/user/exists/"+ user.getEmail())
                .queryParam("onlyActif", "no");

        //TODO: catch possible errors here!
        try{
            HttpEntity<Boolean> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Boolean.class);

            if(response.hasBody() && response.getBody())
                throw new UserApiException("existing.user.registration.attempt.occurred", new Object[]{user.getEmail()});
        }catch (Exception exception){
            UserApiException ex = new UserApiException("user.display.service.joining.attempt.occurred", null, HttpStatus.SERVICE_UNAVAILABLE, exception);
            ex.setStackTrace(exception.getStackTrace());
            throw ex;
        }


        return this.userRepository.save(user);
    }
}
