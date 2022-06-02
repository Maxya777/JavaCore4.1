import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

    private static <T> String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static void writeString(String json) {
        try (FileWriter file = new FileWriter("new_data.json")) {
            file.write(json);
            file.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static List<Employee> parseXML(String file) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(file));
        Node root = doc.getDocumentElement();
        NodeList nodeList = doc.getElementsByTagName("employee");
        List<Employee> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            list.add(getEmployee(nodeList.item(i)));
        }
        return list;
    }

    private static Employee getEmployee(Node node) {
        Employee employee = new Employee();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            employee.setId(Integer.parseInt(getTagValue("id", element)));
            employee.setFirstName(getTagValue("firstName", element));
            employee.setLastName(getTagValue("lastName", element));
            employee.setCountry(getTagValue("country", element));
            employee.setAge(Integer.parseInt(getTagValue("age", element)));
        }
        return employee;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }


    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json);

        List<Employee> list2 = parseXML("data.xml");
        json = listToJson(list2);
        writeString(json);
    }
}


