package repository.file.xml;

import domain.Car;
import domain.Reservation;
import org.w3c.dom.*;
import repository.base.IRepository;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ReservationRepositoryXMLFile implements IRepository<Integer, Reservation> {
    private final String filePath;

    public ReservationRepositoryXMLFile(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void add(Integer id, Reservation reservation) {
        try {
            // Initialize Document Builder
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            File file = new File(filePath);

            if (!file.exists()) {
                return;
            }

            // Load existing XML or create a new document
            if (file.exists()) {
                doc = dBuilder.parse(file);
            } else {
                doc = dBuilder.newDocument();
                Element rootElement = doc.createElement("reservations");
                doc.appendChild(rootElement);
            }

            // Normalize and clean whitespace
            doc.getDocumentElement().normalize();
            removeWhitespaceNodes(doc.getDocumentElement());

            // Create new Reservation element
            Element reservationElement = doc.createElement("Reservation");
            reservationElement.setAttribute("id", String.valueOf(id));

            // Add Car element
            Element carElement = doc.createElement("Car");
            Car car = reservation.getCar();

            Element carID = doc.createElement("id");
            carID.setTextContent(String.valueOf(car.getId()));
            carElement.appendChild(carID);

            Element brand = doc.createElement("brand");
            brand.setTextContent(car.getBrand());
            carElement.appendChild(brand);

            Element model = doc.createElement("model");
            model.setTextContent(car.getModel());
            carElement.appendChild(model);

            Element year = doc.createElement("year");
            year.setTextContent(String.valueOf(car.getYear()));
            carElement.appendChild(year);

            reservationElement.appendChild(carElement);

            // Add startingDate and endingDate elements
            Element startDateElement = doc.createElement("startDate");
            startDateElement.setTextContent(reservation.getStartDate().toString());
            reservationElement.appendChild(startDateElement);

            Element endDateElement = doc.createElement("endDate");
            endDateElement.setTextContent(reservation.getEndDate().toString());
            reservationElement.appendChild(endDateElement);

            // Append the new Reservation element to the root
            doc.getDocumentElement().appendChild(reservationElement);

            // Write the updated XML back to the file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Reservation> delete(Integer id) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            File file = new File(filePath);

            if (!file.exists()) {
                return Optional.empty();
            }

            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            removeWhitespaceNodes(doc.getDocumentElement());

            NodeList reservationNodes = doc.getElementsByTagName("Reservation");

            for (int i = 0; i < reservationNodes.getLength(); i++) {
                Element reservationElement = (Element) reservationNodes.item(i);

                Integer reservationId = Integer.parseInt(reservationElement.getAttribute("id"));
                if (reservationId.equals(id)) {
                    Reservation reservationToDelete = new Reservation(
                            reservationId,
                            new Car(
                                    Integer.parseInt(reservationElement.getElementsByTagName("id").item(0).getTextContent()),
                                    reservationElement.getElementsByTagName("brand").item(0).getTextContent(),
                                    reservationElement.getElementsByTagName("model").item(0).getTextContent(),
                                    Integer.parseInt(reservationElement.getElementsByTagName("year").item(0).getTextContent())
                            ),
                            reservationElement.getElementsByTagName("startDate").item(0).getTextContent(),
                            reservationElement.getElementsByTagName("endDate").item(0).getTextContent()
                    );

                    reservationElement.getParentNode().removeChild(reservationElement);

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(file);
                    transformer.transform(source, result);

                    return Optional.of(reservationToDelete);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void modify(Integer id, Reservation reservation) {
        try {
            // Initialize Document Builder
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            File file = new File(filePath);

            // Check if file exists
            if (!file.exists()) {
                return;
            }

            // Parse the XML file
            Document doc = dBuilder.parse(file);

            // Normalize and clean whitespace
            doc.getDocumentElement().normalize();
            removeWhitespaceNodes(doc.getDocumentElement());

            // Get all "Reservation" elements
            NodeList reservationNodes = doc.getElementsByTagName("Reservation");

            boolean found = false;

            // Loop through each Reservation to find the one with the matching id
            for (int i = 0; i < reservationNodes.getLength(); i++) {
                Element reservationElement = (Element) reservationNodes.item(i);

                // Get the "id" attribute of the reservation and check if it matches
                String reservationId = reservationElement.getAttribute("id");
                if (reservationId.equals(id.toString())) {
                    found = true;

                    // Update Car details
                    Element carElement = (Element) reservationElement.getElementsByTagName("Car").item(0);
                    carElement.getElementsByTagName("id").item(0).setTextContent(reservation.getCar().getId().toString());
                    carElement.getElementsByTagName("brand").item(0).setTextContent(reservation.getCar().getBrand());
                    carElement.getElementsByTagName("model").item(0).setTextContent(reservation.getCar().getModel());
                    carElement.getElementsByTagName("year").item(0).setTextContent(String.valueOf(reservation.getCar().getYear()));

                    // Update start and end dates
                    reservationElement.getElementsByTagName("startDate").item(0).setTextContent(reservation.getStartDate().toString());
                    reservationElement.getElementsByTagName("endDate").item(0).setTextContent(reservation.getEndDate().toString());

                    break;
                }
            }

            if (!found) {
                return;
            }

            // Save the changes back to the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Reservation> findById(Integer id) {
        try {
            // Initialize Document Builder
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            File file = new File(filePath);

            if (!file.exists()) {
                return Optional.empty();
            }

            // Parse the XML file
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            // Get all "Reservation" elements
            NodeList reservationNodes = doc.getElementsByTagName("Reservation");

            // Loop through each Reservation to find the one with the matching id
            for (int i = 0; i < reservationNodes.getLength(); i++) {
                Element reservationElement = (Element) reservationNodes.item(i);

                // Get the "id" attribute of the reservation and check if it matches
                String reservationId = reservationElement.getAttribute("id");
                if (reservationId.equals(id.toString())) {

                    // Get Car details
                    Element carElement = (Element) reservationElement.getElementsByTagName("Car").item(0);
                    Integer carId = Integer.parseInt(carElement.getElementsByTagName("id").item(0).getTextContent());
                    String brand = carElement.getElementsByTagName("brand").item(0).getTextContent();
                    String model = carElement.getElementsByTagName("model").item(0).getTextContent();
                    int year = Integer.parseInt(carElement.getElementsByTagName("year").item(0).getTextContent());
                    Car car = new Car(carId, brand, model, year);

                    // Get start and end dates
                    String startDateStr = reservationElement.getElementsByTagName("startDate").item(0).getTextContent();
                    String endDateStr = reservationElement.getElementsByTagName("endDate").item(0).getTextContent();

                    // Parse dates into LocalDate format
                    LocalDate startDate = LocalDate.parse(startDateStr);
                    LocalDate endDate = LocalDate.parse(endDateStr);

                    // Create and return the Reservation object
                    return Optional.of(new Reservation(id, car, startDate, endDate));
                }
            }
            return Optional.empty();

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Iterable<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        try {
            // Initialize Document Builder
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            File file = new File(filePath);

            if (!file.exists()) {
                return reservations;
            }

            // Parse the XML file
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            // Get all "Reservation" nodes
            NodeList reservationNodes = doc.getElementsByTagName("Reservation");

            for (int i = 0; i < reservationNodes.getLength(); i++) {
                Element reservationElement = (Element) reservationNodes.item(i);

                // Get Reservation ID
                Integer id = Integer.parseInt(reservationElement.getAttribute("id"));

                // Get Car details
                Element carElement = (Element) reservationElement.getElementsByTagName("Car").item(0);

                int carID = Integer.parseInt(carElement.getElementsByTagName("id").item(0).getTextContent());
                String brand = carElement.getElementsByTagName("brand").item(0).getTextContent();
                String model = carElement.getElementsByTagName("model").item(0).getTextContent();
                int year = Integer.parseInt(carElement.getElementsByTagName("year").item(0).getTextContent());
                Car car = new Car(carID, brand, model, year);

                // Get startingDate and endingDate
                LocalDate startingDate = LocalDate.parse(reservationElement.getElementsByTagName("startDate").item(0).getTextContent());
                LocalDate endingDate = LocalDate.parse(reservationElement.getElementsByTagName("endDate").item(0).getTextContent());

                // Create a Reservation object and add it to the list
                Reservation reservation = new Reservation(id, car, startingDate, endingDate);
                reservations.add(reservation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservations;
    }

    @Override
    public Iterator<Reservation> getIterator() {
        return getAll().iterator();
    }

    @Override
    public int size() {
        return ((List<Reservation>) getAll()).size();
    }

    // Helper function to remove whitespace-only text nodes
    private void removeWhitespaceNodes(Element element) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Text && ((Text) node).getData().trim().isEmpty()) {
                element.removeChild(node);
                i--; // Adjust index after removal
            } else if (node instanceof Element) {
                removeWhitespaceNodes((Element) node); // Recursively clean child elements
            }
        }
    }
}
