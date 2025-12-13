package pl.wypozyczalnia.demo.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Uzytkownik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imie;
    private String email;

    @OneToMany(mappedBy = "uzytkownik")
    private List<Rezerwacja> rezerwacje;

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}