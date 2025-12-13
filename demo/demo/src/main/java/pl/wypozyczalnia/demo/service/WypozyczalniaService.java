package pl.wypozyczalnia.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.wypozyczalnia.demo.entity.Rezerwacja;
import pl.wypozyczalnia.demo.entity.Rower;
import pl.wypozyczalnia.demo.entity.Uzytkownik;
import pl.wypozyczalnia.demo.repository.RezerwacjaRepository;
import pl.wypozyczalnia.demo.repository.RowerRepository;
import pl.wypozyczalnia.demo.repository.UzytkownikRepository;


import java.time.LocalDate;

@Service
public class WypozyczalniaService {

    @Autowired
    private RowerRepository rowerRepository;
    @Autowired
    private RezerwacjaRepository rezerwacjaRepository;
    @Autowired
    private UzytkownikRepository uzytkownikRepository;

    // F3 & F4: Logika tworzenia rezerwacji
    public void utworzRezerwacje(Long idRoweru, Uzytkownik klient, LocalDate od, LocalDate doKiedy) {
        // 1. Zapisujemy klienta w bazie
        uzytkownikRepository.save(klient);

        // 2. Pobieramy rower i zmieniamy mu status
        Rower rower = rowerRepository.findById(idRoweru).orElseThrow();
        rower.setStatus("ZAREZERWOWANY");
        rowerRepository.save(rower);

        // 3. Tworzymy rezerwację
        Rezerwacja rezerwacja = new Rezerwacja();
        rezerwacja.setRower(rower);
        rezerwacja.setUzytkownik(klient);
        rezerwacja.setDataOd(od);
        rezerwacja.setDataDo(doKiedy);

        rezerwacjaRepository.save(rezerwacja);
    }
}