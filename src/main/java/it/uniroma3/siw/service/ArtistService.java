package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Artwork;
import it.uniroma3.siw.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private ArtworkService artworkService;

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public List<Artist> getAllById(List<Long> id) {
        return this.artistRepository.findAllById(id);
    }

    public Artist getArtistById(Long id) {
        return artistRepository.findById(id).orElse(null);
    }

    public void saveArtist(Artist artist) {
        this.artistRepository.save(artist);
    }

    public void deleteArtist(Artist artist) {
        List<Artwork> artworksToDelete = new ArrayList<>(artist.getArtworks());
        for(Artwork a : artworksToDelete){
            artworkService.deleteArt(a);
        }
        this.artistRepository.delete(artist);
    }

    public boolean existsByNameAndSurname(String name, String surname) {
        return artistRepository.existsByNameAndSurname(name, surname);
    }

    public List<Artist> searchArtist(String name, String surname) {
        return this.artistRepository.searchAuthors(name, surname);
    }

    //Method to update the details of an author
    @Transactional
    public void updateArtist(Long id, Artist updatedArtist) {
        Artist oldArtist = this.artistRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Artist with id " + id + " does not exist"));
        oldArtist.setName(updatedArtist.getName());
        oldArtist.setSurname(updatedArtist.getSurname());
        oldArtist.setNationality(updatedArtist.getNationality());
        oldArtist.setDateOfBirth(updatedArtist.getDateOfBirth());
        oldArtist.setDateOfDeath(updatedArtist.getDateOfDeath());
        this.artistRepository.save(oldArtist);
    }
}
