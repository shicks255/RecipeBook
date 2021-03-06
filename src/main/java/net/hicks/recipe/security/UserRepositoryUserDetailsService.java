package net.hicks.recipe.security;

import net.hicks.recipe.beans.User;
import net.hicks.recipe.config.RecipeBookException;
import net.hicks.recipe.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryUserDetailsService.class);

    final private UserRepository userRepo;
    final private PasswordEncoder passwordEncoder;

    @Autowired
    public UserRepositoryUserDetailsService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            return user;
        }

        throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    public User saveNewUser(User newUser) {
        String encryptedPassword = passwordEncoder.encoder().encode(newUser.getPassword());

        newUser.setPassword(encryptedPassword);

        try {
            userRepo.save(newUser);
        } catch (Exception e) {
            System.out.println(e);
            if (e instanceof DataIntegrityViolationException)
                throw new RecipeBookException(30, "Username exists", HttpStatus.BAD_REQUEST);
        }
        return newUser;
    }
}