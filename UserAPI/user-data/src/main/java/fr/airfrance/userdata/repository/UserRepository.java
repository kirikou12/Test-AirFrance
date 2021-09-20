package fr.airfrance.userdata.repository;

import fr.airfrance.userdata.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Gets user by email.
     * @param email user's email
     * @return User or null
     */
    User findUserByEmail(String email);

    /**
     * Gets only actif user by email.
     * @param email user's email
     * @return User or null
     */
    User findUserByEmailAndActiveTrue(String email);

    /**
     * Saves new user to the data base.
     * @param user the newely created user with null id
     * @return User the just saved user
     */
    User save(User user);

    /**
     * Verify wither the user exists or not by their email
     * @param email
     * @return true or false
     */
    boolean existsByEmail(String email);
}
