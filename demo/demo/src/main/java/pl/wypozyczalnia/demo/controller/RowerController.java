package pl.wypozyczalnia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.wypozyczalnia.demo.entity.Rezerwacja;
import pl.wypozyczalnia.demo.entity.Rower;
import pl.wypozyczalnia.demo.entity.Uzytkownik;
import pl.wypozyczalnia.demo.repository.RowerRepository;
import pl.wypozyczalnia.demo.service.WypozyczalniaService;


import java.util.List;

@Controller
public class RowerController {

    @Autowired
    private RowerRepository rowerRepository;
    @Autowired
    private WypozyczalniaService service;

    //dodanie rowerów na start
    @EventListener(ApplicationReadyEvent.class)
    public void dodajDaneStartowe() {
        if (rowerRepository.count() == 0) {
            Rower r1 = new Rower(); r1.setModel("Kross Hexagon"); r1.setLokalizacja("Centrum"); r1.setStatus("DOSTEPNY"); r1.setCenaZaGodzine(15.0);
            Rower r2 = new Rower(); r2.setModel("City Bike Basic"); r2.setLokalizacja("Stare Miasto"); r2.setStatus("DOSTEPNY"); r2.setCenaZaGodzine(10.0);
            Rower r3 = new Rower(); r3.setModel("Mountain Pro"); r3.setLokalizacja("Las Kabacki"); r3.setStatus("DOSTEPNY"); r3.setCenaZaGodzine(25.0);
            rowerRepository.saveAll(List.of(r1, r2, r3));
        }
    }

    // F2: Przegląd listy dostępnych rowerów
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("listaRowerow", rowerRepository.findByStatus("DOSTEPNY"));
        return "index";
    }

    // F4: Formularz wypożyczenia roweru
    @GetMapping("/wypozycz/{id}")
    public String formularz(@PathVariable Long id, Model model) {
        Rower rower = rowerRepository.findById(id).get();
        model.addAttribute("rower", rower);
        model.addAttribute("rezerwacja", new Rezerwacja());
        model.addAttribute("klient", new Uzytkownik());
        return "formularz";
    }

    // F3: Zatwierdzenie wypożyczenia
    @PostMapping("/potwierdz")
    public String potwierdz(Uzytkownik klient, Rezerwacja rezerwacja, Long idRoweru) {
        service.utworzRezerwacje(idRoweru, klient, rezerwacja.getDataOd(), rezerwacja.getDataDo());
        return "sukces";
    }
}