package fr.airfrance.userregisterservice.services;

import fr.airfrance.userdata.exceptions.UserApiException;
import fr.airfrance.userdata.models.User;
import fr.airfrance.userdata.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserRegistrationServiceTest {

    UserRegistrationService userRegistrationService;

    @Mock
    UserRepository userRepository;

    User activeUser;
    String activeUserEmail = "active@gmail.com";

    @Mock
    RestTemplate restTemplate;

    @Captor
    ArgumentCaptor<String> urlCaptor;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRegistrationService = new UserRegistrationService(userRepository, restTemplate);

        activeUser = User.builder()
                .email(activeUserEmail)
                .active(true)
                .firstName("fName")
                .lastName("lName")
                .build();
    }



    @Test
    void saveUser() throws UserApiException {
        when(userRepository.save(activeUser)).thenAnswer(i->{
            activeUser.setId("gen-id");
            return activeUser;
        });
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), same(User.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        User userActive = this.userRegistrationService.saveUser(activeUser);

        assertNotNull(userActive);
        assertNotNull(userActive.getId());
        assertEquals("gen-id", userActive.getId());

        verify(userRepository, times(1)).save(activeUser);

        verify(restTemplate, times(1)).exchange(urlCaptor.capture(), eq(HttpMethod.GET), any(HttpEntity.class), same(User.class));

        String url = urlCaptor.getValue();

        assertEquals("http://localhost:8082/user/"+ activeUser.getEmail() + "?onlyActif=no", url);
    }


    @Test
    void saveExistingUser() throws UserApiException {
        when(userRepository.save(activeUser)).thenReturn(activeUser);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), same(User.class)))
                .thenReturn(new ResponseEntity<User>(activeUser, HttpStatus.NOT_FOUND));

        UserApiException expectedException = assertThrows(UserApiException.class, ()->this.userRegistrationService.saveUser(activeUser));
        assertEquals("existing.user.registration.attempt.occurred", expectedException.getMessageKey());

        verify(userRepository, never()).save(activeUser);
    }
}