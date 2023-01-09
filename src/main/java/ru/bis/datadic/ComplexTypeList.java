package ru.bis.datadic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class ComplexTypeList {
    Logger logger = LogManager.getLogger(SimpleTypeList.class);

    private HashMap<Integer, ComplexType> map;
    private Integer newId = 0;

    public ComplexTypeList() {
        this.map = new HashMap<>();
    }

    public HashMap<Integer, ComplexType> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, ComplexType> map) {
        this.map = map;
    }

    /**
     * Reads all child elements <xs:complexType> for specified element
     * @param node - element to parse
     */
    public void getComplexTypes(Node node) {
        NodeList nodes = node.getChildNodes();
        int count = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            // Each nested node: simpleType, empty text, etc
            Node ctNode = nodes.item(i);
            if (ctNode.getNodeType() != Node.TEXT_NODE) {
                if (ctNode.getNodeName().equals("xs:complexType")) {
                    ComplexType ct = new ComplexType();
                    ct.readComplexType(ctNode);
                    map.put(count, ct);
                    count++;
                }
            }
        }
    }

    public void add(ComplexType complexType) {
        Integer id = ++newId;
        map.put(id, complexType);
    }

    public void log() {
        for (Map.Entry<Integer, ComplexType> entry : map.entrySet()) {
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
