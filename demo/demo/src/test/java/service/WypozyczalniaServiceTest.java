package service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wypozyczalnia.demo.entity.Rezerwacja;
import pl.wypozyczalnia.demo.entity.Rower;
import pl.wypozyczalnia.demo.entity.Uzytkownik;
import pl.wypozyczalnia.demo.repository.RezerwacjaRepository;
import pl.wypozyczalnia.demo.repository.RowerRepository;
import pl.wypozyczalnia.demo.repository.UzytkownikRepository;
import pl.wypozyczalnia.demo.service.WypozyczalniaService;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WypozyczalniaServiceTest {

    @Mock
    private RowerRepository rowerRepository;
    @Mock
    private RezerwacjaRepository rezerwacjaRepository;
    @Mock
    private UzytkownikRepository uzytkownikRepository;

    @InjectMocks
    private WypozyczalniaService service;

    @Test
    void testUtworzRezerwacje() {
        Long rowerId = 1L;
        Rower rower = new Rower();
        rower.setId(rowerId);
        rower.setStatus("DOSTEPNY");

        Uzytkownik klient = new Uzytkownik();
        klient.setImie("Jan Testowy");

        when(rowerRepository.findById(rowerId)).thenReturn(Optional.of(rower));

        service.utworzRezerwacje(rowerId, klient, LocalDate.now(), LocalDate.now().plusDays(1));


        assertEquals("ZAREZERWOWANY", rower.getStatus());

        verify(uzytkownikRepository, times(1)).save(klient);
        verify(rowerRepository, times(1)).save(rower); // Zapis zmienionego statusu
        verify(rezerwacjaRepository, times(1)).save(any(Rezerwacja.class));
    }
}