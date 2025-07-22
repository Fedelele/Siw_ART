package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Museum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MuseumRepository extends JpaRepository<Museum, Long> {
    @Query("""
			    SELECT m FROM Museum m
			    WHERE
			        (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')))
			""")
    List<Museum> searchMuseums(@Param("name") String name);

	boolean existsByName(String name);
}
