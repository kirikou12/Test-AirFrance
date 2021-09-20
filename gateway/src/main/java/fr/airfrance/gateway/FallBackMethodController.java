package fr.airfrance.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackMethodController {

    @GetMapping("/userRegisterFallBack")
    public String userRegisterFallBackMethod(){
        return "Please try later, the user registration service is not available for the moment.";
    }

    @GetMapping("/userDisplayFallBack")
    public String userDisplayFallBackMethod(){
        return "Please try later, the user display service dis is not available for the moment.";
    }
}
