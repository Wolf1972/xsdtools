package ru.bis.datadic;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

public class ImportSectionList {
    private HashMap<Integer, ImportSection> map;
    private Integer newId = 0;

    public ImportSectionList() {
        this.map = new HashMap<>();
    }

    public HashMap<Integer, ImportSection> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, ImportSection> map) {
        this.map = map;
    }

    public void add(String nameSpace, String fileName) {
        Integer id = ++newId;
        map.put(id, new ImportSection(nameSpace, fileName));
    }

    public void add(ImportSection importSection) {
        Integer id = ++newId;
        map.put(id, importSection);
    }

    /**
     * Reads all import definitions
     * @param node - element to parse
     */
    void getImports(Node node) {
        NodeList elements = node.getChildNodes();
        int count = 0;
        for (int i = 0; i < elements.getLength(); i++) {
            // Each nested node: simpleType, empty text, etc
            Node element = elements.item(i);
            if (element.getNodeType() != Node.TEXT_NODE) {
                if (element.getNodeName().equals("xs:import")) {
                    ImportSection oneImport = readOneImportSection(element);
                    if (oneImport != null) map.put(count, oneImport);
                    count++;
                }
            }
        }
    }

    /**
     * Get one definition of import file from specified element <xs:import>
     * @param node - element
     */
    private ImportSection readOneImportSection(Node node) {
        if (node.getNodeName().equals("xs:import")) {
            String nameSpace = null;
            String file = null;
            NamedNodeMap attrs = node.getAttributes();
            Node nsNode = attrs.getNamedItem("namespace");
            if (nsNode != null) nameSpace = nsNode.getNodeValue();
            Node nsLoc = attrs.getNamedItem("schemaLocation");
            if (nsLoc != null) file = nsLoc.getNodeValue();
            ImportSection importSection = new ImportSection(nameSpace, file);
            return importSection;
        }
        return null;
    }
}
