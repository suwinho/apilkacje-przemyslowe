package pl.edu.ug.cmms;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class App {

    record MachineBreakdowns(String machineId, long count) {}

    public static Duration calculateMttr(List<WorkOrder> orders) {
        List<WorkOrder> completedBreakdowns = orders.stream()
                .filter(o -> o.type() == WorkOrder.WorkOrderType.BREAKDOWN)
                .filter(o -> o.status() == WorkOrder.WorkOrderStatus.DONE)
                .filter(o -> o.closedAt() != null)
                .toList();

        if (completedBreakdowns.isEmpty()) {
            return Duration.ZERO;
        }

        long totalSeconds = completedBreakdowns.stream()
                .mapToLong(o -> Duration.between(o.reportedAt(), o.closedAt()).toSeconds())
                .sum();

        long avgSeconds = totalSeconds / completedBreakdowns.size();
        return Duration.ofSeconds(avgSeconds);
    }

    public static void main(String[] args) {
        List<Machine> machines = SampleData.machines();
        List<WorkOrder> workOrders = SampleData.workOrders();

        Map<WorkOrder.Priority, Long> activeOrdersByPriority = workOrders.stream()
                .filter(order -> order.status() == WorkOrder.WorkOrderStatus.OPEN
                        || order.status() == WorkOrder.WorkOrderStatus.IN_PROGRESS)
                .collect(Collectors.groupingBy(
                        WorkOrder::priority,
                        TreeMap::new,
                        Collectors.counting()));

        for (WorkOrder.Priority p : WorkOrder.Priority.values()) {
            long count = activeOrdersByPriority.getOrDefault(p, 0L);
            System.out.println("  " + p + ": " + count);
        }

        System.out.println();
        Map<String, Long> breakdownsByMachine = workOrders.stream()
                .filter(order -> order.type() == WorkOrder.WorkOrderType.BREAKDOWN)
                .filter(order -> order.status() != WorkOrder.WorkOrderStatus.CANCELLED)
                .collect(Collectors.groupingBy(
                        WorkOrder::machineId,
                        TreeMap::new,
                        Collectors.counting()));

        List<MachineBreakdowns> topBrokenMachines = breakdownsByMachine.entrySet().stream()
                .map(e -> new MachineBreakdowns(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(MachineBreakdowns::count).reversed()
                        .thenComparing(MachineBreakdowns::machineId))
                .limit(3)
                .toList();

        topBrokenMachines.forEach(m -> System.out.println("  " + m.machineId() + ": " + m.count() + " awarii"));

        System.out.println();
        Map<String, String> machineToHall = machines.stream()
                .collect(Collectors.toMap(Machine::id, Machine::hall));

        Map<String, List<WorkOrder>> ordersByHall = workOrders.stream()
                .filter(o -> machineToHall.containsKey(o.machineId()))
                .collect(Collectors.groupingBy(o -> machineToHall.get(o.machineId()), TreeMap::new, Collectors.toList()));

        for (Map.Entry<String, List<WorkOrder>> entry : ordersByHall.entrySet()) {
            Duration mttr = calculateMttr(entry.getValue());
            long hours = mttr.toHours();
            long minutes = mttr.toMinutesPart();
            System.out.println("  Hala " + entry.getKey() + ": " + hours + " h " + minutes + " min");
        }
    }
}