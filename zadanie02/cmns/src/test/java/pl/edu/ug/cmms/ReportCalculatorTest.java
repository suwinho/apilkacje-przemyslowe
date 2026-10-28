package pl.edu.ug.cmms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReportCalculatorTest {

    private static Report report(int top) {
        return new ReportCalculator(SampleData.machines(), SampleData.workOrders(), top).calculate();
    }

    @Test
    void countsMachinesAndOrders() {
        Report report = report(3);
        assertEquals(9, report.machineCount());
        assertEquals(45, report.workOrderCount());
    }

    @Test
    void countsOpenOrdersByPriority() {
        assertEquals(Map.of(
                WorkOrder.Priority.CRITICAL, 0L,
                WorkOrder.Priority.HIGH, 3L,
                WorkOrder.Priority.NORMAL, 1L,
                WorkOrder.Priority.LOW, 1L), report(3).openOrdersByPriority());
    }

    @Test
    void rankingTop3() {
        assertEquals(List.of(
                new App.MachineBreakdowns("M-04", 5),
                new App.MachineBreakdowns("M-08", 4),
                new App.MachineBreakdowns("M-05", 3)), report(3).topBreakdowns());
    }

    @Test
    void rankingTop5() {
        assertEquals(List.of(
                new App.MachineBreakdowns("M-04", 5),
                new App.MachineBreakdowns("M-08", 4),
                new App.MachineBreakdowns("M-05", 3),
                new App.MachineBreakdowns("M-06", 3),
                new App.MachineBreakdowns("M-02", 2)), report(5).topBreakdowns());
    }

    @Test
    void mttrOfHallA() {
        assertEquals(Duration.ofHours(13).plusMinutes(51), report(3).mttrByHall().get("A"));
        assertEquals(List.of("A", "B", "C"), List.copyOf(report(3).mttrByHall().keySet()));
    }

    @Test
    void negativeTopThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReportCalculator(SampleData.machines(), SampleData.workOrders(), -1));
    }
}
