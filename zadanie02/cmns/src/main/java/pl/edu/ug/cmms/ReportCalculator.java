package pl.edu.ug.cmms;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ReportCalculator {

    private final List<Machine> machines;
    private final List<WorkOrder> workOrders;
    private final int top;

    public ReportCalculator(List<Machine> machines, List<WorkOrder> workOrders, int top) {
        if (top < 0) {
            throw new IllegalArgumentException("cmms.report.top cannot be negative");
        }
        this.machines = List.copyOf(machines);
        this.workOrders = List.copyOf(workOrders);
        this.top = top;
    }

    public Report calculate() {
        return new Report(machines.size(), workOrders.size(), openOrdersByPriority(), topBreakdowns(), mttrByHall());
    }

    private Map<WorkOrder.Priority, Long> openOrdersByPriority() {
        Map<WorkOrder.Priority, Long> counts = workOrders.stream()
                .filter(o -> o.status() == WorkOrder.WorkOrderStatus.OPEN
                        || o.status() == WorkOrder.WorkOrderStatus.IN_PROGRESS)
                .collect(Collectors.groupingBy(WorkOrder::priority, Collectors.counting()));

        Map<WorkOrder.Priority, Long> result = new LinkedHashMap<>();
        for (WorkOrder.Priority p : WorkOrder.Priority.values()) {
            result.put(p, counts.getOrDefault(p, 0L));
        }
        return result;
    }

    private List<App.MachineBreakdowns> topBreakdowns() {
        return workOrders.stream()
                .filter(o -> o.type() == WorkOrder.WorkOrderType.BREAKDOWN)
                .filter(o -> o.status() != WorkOrder.WorkOrderStatus.CANCELLED)
                .collect(Collectors.groupingBy(WorkOrder::machineId, TreeMap::new, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new App.MachineBreakdowns(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(App.MachineBreakdowns::count).reversed()
                        .thenComparing(App.MachineBreakdowns::machineId))
                .limit(top)
                .toList();
    }

    private Map<String, Duration> mttrByHall() {
        Map<String, String> machineToHall = machines.stream()
                .collect(Collectors.toMap(Machine::id, Machine::hall));

        Map<String, List<WorkOrder>> ordersByHall = workOrders.stream()
                .filter(o -> machineToHall.containsKey(o.machineId()))
                .collect(Collectors.groupingBy(o -> machineToHall.get(o.machineId()), TreeMap::new, Collectors.toList()));

        Map<String, Duration> result = new LinkedHashMap<>();
        ordersByHall.forEach((hall, orders) -> result.put(hall, App.calculateMttr(orders).truncatedTo(ChronoUnit.MINUTES)));
        return result;
    }
}
