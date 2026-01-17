package pl.wypozyczalnia.demo.entity;


import jakarta.persistence.*;

@Entity
public class Rower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;
    private String status;
    private String lokalizacja;
    private double cenaZaGodzine;
    @Lob
    private String zdjecie;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLokalizacja() { return lokalizacja; }
    public void setLokalizacja(String lokalizacja) { this.lokalizacja = lokalizacja; }
    public double getCenaZaGodzine() { return cenaZaGodzine; }
    public void setCenaZaGodzine(double cenaZaGodzine) { this.cenaZaGodzine = cenaZaGodzine; }
    public String getZdjecie() { return zdjecie; }
    public void setZdjecie(String zdjecie) { this.zdjecie = zdjecie; }
}
