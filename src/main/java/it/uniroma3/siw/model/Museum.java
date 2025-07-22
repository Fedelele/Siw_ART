package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

@Entity
public class Museum {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Location is required")
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "museum", cascade =  CascadeType.ALL, orphanRemoval = true)
    private List<Artwork> artworks;

//    @Lob
    private String imageUrl;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Artwork> getArtworks() {
        return this.artworks;
    }

    public void setArtworks(List<Artwork> artworks) {
        this.artworks = artworks;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    @Override
    public int hashCode(){
        return Objects.hash(id, name, address);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Museum o = (Museum) obj;
        return Objects.equals(id, o.id) && Objects.equals(name, o.name) &&
                Objects.equals(address, o.address);
    }
}
