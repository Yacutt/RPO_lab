package bmstu.iu3.backend.repositories;
import bmstu.iu3.backend.models.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository  extends JpaRepository<Country, Long>
{

}