package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CredentialsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected CredentialsRepository credentialsRepository;

    public Credentials findByUsername(String username){
        return this.credentialsRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public Credentials saveCredentials(Credentials credentials){
        if(credentials.getRole() == null || !credentials.getRole().equals(Credentials.ADMIN_ROLE)){
            credentials.setRole(Credentials.USER_ROLE);
        }
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return this.credentialsRepository.save(credentials);
    }

    public Credentials getLoggedCredentials(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated()){
                return null;
            }
            String currentUsername = authentication.getName();
            return findByUsername(currentUsername);
        } catch (Exception e){
            return null;
        }
    }

    public boolean existsByUsername(String username){
        return this.credentialsRepository.existsByUsername(username);
    }

    @Transactional
    public void updatePassword(Credentials credentials, String oldPassword, String newPassword, String confirmPassword){
        if(!passwordEncoder.matches(oldPassword, credentials.getPassword())){
            throw new IllegalArgumentException("The old password is incorrect");
        }

        if(newPassword.length() < 5){
            throw new IllegalArgumentException("The new password must be at least 5 characters long");
        }

        if(this.passwordEncoder.matches(newPassword, credentials.getPassword())){
            throw new IllegalArgumentException("The new password must be different from the old one");
        }

        if(!newPassword.equals(confirmPassword)){
            throw new IllegalArgumentException("The passwords do not match");
        }
        credentials.setPassword(this.passwordEncoder.encode(newPassword));
        this.credentialsRepository.save(credentials);
    }
}
