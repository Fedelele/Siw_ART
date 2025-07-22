package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Artwork;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private MuseumService museumService;

    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private UserService userService;

    @GetMapping("/admin/formNewArtwork")
    public String formNewArtwork(Model model){
        model.addAttribute("artwork", new Artwork());
        model.addAttribute("artists", this.artistService.getAllArtists());
        model.addAttribute("museums", this.museumService.getAllMuseums());
        return "admin/formNewArtwork";
    }

    @PostMapping("/admin/artwork/new")
    public String newArtwork(@Valid @ModelAttribute Artwork artwork,
                             @RequestParam(value = "artists[]", required = false) List<Long> artistsIds,
                             BindingResult bindingResult, @RequestParam("image")MultipartFile image, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("artists", artistService.getAllArtists());
            model.addAttribute("museums", museumService.getAllMuseums());
            return "admin/formNewArtwork";
        }
        if(artistsIds != null && !artistsIds.isEmpty()){
            List<Artist> artists = artistService.getAllById(artistsIds);
            artwork.setArtists(artists);
        }
        String originalFileName = image.getOriginalFilename();
        String extension = "";
        if(originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        try{
            Path uploadPath = Paths.get("C:/Users/wufed/Desktop/uploads-siw-art/artwork-cover/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(uniqueFilename);
            image.transferTo(filePath.toFile());
            artwork.setImageUrl("/artwork-cover/" + uniqueFilename);
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.reject("image.upload.failed", "Couldn't save the image :(");
            model.addAttribute("artists", artistService.getAllArtists());
            model.addAttribute("museums", museumService.getAllMuseums());
            return "admin/formNewArtwork";
        }
    if(artwork.getArtists() != null){
        for(Artist a : artwork.getArtists())
            a.getArtworks().add(artwork);
    }
        artworkService.saveArtwork(artwork);
        return "redirect:/artwork/all";
    }

    @GetMapping("/admin/formUpdateArtwork/{id}")
    public String formUpdateArtwork(@PathVariable Long id, Model model){
        Artwork artwork = this.artworkService.getArtworkById(id);
        if(artwork == null){
            model.addAttribute("error", "Artwork not found");
            return "redirect:/artwork/all";
        }
        model.addAttribute("artwork", artwork);
        model.addAttribute("artists", this.artistService.getAllArtists());
        model.addAttribute("museums", this.museumService.getAllMuseums());
        return "admin/formNewArtwork";
    }

    @PostMapping("/admin/updateArtwork/{id}")
    public String updateArtwork(@PathVariable Long id, @Valid @ModelAttribute("artwork") Artwork artwork,
                                @RequestParam(value = "artists[]", required = false) List<Long> artistIds,
                                BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            model.addAttribute("artists", artistService.getAllArtists());
            return "admin/formNewArtwork";
        }
        try {
            artworkService.updateArtwork(id, artwork, artistIds);
            redirectAttributes.addFlashAttribute("success", "Artwork updated successfully");
            return "redirect:/artwork/details/" + id;
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            model.addAttribute("artwork", artwork);
            model.addAttribute("artists", artistService.getAllArtists());
            return "admin/formNewArtwork";
        }
    }

    @GetMapping("/artwork/details/{id}")
    public String artwork(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
        Artwork artwork = this.artworkService.getArtworkById(id);
        if(artwork == null){
            redirectAttributes.addFlashAttribute("error", "Artwork not found");
            return "redirect:/artwork/all";
        }
        model.addAttribute("artwork", artwork);

        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        if (loggedCredentials != null) {
            User user = loggedCredentials.getUser();
            model.addAttribute("isFavorite", user.getFavoriteArts().contains(artwork));
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("role", loggedCredentials.getRole());
            }else{
            model.addAttribute("isFavorite", false);
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("role", "NOROLE");
        }
        return "artwork";
    }

    @GetMapping("/artwork/all")
    public String getArtworks(Model model){
        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        if(loggedCredentials == null) {
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("role", "NOROLE");
        }else{
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("role", loggedCredentials.getRole());
        }
        model.addAttribute("artworks", this.artworkService.getAllArtworks());
        return "artworks";
    }

    @PostMapping("/admin/artwork/delete/{id}")
    public String deleteArtwork(@PathVariable Long id, Model model){
        Artwork artwork = this.artworkService.getArtworkById(id);
        if(artwork == null){
            model.addAttribute("error", "Artwork not found");
            return "redirect:/artwork/all";
        }
        this.artworkService.deleteArt(artwork);
        return "redirect:/artwork/all";
    }

    @PostMapping("/artwork/details/{id}/toggleFavorite")
    public String toggleFavoriteForm(@PathVariable("id") Long artworkId, RedirectAttributes redirectAttributes){
        Credentials credentials = credentialsService.getLoggedCredentials();
        if(credentials==null){
            return "redirect:/login";
        }

        User user = credentials.getUser();
        Artwork artwork = artworkService.getArtworkById(artworkId);

        if(artwork != null){
            userService.toggleFavorite(user, artwork);
        }else{
            redirectAttributes.addFlashAttribute("error", "Artwork could not be found");
        }
        return "redirect:/artwork/details/" + artworkId;
    }
}
