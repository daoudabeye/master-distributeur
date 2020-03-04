package ml.iks.md.repositories;

import ml.iks.md.models.GrPlage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GrPlageRepository extends JpaRepository<GrPlage, Long> {
	
	GrPlage findTopByNumero(String numero);
	
//	@Query("{'from': {$gte: ?0, $lte:?1 }}")
//	GrPlage findByNumeroBetween(Integer numero);
	
	@Query(value = "SELECT p from GrPlage p where ?1 between  p.borneInf and p.bornSup")
	List<GrPlage> find(Integer num);

}
