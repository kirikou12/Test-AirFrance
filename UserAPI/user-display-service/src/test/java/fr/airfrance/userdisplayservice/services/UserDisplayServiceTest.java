package fr.airfrance.userdisplayservice.services;

import fr.airfrance.userdata.models.User;
import fr.airfrance.userdata.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserDisplayServiceTest {

    UserDisplayService userDisplayService;

    @Mock
    UserRepository userRepository;

    User activeUser;
    String activeUserEmail = "active@gmail.com";

    User inactiveUser;
    String inactiveUserEmail = "inactive@gmail.com";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDisplayService = new UserDisplayService(userRepository);

        activeUser = User.builder()
                .email(activeUserEmail)
                .active(true)
                .firstName("fName")
                .lastName("lName")
                .build();

        inactiveUser = User.builder()
                .email(inactiveUserEmail)
                .active(false)
                .firstName("fName")
                .lastName("lName")
                .build();
    }

    @Test
    void getUserByEmail() {

        //Active user
        when(userRepository.findUserByEmail(activeUserEmail)).thenReturn(activeUser);
        User userActive = this.userDisplayService.getUserByEmail(activeUserEmail);
        assertNotNull(userActive);
        assertEquals(activeUser.getEmail(), userActive.getEmail());
        assertEquals(activeUser.getFirstName(), userActive.getFirstName());
        assertEquals(activeUser.getLastName(), userActive.getLastName());
        assertEquals(activeUser.isActive(), userActive.isActive());
        verify(userRepository, times(1)).findUserByEmail(activeUserEmail);

        //Inactive user
        when(userRepository.findUserByEmail(inactiveUserEmail)).thenReturn(inactiveUser);
        User userInactive = this.userDisplayService.getUserByEmail(inactiveUserEmail);
        assertNotNull(userInactive);
        assertEquals(inactiveUser.getEmail(), userInactive.getEmail());
        assertEquals(inactiveUser.getFirstName(), userInactive.getFirstName());
        assertEquals(inactiveUser.getLastName(), userInactive.getLastName());
        assertEquals(inactiveUser.isActive(), userInactive.isActive());
        verify(userRepository, times(1)).findUserByEmail(inactiveUserEmail);
    }

    @Test
    void getActifUserByEmail() {
        //Active user
        when(userRepository.findUserByEmailAndActiveTrue(activeUserEmail)).thenReturn(activeUser);
        User userActive = this.userDisplayService.getActifUserByEmail(activeUserEmail);
        assertNotNull(userActive);
        assertEquals(activeUser.getEmail(), userActive.getEmail());
        assertEquals(activeUser.getFirstName(), userActive.getFirstName());
        assertEquals(activeUser.getLastName(), userActive.getLastName());
        assertEquals(activeUser.isActive(), userActive.isActive());
        verify(userRepository, times(1)).findUserByEmailAndActiveTrue(activeUserEmail);

        //Inactive user
        when(userRepository.findUserByEmailAndActiveTrue(inactiveUserEmail)).thenReturn(null);
        User userInactive = this.userDisplayService.getActifUserByEmail(inactiveUserEmail);
        assertNull(userInactive);
        verify(userRepository, times(1)).findUserByEmailAndActiveTrue(inactiveUserEmail);
    }
}