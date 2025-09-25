package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.*;

@Entity
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotNull(message = "Year is required")
    private Integer year;

    //Telling the db to use Text data type for this column, not VARCHAR(255), to store longer strings
    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    //Artworks may have more than one artist <-> every artist can have more than one artwork
    @ManyToMany
    @JoinTable(
            name = "artwork_artist",
            //foreign key column that points back to the primary key of the Artwork entity
            joinColumns = @JoinColumn(name = "artwork_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> artists = new ArrayList<>();

    //Favourite artworks of a user
    @ManyToMany(mappedBy = "favoriteArts")
    private Set<User> users = new HashSet<>();

    @ManyToOne
    private Museum museum;

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Artist> getArtists() {
        return this.artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Museum getMuseum() {
        return this.museum;
    }

    public void setMuseum(Museum museum) {
        this.museum = museum;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<User> getUsers(){
        return this.users;
    }

    public void setUsers(Set<User> users){
        this.users = users;
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Artwork o = (Artwork) obj;
        return Objects.equals(id, o.id);
    }
}
