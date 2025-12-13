package pl.wypozyczalnia.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wypozyczalnia.demo.entity.Uzytkownik;

public interface UzytkownikRepository extends JpaRepository<Uzytkownik, Long> {
}