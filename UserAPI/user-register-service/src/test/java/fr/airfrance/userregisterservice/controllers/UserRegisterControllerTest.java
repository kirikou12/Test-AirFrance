package fr.airfrance.userregisterservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import fr.airfrance.userdata.models.Address;
import fr.airfrance.userdata.models.User;
import fr.airfrance.userregisterservice.services.UserRegistrationService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRegisterController.class)
@AutoConfigureDataMongo
//@RestClientTest({UserRegistrationService.class})
//@EnableConfigurationProperties({UserValidationMessages.class, AddressValidationMessages.class})
class UserRegisterControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Validator validator;

    @MockBean
    RestTemplate restTemplate;


    @Value("${MethodArgumentNotValidException.occurred}")
    String methodArgumentNotValidOccurred;

    //@Autowired
    //private MockRestServiceServer server;

    //@Autowired
    //UserValidationMessages userValidationMessages;

    //@Autowired
    //AddressValidationMessages addressValidationMessages;

    @MockBean
    private UserRegistrationService userRegistrationService;

    private final String userEmail = "active@gmail.com";

    private String asJsonString(final Object object) {
        try {
            return this.objectMapper.writer().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> validationMessagesMap(Object object){

        Map<String, String> mapValidationMessages = new HashMap<>();
        validator.validate(object)
                .stream()
                .forEach(constraintViolation->
                    mapValidationMessages
                            .put(constraintViolation.getPropertyPath().toString(),
                                    constraintViolation.getMessage())
                );
        return mapValidationMessages;
    }


    @Test
    void registerValidAndInexistingUser() throws Exception {
        // GIVEN
        Address address = Address.builder()
                .cityName("cName")
                .countryName("France")
                .houseNumber(12)
                .street("street")
                .zipCode("77000").build();
        User user = User.builder()
                .active(true)
                .firstName("fName")
                .lastName("lName")
                .email(userEmail)
                .dateOfBirth(LocalDate.of(1990, 5, 24))
                .password("password")
                .address(address).build();

        when(userRegistrationService.saveUser(user)).thenReturn(user);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), same(User.class))).thenReturn(null);

        // WHEN
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user-api/v1.0/register/frenchUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));

        // THEN
        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userEmail));

        verify(userRegistrationService, times(1)).saveUser(any(User.class));
    }

    @Test
    void registerUserWithoutName_ExpectMethodArgumentNotValidException() throws Exception {
        // GIVEN
        Address address = Address.builder()
                .cityName("cName")
                .countryName("France")
                .houseNumber(12)
                .street("street")
                .zipCode("77000").build();
        User user = User.builder()
                .active(true)
                .lastName("lName")
                .email(userEmail)
                .dateOfBirth(LocalDate.of(1990, 5, 24))
                .password("password")
                .address(address).build();

        Map<String, String> mapValidationMessages = validationMessagesMap(user);

        // WHEN
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user-api/v1.0/register/frenchUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));


        // THEN
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result-> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException, "S'assurer qu'une exception de type MethodArgumentNotValidException a été déclenchée."))
                .andExpect(jsonPath("$.message").value(methodArgumentNotValidOccurred))
                .andExpect(jsonPath("$.subErrors[0].message").value(mapValidationMessages.get("firstName")));


        verify(userRegistrationService, never()).saveUser(any(User.class));
    }


    @Test
    void registerUserUnderAged_ExpectMethodArgumentNotValidException() throws Exception {
        // GIVEN
        Address address = Address.builder()
                .cityName("cName")
                .countryName("France")
                .houseNumber(12)
                .street("street")
                .zipCode("77000").build();
        User user = User.builder()
                .active(true)
                //.firstName("fName")
                .lastName("lName")
                .email(userEmail)
                .dateOfBirth(LocalDate.now())
                .password("password")
                .address(address).build();

        Map<String, String> mapValidationMessages = validationMessagesMap(user);


        // WHEN
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user-api/v1.0/register/frenchUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));


        // THEN
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result-> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException, "S'assurer qu'une exception de type MethodArgumentNotValidException a été déclenchée."))
                .andExpect(jsonPath("$.message").value(methodArgumentNotValidOccurred))
                .andExpect(jsonPath("$.subErrors[?(@.field == 'dateOfBirth')].message").value(mapValidationMessages.get("dateOfBirth")));


        verify(userRegistrationService, never()).saveUser(any(User.class));
    }

    @Test
    void registerUserShortPassword_ExpectMethodArgumentNotValidException() throws Exception {
        // GIVEN
        Address address = Address.builder()
                .cityName("cName")
                .countryName("France")
                .houseNumber(12)
                .street("street")
                .zipCode("77000").build();
        User user = User.builder()
                .active(true)
                .firstName("fName")
                .lastName("lName")
                .email(userEmail)
                .dateOfBirth(LocalDate.of(1990, 1,1))
                .password("pass")
                .address(address).build();

        Map<String, String> mapValidationMessages = validationMessagesMap(user);


        // WHEN
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user-api/v1.0/register/frenchUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));


        // THEN
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result-> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException, "S'assurer qu'une exception de type MethodArgumentNotValidException a été déclenchée."))
                .andExpect(jsonPath("$.message").value(methodArgumentNotValidOccurred))
                .andExpect(jsonPath("$.subErrors[?(@.field == 'password')].message").value(mapValidationMessages.get("password")));

        verify(userRegistrationService, never()).saveUser(any(User.class));
    }


    @Test
    void registerUserNotInFrance_ExpectMethodArgumentNotValidException() throws Exception {
        // GIVEN
        Address address = Address.builder()
                .cityName("cName")
                .countryName("US")
                .houseNumber(12)
                .street("street")
                .zipCode("77000").build();
        User user = User.builder()
                .active(true)
                .firstName("fName")
                .lastName("lName")
                .email(userEmail)
                .dateOfBirth(LocalDate.of(1990, 1,1))
                .password("password")
                .address(address).build();

        Map<String, String> mapValidationMessages = validationMessagesMap(user);


        // WHEN
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user-api/v1.0/register/frenchUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));


        // THEN
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result-> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException, "S'assurer qu'une exception de type MethodArgumentNotValidException a été déclenchée."))
                .andExpect(jsonPath("$.message").value(methodArgumentNotValidOccurred))
                .andExpect(jsonPath("$.subErrors[?(@.field == 'address.countryName')].message").value(mapValidationMessages.get("address.countryName")));

        verify(userRegistrationService, never()).saveUser(any(User.class));
    }


}