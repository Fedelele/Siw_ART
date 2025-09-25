package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Artwork;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CredentialsService credentialsService;

    //Saves the user in the database
    @Transactional
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    //Retrieves a user from the database by its id
    @Transactional
    public User getLoggedUser(){
        return this.credentialsService.getLoggedCredentials().getUser();
    }

    @Transactional
    public List<User> getAllUsers(){
       return this.userRepository.findAll();
    }

    @Transactional
    public User updateUserProfile(User loggedUser, User updatedUser){
        //Check in the db if there is already another user with the same email
        Optional<User> userWithSameEmail = this.userRepository.findByEmail(updatedUser.getEmail());
        if(userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(loggedUser.getId())){
            throw new IllegalArgumentException("There is already a user with this email");
        }
        loggedUser.setName(updatedUser.getName());
        loggedUser.setSurname(updatedUser.getSurname());
        loggedUser.setEmail(updatedUser.getEmail());
        return this.userRepository.save(loggedUser);
    }

    @Transactional
    public boolean toggleFavorite(User user, Artwork artwork){
        boolean isFavorite;
        //Checks if the artwork is already in the user's favorites
        if(user.getFavoriteArts().contains(artwork)){
            //TRUE: Once toggled it is removed from the list
            user.getFavoriteArts().remove(artwork);
            isFavorite = false;
        }else {
            //FALSE: Once toggled it is added to the list
            user.getFavoriteArts().add(artwork);
            isFavorite = true;
        }
        this.userRepository.save(user);
        return isFavorite;
    }


}
