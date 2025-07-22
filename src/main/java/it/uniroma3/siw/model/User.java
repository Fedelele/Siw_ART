package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Name required")
    private String name;
    @NotBlank(message = "Surname required")
    private String surname;
    @NotBlank(message = "Email required")
    @Column(unique = true)
    private String email;

    @ManyToMany
    @JoinTable(
            name = "user_favorite_art",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "artwork_id")
    )
    private Set<Artwork> favoriteArts = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String firstName) {
        this.name = firstName;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String lastName) {
        this.surname = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Artwork> getFavoriteArts(){
        return this.favoriteArts;
    }

    public void setFavoriteArts(Set<Artwork> favoriteArts){
        this.favoriteArts = favoriteArts;
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
        User o = (User) obj;
        return Objects.equals(id, o.id);
    }
}
