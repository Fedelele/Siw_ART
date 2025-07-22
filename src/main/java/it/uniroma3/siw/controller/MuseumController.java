package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Museum;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MuseumService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class MuseumController {

    private static final Logger log = LoggerFactory.getLogger(MuseumController.class);

    @Autowired
    private MuseumService museumService;

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/admin/formNewMuseum")
    public String formNewMuseum(Model model){
        model.addAttribute("museum", new Museum());
        return "admin/formNewMuseum";
    }

    @PostMapping("/admin/museum/new")
    public String newMuseum(@Valid @ModelAttribute("museum") Museum museum, BindingResult bindingResult,
                            @RequestParam("image") MultipartFile image,
                            RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "admin/formNewMuseum";
        }
        if(museumService.existsByName(museum.getName())) {
            bindingResult.rejectValue("name", "duplicate", "A museum with this name already exists");
            return "admin/formNewMuseum";
        }
        if(!image.isEmpty()) {
            String originalFileName = image.getOriginalFilename();
            String extension = "";
            if(originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            try {
                Path uploadPath = Paths.get("C:/Users/wufed/Desktop/uploads-siw-art/museum-photo/");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(uniqueFilename);
                image.transferTo(filePath.toFile());
                museum.setImageUrl(String.format("/museum-photo/%s", uniqueFilename));
            } catch (Exception e) {
                log.error("Error while saving the file", e);
                bindingResult.reject("image.upload.fail", "Error while saving the file");
                return "admin/formNewMuseum";
            }
        }
        museumService.saveMuseum(museum);
        redirectAttributes.addFlashAttribute("success", "Museum added successfully");
        return "redirect:/museum/all";
    }

    @GetMapping("/museum/details/{id}")
    public String museum(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes){
        Museum museum = this.museumService.getMuseumById(id);
        if(museum == null){
            redirectAttributes.addFlashAttribute("error", "Museum not found");
            return "redirect:/museum/all";
        }
        model.addAttribute("museum", museum);
        model.addAttribute("artworks", museum.getArtworks());

        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        model.addAttribute("isLoggedIn", loggedCredentials != null);
        if(loggedCredentials != null){
            model.addAttribute("role", loggedCredentials.getRole());
        }else{
            model.addAttribute("role", "NOROLE");
        }
        return "museum";
    }

    @GetMapping("/museum/all")
    public String getMuseums(Model model){
        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        if(loggedCredentials == null){
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("role", "NOROLE");
        }else{
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("role", loggedCredentials.getRole());
        }
        model.addAttribute("museums", this.museumService.getAllMuseums());
        return "museums";
    }

    @PostMapping("/admin/museum/delete/{id}")
    public String deleteMuseum(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        try {
            Museum museum = this.museumService.getMuseumById(id);
            if (museum == null) {
                redirectAttributes.addFlashAttribute("error", "Museum not found.");
                return "redirect:/museum/all";
            }

            this.museumService.deleteMuseum(museum);

            redirectAttributes.addFlashAttribute("success", "Museum '" + museum.getName() + "' deleted successfully.");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete this museum because it contains artworks. Please remove them first.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during deletion.");
        }

        return "redirect:/museum/all";
    }

    @GetMapping("/admin/formUpdateMuseum/{id}")
    public String formUpdateMuseum(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Museum museum = this.museumService.getMuseumById(id);
        if (museum == null) {
            redirectAttributes.addFlashAttribute("error", "Museum not found");
            return "redirect:/museum/all";
        }
        model.addAttribute("museum", museum);
        return "admin/formNewMuseum";
    }

    @PostMapping("/admin/updateMuseum/{id}")
    public String updateMuseum(@PathVariable Long id, @Valid @ModelAttribute("museum") Museum museum,
                               BindingResult bindingResult, @RequestParam("image") MultipartFile image,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/formNewMuseum";
        }

        try {
            if (!image.isEmpty()) {
                String originalFileName = image.getOriginalFilename();
                String extension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                }
                String uniqueFilename = UUID.randomUUID().toString() + extension;
                try {
                    Path uploadPath = Paths.get("C:/Users/wufed/Desktop/uploads-siw-art/museum-photo/");
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    Path filePath = uploadPath.resolve(uniqueFilename);
                    image.transferTo(filePath.toFile());
                    museum.setImageUrl(String.format("/museum-photo/%s", uniqueFilename));
                    Museum existingMuseum = museumService.getMuseumById(id);
                    existingMuseum.setImageUrl(museum.getImageUrl());
                    museumService.saveMuseum(existingMuseum);

                } catch (Exception e) {
                    log.error("Error while saving the file", e);
                    redirectAttributes.addFlashAttribute("error", "Error while saving the image");
                    return "redirect:/admin/formUpdateMuseum/" + id;
                }
            }

            museumService.updateMuseum(id, museum);
            redirectAttributes.addFlashAttribute("success", "Museum updated successfully");
            return "redirect:/museum/details/" + id;

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/formUpdateMuseum/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/museum/all";
        }
    }
}
