package ru.bis.datadic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for describe all elements in complex type or ancestor element
 */
public class ElementList {
    Logger logger = LogManager.getLogger(SimpleTypeList.class);

    private HashMap<Integer, Element> map;
    private Integer newId = 0;

    public ElementList() {
        this.map = new HashMap<>();
    }

    public HashMap<Integer, Element> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, Element> map) {
        this.map = map;
    }

    public void add(Element element) {
        Integer id = ++newId;
        map.put(id, element);
    }

    /**
     * Reads all import definitions
     * @param node - element to parse
     */
    public void getElements(Node node) {
        NodeList elements = node.getChildNodes();
        int count = 0;
        for (int i = 0; i < elements.getLength(); i++) {
            // Each nested node: simpleType, empty text, etc
            Node element = elements.item(i);
            if (element.getNodeType() != Node.TEXT_NODE) {
                if (element.getNodeName().equals("xs:element")) {
                    Element el = new Element();
                    el.readElement(element);
                    if (el != null) {
                        map.put(count, el);
                        count++;
                    }
                }
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        boolean first = true;
        for (Map.Entry<Integer, Element> item : map.entrySet()) {
            if (!first) str.append(", "); else first = false;
            str.append(item.getValue());
        }
        return str.toString();
    }

    public void log() {
        for (Map.Entry<Integer, Element> entry : map.entrySet()) {
            logger.info(entry.getValue().toString());
        }
    }

    /**
     * Outputs all simple types to XLS sheet
     * @param rowCount - current XLS sheet row number
     * @param sheet - XLS sheet
     * @return - current XLS sheet row number after output
     */
    public int outXLS(int rowCount, Sheet sheet) {
        for (Integer idSimple : map.keySet()) {
            rowCount = map.get(idSimple).outXLS(rowCount, sheet);
        }
        return rowCount;
    }
}
