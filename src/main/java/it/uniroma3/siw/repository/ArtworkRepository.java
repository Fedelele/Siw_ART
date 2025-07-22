package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    @Query("""
		    SELECT DISTINCT a FROM Artwork a
		    WHERE
		        (:title IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')))
		        AND (:year IS NULL OR a.year = :year)
		""")
    List<Artwork> searchArtworks(
            @Param("title") String title,
            @Param("year") Integer year
    );

    List<Artwork> findByYear(int year);

	boolean existsByTitleAndYear(String title, Integer year);

	Optional<Artwork> findByTitleAndYear(String title, Integer year);

	//Maybe not needed, we already have searchArtworks
	List<Artwork> findByTitleContainingIgnoreCase(String keyword);




}
