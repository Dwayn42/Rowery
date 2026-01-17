package controller;

import pl.wypozyczalnia.demo.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.wypozyczalnia.demo.repository.RowerRepository;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@Transactional
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RowerRepository rowerRepository;

    @Test
    void wejscieDoPaneluBezLogowania() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void poprawneLogowanie() throws Exception {
        mockMvc.perform(post("/login")
                        .param("haslo", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void dodanieRoweru() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("zalogowany", true);

        mockMvc.perform(multipart("/admin/zapisz")
                        .file("plik", new byte[0])
                        .session(session)
                        .param("model", "Testowy Rower")
                        .param("lokalizacja", "Laboratorium")
                        .param("cenaZaGodzine", "123.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        boolean istnieje = rowerRepository.findAll().stream()
                .anyMatch(r -> r.getModel().equals("Testowy Rower"));

        if (!istnieje) {
            throw new AssertionError("Rower nie został zapisany w bazie");
        }
    }

    @Test
    void stronaGlowna() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dostępne Rowery")));
    }
}