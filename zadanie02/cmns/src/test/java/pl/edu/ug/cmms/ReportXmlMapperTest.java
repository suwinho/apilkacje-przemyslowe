package pl.edu.ug.cmms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class ReportXmlMapperTest {

    private final ReportXmlMapper mapper = new ReportXmlMapper();

    @TempDir
    Path tempDir;

    @Test
    void reportWrittenToXmlIsReadBackAsEqualObject() throws Exception {
        Report report = new ReportCalculator(SampleData.machines(), SampleData.workOrders(), 3).calculate();
        Path file = tempDir.resolve("report.xml");

        mapper.write(report, file);
        Report read = mapper.read(file);

        assertNotSame(report, read);
        assertEquals(report, read);
    }

    @Test
    void customReportRoundTrip() throws Exception {
        Map<WorkOrder.Priority, Long> open = new LinkedHashMap<>();
        open.put(WorkOrder.Priority.CRITICAL, 2L);
        open.put(WorkOrder.Priority.HIGH, 0L);
        open.put(WorkOrder.Priority.NORMAL, 7L);
        open.put(WorkOrder.Priority.LOW, 1L);
        Map<String, Duration> mttr = new LinkedHashMap<>();
        mttr.put("A", Duration.ofHours(30).plusMinutes(5));
        mttr.put("Hala Ł", Duration.ZERO);
        Report report = new Report(1, 2, open,
                List.of(new App.MachineBreakdowns("X-1", 10), new App.MachineBreakdowns("X-2", 1)), mttr);
        Path file = tempDir.resolve("custom.xml");

        mapper.write(report, file);

        assertEquals(report, mapper.read(file));
    }

    @Test
    void xmlHasExpectedStructure() throws Exception {
        Report report = new ReportCalculator(SampleData.machines(), SampleData.workOrders(), 3).calculate();
        Path file = tempDir.resolve("report.xml");
        mapper.write(report, file);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file.toFile());
        Element root = doc.getDocumentElement();
        assertEquals("report", root.getTagName());
        assertEquals("9", text(root, "machineCount"));
        assertEquals("45", text(root, "workOrderCount"));

        NodeList machines = root.getElementsByTagName("machine");
        assertEquals(3, machines.getLength());
        Element first = (Element) machines.item(0);
        assertEquals("M-04", first.getAttribute("id"));
        assertEquals("5", first.getAttribute("breakdowns"));

        Element hallA = (Element) root.getElementsByTagName("hall").item(0);
        assertEquals("A", hallA.getAttribute("name"));
        assertEquals("13 h 51 min", hallA.getTextContent());

        String xml = Files.readString(file);
        assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<report>"));
        assertTrue(xml.contains("<machine id=\"M-04\" breakdowns=\"5\"/>"));
    }

    @Test
    void writeCreatesMissingDirectories() throws Exception {
        Report report = new ReportCalculator(SampleData.machines(), SampleData.workOrders(), 3).calculate();
        Path file = tempDir.resolve("out/2026/report.xml");

        mapper.write(report, file);

        assertTrue(Files.isRegularFile(file));
        assertEquals(report, mapper.read(file));
    }

    private static String text(Element parent, String name) {
        return parent.getElementsByTagName(name).item(0).getTextContent();
    }
}
