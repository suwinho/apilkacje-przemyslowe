package pl.edu.ug.cmms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(args = {"--cmms.report.top=5", "--cmms.report.file=build/test-output/out/2026/report.xml"})
class AppContextTest {

    private static final Path OUTPUT_DIR = Path.of("build/test-output");
    private static final Path REPORT_FILE = OUTPUT_DIR.resolve("out/2026/report.xml");

    @Autowired
    ApplicationContext context;

    @BeforeAll
    static void cleanOutput() throws IOException {
        if (Files.exists(OUTPUT_DIR)) {
            try (Stream<Path> paths = Files.walk(OUTPUT_DIR)) {
                for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(p);
                }
            }
        }
    }

    @Test
    void beansAreDefinedInXml() {
        assertTrue(context.containsBean("reportCalculator"));
        assertTrue(context.containsBean("reportPrinter"));
        assertTrue(context.containsBean("reportXmlMapper"));
        assertTrue(context.containsBean("reportRunner"));
    }

    @Test
    void commandLineOverridesConfigAndRunnerWritesXml() throws IOException {
        assertTrue(Files.isRegularFile(REPORT_FILE));
        Report written = context.getBean(ReportXmlMapper.class).read(REPORT_FILE);
        assertEquals(5, written.topBreakdowns().size());
        assertEquals(context.getBean(ReportCalculator.class).calculate(), written);
    }
}
