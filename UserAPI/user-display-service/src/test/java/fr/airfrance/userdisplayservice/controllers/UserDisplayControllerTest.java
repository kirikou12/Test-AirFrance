package fr.airfrance.userdisplayservice.controllers;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import fr.airfrance.userdata.exceptions.UserApiException;
import fr.airfrance.userdata.models.User;
import fr.airfrance.userdisplayservice.services.UserDisplayService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.validation.ConstraintViolationException;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserDisplayController.class)
@AutoConfigureDataMongo
@TestPropertySource({"classpath:ValidationMessages.properties", "classpath:messages.properties"})
class UserDisplayControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private UserDisplayService userDisplayService;

    @Value("${UserDisplayController.getUser.email.invalid}")
    String invalidEmailMessage;

    @Value("${ConstraintViolationException.occurred}")
    String constraintViolationOccurred;

    @Autowired
    private MessageSource messageSource;



    User activeUser;
    String activeUserEmail = "active@gmail.com";

    User inactiveUser;
    String inactiveUserEmail = "inactive@gmail.com";

    @BeforeEach
    void setUp() {

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
    void getActiveUserWithoutDefiningParamOnlyActif_ExpectActiveUser() throws Exception {
        // GIVEN
        when(userDisplayService.getActifUserByEmail(activeUserEmail)).thenReturn(activeUser);

        // WHEN
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/user-api/v1.0/display/byEmail/" + activeUserEmail)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // THEN
         this.mockMvc.perform(requestBuilder)
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.email").value(activeUserEmail))
                 .andExpect(jsonPath("$.firstName").value("fName"))
                 .andExpect(jsonPath("$.lastName").value("lName"))
                 .andExpect(jsonPath("$.active").value(true))
                .andDo(print());

         verify(userDisplayService, times(1)).getActifUserByEmail(activeUserEmail);
    }

    @Test
    void getInactiveUser_WithOnlyActifParmEqNo_ExpectInactiveUser() throws Exception {
        // GIVEN
        when(userDisplayService.getUserByEmail(inactiveUserEmail)).thenReturn(inactiveUser);

        // WHEN
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/user-api/v1.0/display/byEmail/" + inactiveUserEmail)
                .contentType(MediaType.APPLICATION_JSON)
                .param("onlyActif", "no");

        // THEN
        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.active").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(inactiveUserEmail));

        verify(userDisplayService, times(1)).getUserByEmail(inactiveUserEmail);
    }

    @Test
    void getNoExistingActifUser_WithOnlyActifParamDefaultValue_ExpectUserNotFoundException() throws Exception {
        // GIVEN
        when(userDisplayService.getActifUserByEmail(activeUserEmail)).thenReturn(null);
        String expectedErrorMessage = messageSource.getMessage("UserNotFoundException.occurred", new Object[]{activeUserEmail}, Locale.getDefault());

        // WHEN
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/user-api/v1.0/display/byEmail/" + activeUserEmail)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // THEN
        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof UserApiException))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage));

        verify(userDisplayService, times(1)).getActifUserByEmail(activeUserEmail);
    }

    @Test
    void getInactiveUserWithOnlyActifParmEqYes_ExpectUserNotFoundException() throws Exception {
        // GIVEN
        when(userDisplayService.getActifUserByEmail(inactiveUserEmail)).thenReturn(null);
        String expectedErrorMessage = messageSource.getMessage("UserNotFoundException.occurred", new Object[]{inactiveUserEmail}, Locale.getDefault());

        // WHEN
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/user-api/v1.0/display/byEmail/" + inactiveUserEmail)
                .contentType(MediaType.APPLICATION_JSON)
                .param("onlyActif", "yes");

        // THEN
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result-> assertTrue(result.getResolvedException() instanceof UserApiException))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage));


        verify(userDisplayService, times(1)).getActifUserByEmail(inactiveUserEmail);
    }


    @Test
    void getInactiveUserWithInvalidEmail_ExpectMethodArgumentNotValidException() throws Exception {
        // GIVEN
        String invalidEmail = "invalidEmail";
        when(userDisplayService.getActifUserByEmail(invalidEmail)).thenReturn(null);

        // WHEN
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/user-api/v1.0/display/byEmail/" +invalidEmail)
                .contentType(MediaType.APPLICATION_JSON)
                .param("onlyActif", "yes");

        //DocumentContext documentContext = JsonPath.parse(this.mockMvc.perform(requestBuilder).andReturn().getResponse().getContentAsString());
        //documentContext.read("$.subErrors[0].message");

        // THEN
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result-> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(mvcResult -> assertThat(mvcResult.getResolvedException().getMessage(), CoreMatchers.containsString(invalidEmailMessage)))
                .andExpect(jsonPath("$.message").value(constraintViolationOccurred))
                .andExpect(jsonPath("$.subErrors[0].message").value(invalidEmailMessage));


        verify(userDisplayService, never()).getActifUserByEmail(invalidEmail);
    }
}