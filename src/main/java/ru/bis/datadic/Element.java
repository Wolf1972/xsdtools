package ru.bis.datadic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for describe one element entry
 */
public class Element {
    private String name;
    private String sysName;
    private String type;
    private Integer minOccurs = 1;
    private Integer maxOccurs = 1; // May become null (when defined as "unbounded")
    private DocList doc;
    private ComplexType complexType; // Nested complex type possible

    public Element() { }

    public Element(String sysName, String name, String type, Integer minOccurs, Integer maxOccurs, DocList doc, ComplexType complexType) {
        this.sysName = sysName;
        this.name = name;
        this.type = type;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;
        this.doc = doc;
        this.complexType = complexType; // Inline complex type
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

    public void setType(String type) {
        this.type = type;
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public int getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public DocList getDoc() {
        return doc;
    }

    public void setDoc(DocList doc) {
        this.doc = doc;
    }

    public ComplexType getComplexType() { return complexType; }

    public void setComplexType(ComplexType complexType) { this.complexType = complexType; }

    /**
     * Process one element <xs:element>
     * @param node - node with element description
     */
    public void readElement(Node node) {

        NamedNodeMap attrs = node.getAttributes();
        Node nameNode = attrs.getNamedItem("name");
        if (nameNode != null) sysName = nameNode.getNodeValue();
        Node typeNode = attrs.getNamedItem("type");
        if (typeNode != null) type = typeNode.getNodeValue();
        Node minNode = attrs.getNamedItem("minOccurs");
        if (minNode != null) minOccurs = Integer.parseInt(minNode.getNodeValue());
        Node maxNode = attrs.getNamedItem("maxOccurs");
        if (maxNode != null) {
            if (maxNode.getNodeValue().equals("unbounded")) {
                maxOccurs = null;
            }
            else {
                maxOccurs = Integer.parseInt(maxNode.getNodeValue());
            }
        }

        NodeList nestedNodes = node.getChildNodes();
        for (int i = 0; i < nestedNodes.getLength(); i++) {
            Node edChildNode = nestedNodes.item(i);
            if (edChildNode.getNodeName().equals("xs:annotation")) {
                doc = new DocList();
                doc.readDoc(edChildNode);
            }
            else if (edChildNode.getNodeName().equals("xs:complexType")) {
                complexType = new ComplexType();
                complexType.readComplexType(edChildNode);
            }
        }
    }

    @Override
    public String toString() {
        return "ELEMENT: " + sysName + " [" + minOccurs + ":" + (maxOccurs != null ? maxOccurs : "*") + "]" +
        (type != null? ":" + type : "") +
        (complexType != null? " {" + complexType.toString() + "}" : "");
    }

    /**
     * Outputs one element to XLS sheet with grouping
     * @param rowCount - current XLS sheet row number
     * @param sheet - XLS sheet
     * @return - current XLS sheet row number after output
     */
    public int outXLS(int rowCount, Sheet sheet) {
        int startRow = rowCount; // Start row for grouping
        Row eleRow = sheet.createRow(rowCount);
        Cell typeCell = eleRow.createCell(0);
        typeCell.setCellValue("Element");
        Cell nameCell = eleRow.createCell(1);
        nameCell.setCellValue(sysName);
        Cell reqCell = eleRow.createCell(2);
        StringBuilder reqStr = new StringBuilder("[");
        if (minOccurs == null)
            reqStr.append("*");
        else
            reqStr.append(minOccurs);
        reqStr.append("..");
        if (maxOccurs == null)
            reqStr.append("*");
        else
            reqStr.append(maxOccurs);
        reqStr.append("]");
        reqCell.setCellValue(reqStr.toString());
        Cell baseCell = eleRow.createCell(3);
        if (type != null) baseCell.setCellValue(type);
        else if (complexType != null) baseCell.setCellValue(complexType.getSysName());
        Cell descCell = eleRow.createCell(4);
        if (doc != null) descCell.setCellValue(doc.toString());
        rowCount++;
        if (complexType != null) {
            rowCount = complexType.outXLS(rowCount, sheet);
        }
        if (rowCount - startRow - 1 > 0) {
            sheet.groupRow(startRow + 1, rowCount - 1);
        }
        return rowCount;
    }
}
