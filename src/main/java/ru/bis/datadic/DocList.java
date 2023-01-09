package ru.bis.datadic;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for store all annotations (documentations) for element, attribute, base type, complex type, etc
 */
public class DocList {
    private HashMap<Integer, String> map;
    private Integer newId = 0;

    public DocList() {
        this.map = new HashMap<>();
    }

    public DocList(String str) {
        this();
        add(str);
    }

    public HashMap<Integer, String> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, String> map) {
        this.map = map;
    }

    public void add(String doc) {
        Integer id = ++newId;
        map.put(id, doc);
    }

    /**
     * Get all annotations for specified element. Annotations are read from all child elements <xs:documentation>
     * @param node - element
     */
    public void readDoc(Node node) {
        NodeList nestedNodes = node.getChildNodes();
        int count = 0;
        for (int i = 0; i < nestedNodes.getLength(); i++) {
            Node edChildNode = nestedNodes.item(i);
            if (edChildNode.getNodeName().equals("xs:documentation")) {
                map.put(++count, edChildNode.getTextContent());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        for (Map.Entry<Integer, String> item : map.entrySet()) {
            str.append(item.getValue());
        }
        return str.toString();
    }

    /**
     * Saves element <xs:annotation> with specified tab level
     * @param tabs - tabs level
     * @param writer - file to save
     * @throws IOException
     */
    public void saveDoc(String tabs, BufferedWriter writer) throws IOException {
        if (map.size() > 0) {
            writer.write(tabs + "<xs:annotation>" + System.lineSeparator());
            for (Map.Entry<Integer, String> oneDoc : map.entrySet()) {
                writer.write(tabs + "\t<xs:documentation>" + oneDoc.getValue() + "</xs:documentation>" + System.lineSeparator());
            }
            writer.write(tabs + "</xs:annotation>" + System.lineSeparator());
        }
    }

}
