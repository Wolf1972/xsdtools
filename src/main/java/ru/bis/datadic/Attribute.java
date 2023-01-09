package ru.bis.datadic;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for describe one attribute
 */
public class Attribute {
    private String name;
    private String sysName;
    private String type;
    private boolean isRequired;
    private DocList doc;
    private SimpleType simpleType; // Nested simple type possible

    public Attribute() { }

    public Attribute(String sysName, String name, String type, boolean isRequired, DocList doc, SimpleType simpleType) {
        this.sysName = sysName;
        this.name = name;
        this.type = type;
        this.isRequired = isRequired;
        this.doc = doc;
        this.simpleType = simpleType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) { this.type = type; }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public DocList getDoc() {
        return doc;
    }

    public void setDoc(DocList doc) {
        this.doc = doc;
    }

    public SimpleType getSimpleType() { return simpleType; }

    public void setSimpleType(SimpleType simpleType) { this.simpleType = simpleType; }

    /**
     * Process one element <xs:attribute>
     * @param node - element with attribute description
     */
    public void readAttribute(Node node, SchemeFileList otherFiles) {

        NamedNodeMap attrs = node.getAttributes();
        Node nameNode = attrs.getNamedItem("name");
        if (nameNode != null) sysName = nameNode.getNodeValue();
        Node typeNode = attrs.getNamedItem("type");
        if (typeNode != null) type = typeNode.getNodeValue();
        Node useNode = attrs.getNamedItem("use");
        if (useNode != null) isRequired = useNode.getNodeValue().equals("required");

        NodeList nestedNodes = node.getChildNodes();
        for (int i = 0; i < nestedNodes.getLength(); i++) {
            Node edChildNode = nestedNodes.item(i);
            if (edChildNode.getNodeName().equals("xs:annotation")) {
                doc = new DocList();
                doc.readDoc(edChildNode);
            }
            else if (edChildNode.getNodeName().equals("xs:simpleType")) {
                simpleType = new SimpleType();
                simpleType.readSimpleType(edChildNode);
            }
        }
        if (simpleType == null && otherFiles != null && type != null) { // Refers to simple type is defined in another file
            simpleType = otherFiles.searchForType(type, null);
        }
        if (type == null && simpleType != null && simpleType.getRestriction() != null) {
            type = simpleType.getRestriction().getBase();
        }
    }

    @Override
    public String toString() {
        return (isRequired ? "*" : "") + sysName + ":" +
                // Type can be declared directly or by reference to base type or by inline anonymous type that refers to base type
                (type != null ? type : (simpleType != null ? " {" +
                                (simpleType.getSysName() != null ? simpleType.getSysName() : (simpleType.getRestriction() != null ? simpleType.getRestriction().getBase() : "?")) +
                                "}" : ""));
    }

    /**
     * Outputs one attribute to XLS sheet
     * @param rowCount - current XLS sheet row number
     * @param sheet - XLS sheet
     * @return - current XLS sheet row number after output
     */
    public int outXLS(int rowCount, Sheet sheet) {
        Row attrRow = sheet.createRow(rowCount);
        Cell typeCell = attrRow.createCell(0);
        typeCell.setCellValue("Attr");
        Cell nameCell = attrRow.createCell(1);
        nameCell.setCellValue(sysName);
        Cell reqCell = attrRow.createCell(2);
        reqCell.setCellValue(isRequired ? "[1..1]" : "[0..1]");
        Cell baseCell = attrRow.createCell(3);
        baseCell.setCellValue(type);
        Cell descCell = attrRow.createCell(4);
        if (doc != null) descCell.setCellValue(doc.toString());
        Cell consCell = attrRow.createCell(5);
        if (simpleType != null) {
            if (simpleType.getRestriction() != null) consCell.setCellValue(simpleType.getRestriction().toString());
        }
        rowCount++;
        return rowCount;
    }
}
