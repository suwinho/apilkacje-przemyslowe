package pl.edu.ug.cmms;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record Report(
        int machineCount,
        int workOrderCount,
        Map<WorkOrder.Priority, Long> openOrdersByPriority,
        List<App.MachineBreakdowns> topBreakdowns,
        Map<String, Duration> mttrByHall
) {
    public Report {
        openOrdersByPriority = Collections.unmodifiableMap(new LinkedHashMap<>(openOrdersByPriority));
        topBreakdowns = List.copyOf(topBreakdowns);
        mttrByHall = Collections.unmodifiableMap(new LinkedHashMap<>(mttrByHall));
    }
}
