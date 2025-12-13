package pl.wypozyczalnia.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.wypozyczalnia.demo.entity.Rower;
import pl.wypozyczalnia.demo.repository.RowerRepository;

import java.io.IOException;
import java.util.Base64;

@Controller
public class AdminController {

    @Autowired
    private RowerRepository rowerRepository;

    // --- LOGOWANIE ---

    @GetMapping("/login")
    public String formularzLogowania() {
        return "login";
    }

    @PostMapping("/login")
    public String zaloguj(@RequestParam String haslo, HttpSession session, Model model) {
        if ("admin123".equals(haslo)) {
            session.setAttribute("zalogowany", true);
            return "redirect:/admin";
        } else {
            model.addAttribute("error", true);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String wyloguj(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- PANEL ADMINA (DODAWANIE I EDYCJA) ---

    @GetMapping("/admin")
    public String panelAdmina(Model model, HttpSession session,
                              @RequestParam(required = false) Long edytujId) {

        // 1. Zabezpieczenie
        if (session.getAttribute("zalogowany") == null) {
            return "redirect:/login";
        }

        // 2. Pobierz listę do tabeli
        model.addAttribute("wszystkieRowery", rowerRepository.findAll());

        // 3. Logika formularza (Czy edytujemy, czy dodajemy?)
        if (edytujId != null) {
            // TRYB EDYCJI: Pobieramy rower z bazy
            Rower rowerDoEdycji = rowerRepository.findById(edytujId).orElse(new Rower());
            model.addAttribute("rowerForm", rowerDoEdycji);
            model.addAttribute("trybEdycji", true); // Flaga do HTML
        } else {
            // TRYB DODAWANIA: Tworzymy czysty obiekt
            Rower nowyRower = new Rower();
            nowyRower.setStatus("DOSTEPNY"); // Domyślny status
            model.addAttribute("rowerForm", nowyRower);
            model.addAttribute("trybEdycji", false);
        }

        return "admin";
    }

    // Jedna metoda do zapisu (obsługuje zarówno INSERT jak i UPDATE)
    @PostMapping("/admin/zapisz")
    public String zapiszRower(Rower rowerForm,
                              @RequestParam("plik") MultipartFile plik,
                              HttpSession session) throws IOException {

        if (session.getAttribute("zalogowany") == null) {
            return "redirect:/login";
        }

        // --- LOGIKA ZDJĘCIA PRZY EDYCJI ---
        // Jeśli edytujemy (jest ID) i NIE wgrano nowego pliku -> zachowaj stare zdjęcie
        if (rowerForm.getId() != null && plik.isEmpty()) {
            Rower staryRower = rowerRepository.findById(rowerForm.getId()).orElse(null);
            if (staryRower != null) {
                rowerForm.setZdjecie(staryRower.getZdjecie());
            }
        }
        // Jeśli wgrano nowy plik -> zamień na Base64
        else if (!plik.isEmpty()) {
            String encodedString = Base64.getEncoder().encodeToString(plik.getBytes());
            rowerForm.setZdjecie(encodedString);
        }

        // Zabezpieczenie statusu (żeby nie był null przy nowym rowerze)
        if (rowerForm.getStatus() == null || rowerForm.getStatus().isEmpty()) {
            rowerForm.setStatus("DOSTEPNY");
        }

        // save() w Spring Data działa sprytnie: jak jest ID to aktualizuje, jak nie ma to dodaje.
        rowerRepository.save(rowerForm);

        return "redirect:/admin";
    }

    // Opcjonalne usuwanie
    @GetMapping("/admin/usun/{id}")
    public String usunRower(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("zalogowany") != null) {
            rowerRepository.deleteById(id);
        }
        return "redirect:/admin";
    }
}