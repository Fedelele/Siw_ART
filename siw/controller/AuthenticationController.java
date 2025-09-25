package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Artwork;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.ArtworkService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;


@Controller
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private ArtworkService artworkService;

    @GetMapping("/")
    public String index(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model){
        Credentials credentials = credentialsService.getLoggedCredentials();
        if(credentials != null){
            model.addAttribute("user", credentials.getUser());
            model.addAttribute("isLoggedIn", true);
        }else{
            model.addAttribute("isLoggedIn", false);
        }

        List<Artwork> randomArtworks = artworkService.findRandomArtworks(8);
        model.addAttribute("randomArtworks", randomArtworks);
        return "home";
    }

    @GetMapping(value="/login")
    public String showLoginForm() {
        return "formLogin.html";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "formRegister.html";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult userBindingResult, @Valid @ModelAttribute Credentials credentials, BindingResult credentialsBindingResult, RedirectAttributes redirectAttributes){
        if(userBindingResult.hasErrors() || credentialsBindingResult.hasErrors()){
            return "formRegister.html";
        }
        credentials.setUser(user);
        credentials.setRole(Credentials.USER_ROLE);
        credentialsService.saveCredentials(credentials);
        redirectAttributes.addFlashAttribute("registrationSuccess", true);
        redirectAttributes.addFlashAttribute("userName", user.getName());
        return "redirect:/success";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success.html";
    }

    @GetMapping("/profile")
    public String showProfile(Model model){
        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        if(loggedCredentials == null){
            return "redirect:/login";
        }
        User user = loggedCredentials.getUser();
        Set<Artwork> favoriteArtwork = user.getFavoriteArts();
        model.addAttribute("user", user);
        model.addAttribute("favoriteArtwork", favoriteArtwork);
        model.addAttribute("credentials", loggedCredentials);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String formEditProfile(Model model){
        model.addAttribute("user", userService.getLoggedUser());
        model.addAttribute("credentials", credentialsService.getLoggedCredentials());
        return "formEditProfile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("user") User updatedUser, RedirectAttributes redirectAttributes){
        User oldUser = userService.getLoggedUser();
        if(oldUser == null){
            return "redirect:/login";
        }
        try {
            //Updating the user's details
            userService.updateUserProfile(oldUser, updatedUser);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
            return "redirect:/profile";
        }catch(IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/edit";
        }
    }

    @GetMapping("/profile/formEditPassword")
    public String formEditPassword(Model model){
        model.addAttribute("credentials", credentialsService.getLoggedCredentials());
        return "formEditPassword";
    }

    @PostMapping("/profile/edit/password")
    public String editPassword(@RequestParam("oldPassword") String oldPassword,
                               @RequestParam("newPassword") String newPassword,
                               @RequestParam("confirmPassword") String confirmPassword,
                               RedirectAttributes redirectAttributes){
        Credentials credentials = credentialsService.getLoggedCredentials();
        if(credentials == null){
            return "redirect:/login";
        }
        try{
            credentialsService.updatePassword(credentials, oldPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully");
            return "redirect:/profile";
        } catch(IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/formEditPassword";
        }
    }

}
