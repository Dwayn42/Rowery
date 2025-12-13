package pl.wypozyczalnia.demo.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
public class Rezerwacja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataOd;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataDo;

    @ManyToOne
    @JoinColumn(name = "uzytkownik_id")
    private Uzytkownik uzytkownik;

    @ManyToOne
    @JoinColumn(name = "rower_id")
    private Rower rower;

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDataOd() { return dataOd; }
    public void setDataOd(LocalDate dataOd) { this.dataOd = dataOd; }
    public LocalDate getDataDo() { return dataDo; }
    public void setDataDo(LocalDate dataDo) { this.dataDo = dataDo; }
    public Uzytkownik getUzytkownik() { return uzytkownik; }
    public void setUzytkownik(Uzytkownik uzytkownik) { this.uzytkownik = uzytkownik; }
    public Rower getRower() { return rower; }
    public void setRower(Rower rower) { this.rower = rower; }
}