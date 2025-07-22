package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Museum;
import it.uniroma3.siw.repository.MuseumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MuseumService {

    @Autowired
    private MuseumRepository museumRepository;

    @Transactional
    public Museum getMuseumById(Long id) {
        return museumRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<Museum> getAllMuseums() {
        return museumRepository.findAll();
    }

    @Transactional
    public void saveMuseum(Museum museum) {
        museumRepository.save(museum);
    }

    @Transactional
    public void deleteMuseum(Museum museum) {
        museumRepository.delete(museum);
    }

    @Transactional
    public List<Museum> searchMuseums(String name) {
        return museumRepository.searchMuseums(name);
    }

    public boolean existsByName(String name) {
        return museumRepository.existsByName(name);
    }

    @Transactional
    public void updateMuseum(Long id, Museum updatedMuseum) {
        Museum oldMuseum = this.museumRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Museum with id " + id + " does not exist"));

        if (museumRepository.existsByName(updatedMuseum.getName()) && !this.getMuseumById(id).getName().equals(updatedMuseum.getName())) {
            throw new IllegalStateException("A museum with this name already exists");
        }

        oldMuseum.setName(updatedMuseum.getName());
        oldMuseum.setAddress(updatedMuseum.getAddress());
        oldMuseum.setDescription(updatedMuseum.getDescription());
        this.museumRepository.save(oldMuseum);
    }
}
