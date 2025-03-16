package repository.file.xml;

import domain.Car;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


public class CarRepositoryXMLFile implements IRepository<Integer, Car> {
    private final File xmlFile;

    public CarRepositoryXMLFile(String filePath) {
        this.xmlFile = new File(filePath);

        if (!xmlFile.exists()) {
            try {
                xmlFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(Integer id, Car car) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            removeWhitespaceNodes(doc.getDocumentElement());

            Element rootElement = doc.getDocumentElement();

            Element newCar = doc.createElement("Car");
            newCar.setAttribute("id", String.valueOf(id));

            Element brand = doc.createElement("brand");
            brand.appendChild(doc.createTextNode(car.getBrand()));
            newCar.appendChild(brand);

            Element model = doc.createElement("model");
            model.appendChild(doc.createTextNode(car.getModel()));
            newCar.appendChild(model);

            Element year = doc.createElement("year");
            year.appendChild(doc.createTextNode(String.valueOf(car.getYear())));
            newCar.appendChild(year);

            rootElement.appendChild(newCar);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Car> delete(Integer id) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            removeWhitespaceNodes(doc.getDocumentElement());

            NodeList cars = doc.getElementsByTagName("Car");

            for (int i = 0; i < cars.getLength(); i++) {
                Element carElement = (Element) cars.item(i);

                if (carElement.getAttribute("id").equals(id.toString())) {
                    Car carToDelete = new Car(
                            Integer.parseInt(carElement.getAttribute("id")),
                            carElement.getElementsByTagName("brand").item(0).getTextContent(),
                            carElement.getElementsByTagName("model").item(0).getTextContent(),
                            Integer.parseInt(carElement.getElementsByTagName("year").item(0).getTextContent())
                    );

                    carElement.getParentNode().removeChild(carElement);

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                    transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(xmlFile);
                    transformer.transform(source, result);

                    return Optional.of(carToDelete);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void modify(Integer id, Car newCar) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // Parse the XML file
            Document doc = dBuilder.parse(xmlFile);

            // Normalize the document to remove extra whitespace
            doc.getDocumentElement().normalize();

            // Remove whitespace-only text nodes
            removeWhitespaceNodes(doc.getDocumentElement());

            NodeList cars = doc.getElementsByTagName("Car");
            for (int i = 0; i < cars.getLength(); i++) {
                Element car = (Element) cars.item(i);
                if (car.getAttribute("id").equals(id.toString())) {
                    car.getElementsByTagName("brand").item(0).setTextContent(newCar.getBrand());
                    car.getElementsByTagName("model").item(0).setTextContent(newCar.getModel());
                    car.getElementsByTagName("year").item(0).setTextContent(String.valueOf(newCar.getYear()));
                    break;
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Set the output format to match your desired style
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // Remove standalone="no" (optional)
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Car> findById(Integer id) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // Parse the XML file
            Document doc = dBuilder.parse(xmlFile);

            // Normalize the document to remove extra whitespace
            doc.getDocumentElement().normalize();

            // Remove whitespace-only text nodes
            removeWhitespaceNodes(doc.getDocumentElement());

            NodeList cars = doc.getElementsByTagName("Car");
            for (int i = 0; i < cars.getLength(); i++) {
                Element car = (Element) cars.item(i);
                if (car.getAttribute("id").equals(id.toString())) {
                    String brand = car.getElementsByTagName("brand").item(0).getTextContent();
                    String model = car.getElementsByTagName("model").item(0).getTextContent();
                    int year = Integer.parseInt(car.getElementsByTagName("year").item(0).getTextContent());
                    return Optional.of(new Car(id, brand, model, year));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Iterable<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            NodeList carNodes = doc.getElementsByTagName("Car");
            for (int i = 0; i < carNodes.getLength(); i++) {
                Element car = (Element) carNodes.item(i);
                Integer id = Integer.parseInt(car.getAttribute("id"));
                String brand = car.getElementsByTagName("brand").item(0).getTextContent();
                String model = car.getElementsByTagName("model").item(0).getTextContent();
                int year = Integer.parseInt(car.getElementsByTagName("year").item(0).getTextContent());
                cars.add(new Car(id, brand, model, year));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cars;
    }

    @Override
    public Iterator<Car> getIterator() {
        return getAll().iterator();
    }

    @Override
    public int size() {
        return ((List<Car>) getAll()).size();
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