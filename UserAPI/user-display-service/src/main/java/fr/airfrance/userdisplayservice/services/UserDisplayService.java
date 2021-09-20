package fr.airfrance.userdisplayservice.services;

import fr.airfrance.userdata.models.User;
import fr.airfrance.userdata.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDisplayService {

    private UserRepository userRepository;

    /**
     * @see UserRepository#findUserByEmail(String)
     *
     * @param email
     * @return User or null
     */
    public User getUserByEmail(String email){
        return this.userRepository.findUserByEmail(email);
    }

    /**
     * @see UserRepository#findUserByEmailAndActiveTrue(String)
     * @param email
     * @return User or null
     */
    public User getActifUserByEmail(String email){
        return this.userRepository.findUserByEmailAndActiveTrue(email);
    }


    /**
     * @see UserRepository#existsByEmail(String) 
     * @param email
     * @return
     */
    public boolean verifUserExists(String email){
        return this.userRepository.existsByEmail(email);
    }

}
