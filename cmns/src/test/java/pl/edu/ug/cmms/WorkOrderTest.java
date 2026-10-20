package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderTest {

    private static final LocalDateTime REPORTED = LocalDateTime.of(2026, 5, 10, 8, 0);
    private static final LocalDateTime CLOSED = LocalDateTime.of(2026, 5, 10, 12, 0);

    @Test
    void validDoneOrder() {
        WorkOrder wo = new WorkOrder("WO-01", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                WorkOrder.WorkOrderStatus.DONE, REPORTED, CLOSED);
        assertEquals("WO-01", wo.id());
        assertEquals(WorkOrder.WorkOrderStatus.DONE, wo.status());
        assertEquals(CLOSED, wo.closedAt());
    }

    @Test
    void validOpenOrderWithoutClosedAt() {
        WorkOrder wo = new WorkOrder("WO-02", "M-01",
                WorkOrder.WorkOrderType.PREVENTIVE, WorkOrder.Priority.LOW,
                WorkOrder.WorkOrderStatus.OPEN, REPORTED, null);
        assertNull(wo.closedAt());
    }

    @Test
    void validCancelledOrderWithoutClosedAt() {
        WorkOrder wo = new WorkOrder("WO-03", "M-01",
                WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.CRITICAL,
                WorkOrder.WorkOrderStatus.CANCELLED, REPORTED, null);
        assertEquals(WorkOrder.WorkOrderStatus.CANCELLED, wo.status());
    }

    @Test
    void nullIdThrows() {
        assertThrows(NullPointerException.class,
                () -> new WorkOrder(null, "M-01",
                        WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.DONE, REPORTED, CLOSED));
    }

    @Test
    void blankIdThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkOrder("", "M-01",
                        WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.DONE, REPORTED, CLOSED));
    }

    @Test
    void nullMachineIdThrows() {
        assertThrows(NullPointerException.class,
                () -> new WorkOrder("WO-01", null,
                        WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.DONE, REPORTED, CLOSED));
    }

    @Test
    void blankMachineIdThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkOrder("WO-01", " ",
                        WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.DONE, REPORTED, CLOSED));
    }

    @Test
    void nullTypeThrows() {
        assertThrows(NullPointerException.class,
                () -> new WorkOrder("WO-01", "M-01",
                        null, WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.DONE, REPORTED, CLOSED));
    }

    @Test
    void nullPriorityThrows() {
        assertThrows(NullPointerException.class,
                () -> new WorkOrder("WO-01", "M-01",
                        WorkOrder.WorkOrderType.BREAKDOWN, null,
                        WorkOrder.WorkOrderStatus.DONE, REPORTED, CLOSED));
    }

    @Test
    void nullStatusThrows() {
        assertThrows(NullPointerException.class,
                () -> new WorkOrder("WO-01", "M-01",
                        WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                        null, REPORTED, CLOSED));
    }

    @Test
    void nullReportedAtThrows() {
        assertThrows(NullPointerException.class,
                () -> new WorkOrder("WO-01", "M-01",
                        WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.DONE, null, CLOSED));
    }

    @Test
    void doneWithoutClosedAtThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorkOrder("WO-01", "M-01",
                        WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.DONE, REPORTED, null));
    }

    @Test
    void closedBeforeReportedThrows() {
        LocalDateTime earlier = REPORTED.minusHours(1);
        assertThrows(IllegalArgumentException.class,
                () -> new WorkOrder("WO-01", "M-01",
                        WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.DONE, REPORTED, earlier));
    }
}
