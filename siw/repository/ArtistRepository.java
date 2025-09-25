package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

	//First: Select all artist entities and refer to them as "a" (for simplicity)
	//Second: Checks if the value passed for "name" parameter is null. If it is that or condition is "true" and doesn't apply any filter
	//Third: If is provided: "lower" makes the search case-insensitive and "%" wraps the search term and will find any artist whose name contains the search string
    @Query("""
			    SELECT a FROM Artist a
			    WHERE
			        (:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')))
			        AND (:surname IS NULL OR LOWER(a.surname) LIKE LOWER(CONCAT('%', :surname, '%')))
			""")
    List<Artist> searchArtist(@Param("name") String name, @Param("surname") String surname);

    boolean existsByNameAndSurname(String name, String surname);
}
