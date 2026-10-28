package pl.edu.ug.cmms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReportXmlMapper {

    private static final String INDENT = "    ";

    public void write(Report report, Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (OutputStream out = Files.newOutputStream(file)) {
            XMLStreamWriter xml = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
            xml.writeStartDocument("UTF-8", "1.0");
            xml.writeCharacters("\n");
            xml.writeStartElement("report");

            textElement(xml, 1, "machineCount", null, null, String.valueOf(report.machineCount()));
            textElement(xml, 1, "workOrderCount", null, null, String.valueOf(report.workOrderCount()));

            startElement(xml, 1, "openWorkOrdersByPriority");
            for (Map.Entry<WorkOrder.Priority, Long> e : report.openOrdersByPriority().entrySet()) {
                textElement(xml, 2, "priority", "name", e.getKey().name(), String.valueOf(e.getValue()));
            }
            endElement(xml, 1);

            startElement(xml, 1, "breakdownRanking");
            for (App.MachineBreakdowns m : report.topBreakdowns()) {
                xml.writeCharacters("\n" + INDENT.repeat(2));
                xml.writeEmptyElement("machine");
                xml.writeAttribute("id", m.machineId());
                xml.writeAttribute("breakdowns", String.valueOf(m.count()));
            }
            endElement(xml, 1);

            startElement(xml, 1, "mttrByHall");
            for (Map.Entry<String, Duration> e : report.mttrByHall().entrySet()) {
                textElement(xml, 2, "hall", "name", e.getKey(), DurationFormat.format(e.getValue()));
            }
            endElement(xml, 1);

            endElement(xml, 0);
            xml.writeCharacters("\n");
            xml.writeEndDocument();
            xml.close();
        } catch (XMLStreamException e) {
            throw new IOException("Cannot write report to " + file, e);
        }
    }

    private static void startElement(XMLStreamWriter xml, int depth, String name) throws XMLStreamException {
        xml.writeCharacters("\n" + INDENT.repeat(depth));
        xml.writeStartElement(name);
    }

    private static void endElement(XMLStreamWriter xml, int depth) throws XMLStreamException {
        xml.writeCharacters("\n" + INDENT.repeat(depth));
        xml.writeEndElement();
    }

    private static void textElement(XMLStreamWriter xml, int depth, String name,
                                    String attribute, String attributeValue, String text) throws XMLStreamException {
        startElement(xml, depth, name);
        if (attribute != null) {
            xml.writeAttribute(attribute, attributeValue);
        }
        xml.writeCharacters(text);
        xml.writeEndElement();
    }

    public Report read(Path file) throws IOException {
        Document doc;
        try (InputStream in = Files.newInputStream(file)) {
            doc = newDocumentBuilder().parse(in);
        } catch (SAXException e) {
            throw new IOException("Cannot parse report " + file, e);
        }

        Element root = doc.getDocumentElement();
        if (!"report".equals(root.getTagName())) {
            throw new IOException("Unexpected root element: " + root.getTagName());
        }

        int machineCount = Integer.parseInt(child(root, "machineCount").getTextContent().trim());
        int workOrderCount = Integer.parseInt(child(root, "workOrderCount").getTextContent().trim());

        Map<WorkOrder.Priority, Long> open = new LinkedHashMap<>();
        for (Element e : children(child(root, "openWorkOrdersByPriority"), "priority")) {
            open.put(WorkOrder.Priority.valueOf(e.getAttribute("name")), Long.parseLong(e.getTextContent().trim()));
        }

        List<App.MachineBreakdowns> ranking = new ArrayList<>();
        for (Element e : children(child(root, "breakdownRanking"), "machine")) {
            ranking.add(new App.MachineBreakdowns(e.getAttribute("id"), Long.parseLong(e.getAttribute("breakdowns"))));
        }

        Map<String, Duration> mttr = new LinkedHashMap<>();
        for (Element e : children(child(root, "mttrByHall"), "hall")) {
            mttr.put(e.getAttribute("name"), DurationFormat.parse(e.getTextContent()));
        }

        return new Report(machineCount, workOrderCount, open, ranking, mttr);
    }

    private static DocumentBuilder newDocumentBuilder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Element child(Element parent, String name) throws IOException {
        List<Element> found = children(parent, name);
        if (found.isEmpty()) {
            throw new IOException("Missing element <" + name + "> in <" + parent.getTagName() + ">");
        }
        return found.getFirst();
    }

    private static List<Element> children(Element parent, String name) {
        List<Element> result = new ArrayList<>();
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element element && name.equals(element.getTagName())) {
                result.add(element);
            }
        }
        return result;
    }
}
