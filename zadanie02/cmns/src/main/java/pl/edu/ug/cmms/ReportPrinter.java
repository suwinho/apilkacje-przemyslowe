package pl.edu.ug.cmms;

import java.io.PrintStream;

public class ReportPrinter {

    private final PrintStream out;

    public ReportPrinter(PrintStream out) {
        this.out = out;
    }

    public void print(Report report) {
        out.println("Raport utrzymania ruchu");
        out.println("  Liczba maszyn: " + report.machineCount());
        out.println("  Liczba zleceń: " + report.workOrderCount());
        out.println();

        out.println("Niezakończone zlecenia według priorytetu:");
        report.openOrdersByPriority().forEach((p, count) -> out.println("  " + p + ": " + count));
        out.println();

        out.println("Maszyny z największą liczbą awarii (top " + report.topBreakdowns().size() + "):");
        report.topBreakdowns().forEach(m -> out.println("  " + m.machineId() + ": " + m.count() + " awarii"));
        out.println();

        out.println("MTTR według hali:");
        report.mttrByHall().forEach((hall, mttr) -> out.println("  Hala " + hall + ": " + DurationFormat.format(mttr)));
        out.flush();
    }
}
