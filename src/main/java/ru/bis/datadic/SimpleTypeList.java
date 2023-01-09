package ru.bis.datadic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for list of all XSD type descriptions
 */
public class SimpleTypeList {
    Logger logger = LogManager.getLogger(SimpleTypeList.class);

    private HashMap<Integer, SimpleType> map;
    private Integer newId = 0;

    public SimpleTypeList() {
        this.map = new HashMap<>();
    }

    public HashMap<Integer, SimpleType> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, SimpleType> map) {
        this.map = map;
    }

    public void add(String nameSpace, String sysName, String name, DocList description, Restriction restriction) {
        Integer id = ++newId;
        map.put(id, new SimpleType(nameSpace, sysName, name, description, restriction));
    }

    public void add(SimpleType simpleType) {
        Integer id = ++newId;
        map.put(id, simpleType);
    }

    /**
     * Reads all child elements <xs:simpleType> for specified element
     * @param node - element to parse
     */
    public void getSimpleTypes(Node node) {
        NodeList elements = node.getChildNodes();
        int count = 0;
        for (int i = 0; i < elements.getLength(); i++) {
            // Each nested node: simpleType, empty text, etc
            Node element = elements.item(i);
            if (element.getNodeType() != Node.TEXT_NODE) {
                if (element.getNodeName().equals("xs:simpleType")) {
                    SimpleType st = new SimpleType();
                    st.readSimpleType(element);
                    map.put(count, st);
                    count++;
                }
            }
        }
    }

    /**
     * Search for specified simple type (considering namespace)
     * @param typeName - simple type name
     * @param nameSpace - namespace (if null then search performs without it)
     * @return SimpleType
     */
    public SimpleType find(String typeName, String nameSpace) {
        SimpleType found = null;
        for (Map.Entry<Integer, SimpleType> entry : map.entrySet()) {
            if (entry.getValue().getSysName().equals(typeName) && (nameSpace == null || entry.getValue().getSysName().equals(nameSpace))) {
                found = entry.getValue();
                break;
            }
        }
        return found;
    }

    public void log() {
        for (Map.Entry<Integer, SimpleType> entry : map.entrySet()) {
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
