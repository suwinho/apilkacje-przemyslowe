package pl.edu.ug.cmms;

import java.time.LocalDate;
import java.util.Objects;

public record Machine(String id, String name, MachineType type, String hall, LocalDate installationDate) {
    public enum MachineType {
        CNC_LATHE,
        CONVEYOR,
        MILLING_MACHINE,
        WELDING_ROBOT,
        INJECTION_MOLDER,
        HYDRAULIC_PRESS,
        COMPRESSOR
    }
    public Machine {
        Objects.requireNonNull(id, "ID cannot be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(type, "MachineType cannot be null");
        Objects.requireNonNull(hall, "Hall cannot be null");
        if (hall.isBlank()) {
            throw new IllegalArgumentException("Hall cannot be empty");
        }
        Objects.requireNonNull(installationDate, "Installation date cannot be null");
    }
}