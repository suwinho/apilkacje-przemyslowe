package pl.edu.ug.cmms;

import java.time.LocalDateTime;
import java.util.Objects;

public record WorkOrder(
        String id,
        String machineId,
        WorkOrderType type,
        Priority priority,
        WorkOrderStatus status,
        LocalDateTime reportedAt,
        LocalDateTime closedAt
) {
    public enum WorkOrderType {
        PREVENTIVE,
        INSPECTION,
        BREAKDOWN
    }

    public enum Priority {
        CRITICAL,
        HIGH,
        NORMAL,
        LOW
    }

    public enum WorkOrderStatus {
        OPEN,
        IN_PROGRESS,
        DONE,
        CANCELLED
    }

    public WorkOrder {
        Objects.requireNonNull(id, "ID cannot be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        Objects.requireNonNull(machineId, "Machine ID cannot be null");
        if (machineId.isBlank()) {
            throw new IllegalArgumentException("Machine ID cannot be empty");
        }
        Objects.requireNonNull(type, "Type cannot be null");
        Objects.requireNonNull(priority, "Priority cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Objects.requireNonNull(reportedAt, "Reported date cannot be null");

        if (status == WorkOrderStatus.DONE && closedAt == null) {
            throw new IllegalArgumentException("DONE order must have a closedAt timestamp");
        }

        if (closedAt != null && closedAt.isBefore(reportedAt)) {
            throw new IllegalArgumentException("closedAt cannot be earlier than reportedAt");
        }
    }
}