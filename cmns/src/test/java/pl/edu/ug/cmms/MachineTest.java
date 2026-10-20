package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MachineTest {

    @Test
    void validMachineCreation() {
        Machine m = new Machine("M-01", "Tokarka", Machine.MachineType.CNC_LATHE, "A", LocalDate.of(2020, 1, 1));
        assertEquals("M-01", m.id());
        assertEquals("Tokarka", m.name());
        assertEquals(Machine.MachineType.CNC_LATHE, m.type());
        assertEquals("A", m.hall());
        assertEquals(LocalDate.of(2020, 1, 1), m.installationDate());
    }

    @Test
    void nullIdThrows() {
        assertThrows(NullPointerException.class,
                () -> new Machine(null, "Tokarka", Machine.MachineType.CNC_LATHE, "A", LocalDate.of(2020, 1, 1)));
    }

    @Test
    void blankIdThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Machine("  ", "Tokarka", Machine.MachineType.CNC_LATHE, "A", LocalDate.of(2020, 1, 1)));
    }

    @Test
    void emptyIdThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Machine("", "Tokarka", Machine.MachineType.CNC_LATHE, "A", LocalDate.of(2020, 1, 1)));
    }

    @Test
    void nullNameThrows() {
        assertThrows(NullPointerException.class,
                () -> new Machine("M-01", null, Machine.MachineType.CNC_LATHE, "A", LocalDate.of(2020, 1, 1)));
    }

    @Test
    void nullTypeThrows() {
        assertThrows(NullPointerException.class,
                () -> new Machine("M-01", "Tokarka", null, "A", LocalDate.of(2020, 1, 1)));
    }

    @Test
    void nullHallThrows() {
        assertThrows(NullPointerException.class,
                () -> new Machine("M-01", "Tokarka", Machine.MachineType.CNC_LATHE, null, LocalDate.of(2020, 1, 1)));
    }

    @Test
    void blankHallThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Machine("M-01", "Tokarka", Machine.MachineType.CNC_LATHE, " ", LocalDate.of(2020, 1, 1)));
    }

    @Test
    void nullInstallationDateThrows() {
        assertThrows(NullPointerException.class,
                () -> new Machine("M-01", "Tokarka", Machine.MachineType.CNC_LATHE, "A", null));
    }
}
