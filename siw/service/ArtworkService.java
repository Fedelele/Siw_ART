package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Artwork;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtworkService {

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private ArtistRepository artistRepository;

    public List<Artwork> getAllArtworks(){
        return artworkRepository.findAll();
    }

    public Artwork getArtworkById(Long id){
        return this.artworkRepository.findById(id).orElse(null);
    }

    @Transactional
    public void saveArtwork(Artwork artwork){
        this.artworkRepository.save(artwork);
    }

    @Transactional
    public void deleteArt(Artwork artwork){
        //To maintain bidirectional delete art from artists' list
        for(Artist artist : artwork.getArtists()){
            artist.getArtworks().remove(artwork);
        }
        //remove art from its museum's list
        if(artwork.getMuseum() != null){
            artwork.getMuseum().getArtworks().remove(artwork);
        }

        //remove art from users' favorites
        for(User user : artwork.getUsers()){
            user.getFavoriteArts().remove(artwork);
        }
        this.artworkRepository.delete(artwork);
    }

    public List<Artwork> getAllById(List<Long> id){
        return artworkRepository.findAllById(id);
    }

    public List<Artwork> searchArtworks(String title, Integer year){
        return artworkRepository.searchArtworks(title, year);
    }

    public boolean existsByTitleAndYear(String title, Integer year){
        return artworkRepository.existsByTitleAndYear(title, year);
    }

    @Transactional
    public void updateArtwork(Long artworkId, Artwork updatedArtwork, List<Long> artistsIds){
        Artwork oldArtwork = this.artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("Artwork not found"));

        Optional<Artwork> duplicate = artworkRepository.findByTitleAndYear(updatedArtwork.getTitle(), updatedArtwork.getYear());
        if(duplicate.isPresent() && !duplicate.get().getId().equals(artworkId)){
            throw new IllegalArgumentException("Artwork with this title and year already exists");
        }
        oldArtwork.setTitle(updatedArtwork.getTitle());
        oldArtwork.setYear(updatedArtwork.getYear());
        oldArtwork.setMuseum(updatedArtwork.getMuseum());
        oldArtwork.setDescription(updatedArtwork.getDescription());

        for(Artist artist : new ArrayList<>(oldArtwork.getArtists())){
            artist.getArtworks().remove(oldArtwork);
        }
        oldArtwork.getArtists().clear();

        if(artistsIds != null && !artistsIds.isEmpty()){
            List<Artist> artists = artistRepository.findAllById(artistsIds);
            for(Artist artist : artists){
                oldArtwork.getArtists().add(artist);
                artist.getArtworks().add(oldArtwork);
            }

        }
        this.artworkRepository.save(oldArtwork);
    }

    public List<Artwork> findRandomArtworks(int count) {
        List<Artwork> allArtworks = artworkRepository.findAll();
        //Java utility method: takes the entire list of artworks that is in memory and randomly reorders its elements
        Collections.shuffle(allArtworks);
        //Use Java stream to take the first count (8) elements from the shuffled list
        //First takes the List and converts it into Stream (a stream of elements that allows to perform operations on)
        //Second lets the first 8 artworks pass
        //Third gathers all the 8 elements and puts them in a new collection (LIST)
        return allArtworks.stream().limit(count).collect(Collectors.toList());
    }
}
