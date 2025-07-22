package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query("""
			    SELECT a FROM Artist a
			    WHERE
			        (:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')))
			        AND (:surname IS NULL OR LOWER(a.surname) LIKE LOWER(CONCAT('%', :surname, '%')))
			""")
    List<Artist> searchAuthors(@Param("name") String name, @Param("surname") String surname);

    boolean existsByNameAndSurname(String name, String surname);
}
