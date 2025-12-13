package pl.wypozyczalnia.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wypozyczalnia.demo.entity.Rower;
import java.util.List;

public interface RowerRepository extends JpaRepository<Rower, Long> {
    List<Rower> findByStatus(String status);
}