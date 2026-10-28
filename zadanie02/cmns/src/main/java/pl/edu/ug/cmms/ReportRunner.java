package pl.edu.ug.cmms;

import java.io.IOException;
import java.nio.file.Path;
import org.springframework.boot.CommandLineRunner;

public class ReportRunner implements CommandLineRunner {

    private final ReportCalculator calculator;
    private final ReportPrinter printer;
    private final ReportXmlMapper xmlMapper;
    private final Path file;

    public ReportRunner(ReportCalculator calculator, ReportPrinter printer, ReportXmlMapper xmlMapper, String file) {
        this.calculator = calculator;
        this.printer = printer;
        this.xmlMapper = xmlMapper;
        this.file = Path.of(file);
    }

    @Override
    public void run(String... args) throws IOException {
        Report report = calculator.calculate();
        printer.print(report);
        xmlMapper.write(report, file);
    }
}
