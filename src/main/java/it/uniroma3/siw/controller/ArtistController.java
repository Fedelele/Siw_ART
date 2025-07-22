package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ArtistController {

    //To log errors when it fails at saving an entity
    private static final Logger log = LoggerFactory.getLogger(ArtistController.class);

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private ArtistService artistService;

    @GetMapping(value = "/admin/formNewArtist")
    public String formNewArtist(Model model){
        model.addAttribute("artist", new Artist());
        return "admin/formNewArtist";
    }

    @PostMapping("/admin/artist/new")
    public String artistNew(@Valid @ModelAttribute Artist artist,
                            BindingResult bindingResult,
                            @RequestParam("image")MultipartFile image,
                            Model model, RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()){
            return "admin/formNewArtist";
        }

        String originalFileName = image.getOriginalFilename();
        String extension = "";
        if(originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        try{
            Path uploadPath = Paths.get("C:/Users/wufed/Desktop/uploads-siw-art/artist-photo/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(uniqueFilename);
            image.transferTo(filePath.toFile());
            artist.setImageUrl(String.format("/artist-photo/%s", uniqueFilename));
            artistService.saveArtist(artist);

        } catch (IOException e) {
            log.error("Error while saving the file", e);
            model.addAttribute("error", "Error while saving the file");
            return "admin/formNewArtist";
        }
        redirectAttributes.addFlashAttribute("success", "Artist added successfully");
        return "redirect:/artist/all";
    }


    @GetMapping("/admin/formUpdateArtist/{id}")
    public String formUpdateArtist(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
        Artist artist = this.artistService.getArtistById(id);
        if(artist == null){
            redirectAttributes.addFlashAttribute("error", "Artist not found");
            return "redirect:/artist/all";
        }
        model.addAttribute("artist", artist);
        return "admin/formNewArtist";
    }

    @PostMapping("/admin/updateArtist/{id}")
    public String updateArtist(@PathVariable Long id, @Valid @ModelAttribute Artist artist,
                               BindingResult bindingResult,RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "admin/formNewArtist";
        }
        try{
            artistService.updateArtist(id, artist);
            redirectAttributes.addFlashAttribute("success", "Artist updated successfully");
            return "redirect:/artist/all";
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/artist/all";
    }

    @GetMapping("/artist/details/{id}")
    public String artist(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
        Artist artist = this.artistService.getArtistById(id);
        if(artist == null){
            redirectAttributes.addFlashAttribute("error", "Artist not found");
            return "redirect:/artist/all";
        }

        model.addAttribute("artist", artist);
        model.addAttribute("artworks", artist.getArtworks());

        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        model.addAttribute("isLoggedIn", loggedCredentials != null);
        return "artist";
    }

    @GetMapping("/artist/all")
    public String getArtists(Model model){
        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        if(loggedCredentials == null){
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("role", "NOROLE");
        }else{
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("role", loggedCredentials.getRole());
        }
        model.addAttribute("artists", this.artistService.getAllArtists());
        return "artists";
    }

    @PostMapping("/admin/artist/delete/{id}")
    public String deleteArtist(@PathVariable Long id, Model model){
        Artist artist = this.artistService.getArtistById(id);
        if(artist == null){
            model.addAttribute("error", "Artist not found");
            return "error";
        }
        this.artistService.deleteArtist(artist);
        return "redirect:/artist/all";
    }

    @GetMapping("/artists/search")
    public String searchArtists(@RequestParam(required = false) String name,@RequestParam(required = false) String surname, Model model){
        List<Artist> artists = artistService.searchArtist(name, surname);
        model.addAttribute("artists", artists);
        model.addAttribute("name", name);
        model.addAttribute("surname", surname);

        return "artists";
    }
}
