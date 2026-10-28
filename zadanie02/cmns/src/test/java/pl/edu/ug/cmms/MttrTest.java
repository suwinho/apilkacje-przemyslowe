package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MttrTest {

    @Test
    void mttrCalculatedCorrectly() {
        // Breakdown DONE: 2h
        WorkOrder wo1 = new WorkOrder("WO-T1", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                WorkOrder.WorkOrderStatus.DONE,
                LocalDateTime.of(2026, 6, 1, 8, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0));

        // Breakdown DONE: 5h 30min
        WorkOrder wo2 = new WorkOrder("WO-T2", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.NORMAL,
                WorkOrder.WorkOrderStatus.DONE,
                LocalDateTime.of(2026, 6, 2, 9, 0),
                LocalDateTime.of(2026, 6, 2, 14, 30));

        // Breakdown DONE: 1h 30min
        WorkOrder wo3 = new WorkOrder("WO-T3", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.CRITICAL,
                WorkOrder.WorkOrderStatus.DONE,
                LocalDateTime.of(2026, 6, 3, 12, 0),
                LocalDateTime.of(2026, 6, 3, 13, 30));

        // MTTR = (2h + 5h30m + 1h30m) / 3 = 9h / 3 = 3h 0min
        Duration mttr = App.calculateMttr(List.of(wo1, wo2, wo3));
        assertEquals(3, mttr.toHours());
        assertEquals(0, mttr.toMinutesPart());
    }

    @Test
    void mttrIgnoresNonBreakdownOrders() {
        WorkOrder preventive = new WorkOrder("WO-P1", "M-01",
                WorkOrder.WorkOrderType.PREVENTIVE, WorkOrder.Priority.LOW,
                WorkOrder.WorkOrderStatus.DONE,
                LocalDateTime.of(2026, 6, 1, 8, 0),
                LocalDateTime.of(2026, 6, 1, 20, 0));

        WorkOrder inspection = new WorkOrder("WO-I1", "M-01",
                WorkOrder.WorkOrderType.INSPECTION, WorkOrder.Priority.NORMAL,
                WorkOrder.WorkOrderStatus.DONE,
                LocalDateTime.of(2026, 6, 2, 8, 0),
                LocalDateTime.of(2026, 6, 2, 18, 0));

        // Breakdown DONE: 4h
        WorkOrder breakdown = new WorkOrder("WO-B1", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                WorkOrder.WorkOrderStatus.DONE,
                LocalDateTime.of(2026, 6, 3, 10, 0),
                LocalDateTime.of(2026, 6, 3, 14, 0));

        // MTTR = 4h / 1 = 4h 0min (only breakdown counted)
        Duration mttr = App.calculateMttr(List.of(preventive, inspection, breakdown));
        assertEquals(4, mttr.toHours());
        assertEquals(0, mttr.toMinutesPart());
    }

    @Test
    void mttrIgnoresCancelledBreakdowns() {
        WorkOrder cancelled = new WorkOrder("WO-C1", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.CRITICAL,
                WorkOrder.WorkOrderStatus.CANCELLED,
                LocalDateTime.of(2026, 6, 1, 8, 0), null);

        // Breakdown DONE: 6h 20min
        WorkOrder done = new WorkOrder("WO-D1", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                WorkOrder.WorkOrderStatus.DONE,
                LocalDateTime.of(2026, 6, 2, 9, 0),
                LocalDateTime.of(2026, 6, 2, 15, 20));

        // MTTR = 6h20min / 1 = 6h 20min (cancelled not counted)
        Duration mttr = App.calculateMttr(List.of(cancelled, done));
        assertEquals(6, mttr.toHours());
        assertEquals(20, mttr.toMinutesPart());
    }

    @Test
    void mttrWithNoCompletedBreakdownsReturnsZero() {
        WorkOrder open = new WorkOrder("WO-O1", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                WorkOrder.WorkOrderStatus.OPEN,
                LocalDateTime.of(2026, 6, 1, 8, 0), null);

        Duration mttr = App.calculateMttr(List.of(open));
        assertEquals(Duration.ZERO, mttr);
    }

    @Test
    void mttrTruncatesSeconds() {
        // Breakdown DONE: 2h 15min 40s
        WorkOrder wo1 = new WorkOrder("WO-T1", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                WorkOrder.WorkOrderStatus.DONE,
                LocalDateTime.of(2026, 6, 1, 8, 0, 0),
                LocalDateTime.of(2026, 6, 1, 10, 15, 40));

        // Breakdown DONE: 3h 44min 20s
        WorkOrder wo2 = new WorkOrder("WO-T2", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.NORMAL,
                WorkOrder.WorkOrderStatus.DONE,
                LocalDateTime.of(2026, 6, 2, 9, 0, 0),
                LocalDateTime.of(2026, 6, 2, 12, 44, 20));

        // Total = 2h15m40s + 3h44m20s = 6h 0min 0s
        // Avg = 6h / 2 = 3h 0min 0s
        Duration mttr = App.calculateMttr(List.of(wo1, wo2));
        assertEquals(3, mttr.toHours());
        assertEquals(0, mttr.toMinutesPart());
    }
}
