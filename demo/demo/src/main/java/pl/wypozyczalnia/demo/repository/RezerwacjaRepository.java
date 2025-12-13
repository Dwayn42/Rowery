package pl.wypozyczalnia.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wypozyczalnia.demo.entity.Rezerwacja;

public interface RezerwacjaRepository extends JpaRepository<Rezerwacja, Long> {
}