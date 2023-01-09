package ru.bis.datadic;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Class for XSD type description
 */
public class SimpleType {
    private String nameSpace;
    private String sysName;
    private String name;
    private DocList doc;
    private Restriction restriction;

    public SimpleType() { }

    public SimpleType(String nameSpace, String sysName, String name, DocList doc, Restriction restriction) {
        this.nameSpace = nameSpace;
        this.sysName = sysName;
        this.name = name;
        this.doc = doc;
        this.restriction = restriction;
    }

    public String getNameSpace() { return nameSpace; }

    public void setNameSpace(String nameSpace) { this.nameSpace = nameSpace; }

    public String getSysName() { return sysName; }

    public void setSysName(String sysName) { this.sysName = sysName; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public DocList getDoc() { return doc; }

    public void setDoc(DocList description) { this.doc = description; }

    public Restriction getRestriction() { return restriction; }

    public void setRestriction(Restriction restriction) { this.restriction = restriction; }

    /**
     * Process one simpleType element
     * @param element - element to parse
     */
    public void readSimpleType(Node element) {
        NamedNodeMap attr = element.getAttributes();
        Node nameNode = attr.getNamedItem("name");
        if (nameNode != null) sysName = nameNode.getNodeValue();

        NodeList nestedNodes = element.getChildNodes();
        for (int i = 0; i < nestedNodes.getLength(); i++) {
            Node edChildNode = nestedNodes.item(i);
            if (edChildNode.getNodeName().equals("xs:annotation")) {
                doc = new DocList();
                doc.readDoc(edChildNode);
            }
            else if (edChildNode.getNodeName().equals("xs:restriction")) {
                restriction = new Restriction();
                restriction.readRestriction(edChildNode);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleType simpleType = (SimpleType) o;
        return Objects.equals(nameSpace, simpleType.nameSpace) &&
                sysName.equals(simpleType.sysName) &&
                Objects.equals(name, simpleType.name) &&
                Objects.equals(doc, simpleType.doc) &&
                Objects.equals(restriction, simpleType.restriction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameSpace, sysName, name, doc, restriction);
    }

    @Override
    public String toString() {
        return  "SIMPLE: " + (nameSpace == null ? "" : nameSpace + ":") +
                           (sysName == null ? "" : sysName + " ") +
                           (restriction != null ? "(" + restriction.getBase() + ") " : " ") +
                           (doc != null? doc : "");
    }

    /**
     * Saves one simple type description
     * @param tabs - tabs level
     * @param writer - file to save
     * @throws IOException
     */
    public void saveSimpleType(String tabs, BufferedWriter writer) throws IOException {
        writer.write(tabs + "<xs:simpleType name=\"" + sysName + "\">" + System.lineSeparator());
        if (doc != null) doc.saveDoc(tabs + "\t", writer);
        if (restriction != null) restriction.saveRestriction(tabs + "\t", writer);
        writer.write(tabs + "</xs:simpleType>" + System.lineSeparator());
    }

    /**
     * Outputs one simple type to XLS sheet
     * @param rowCount - current XLS sheet row number
     * @param sheet - XLS sheet
     * @return - current XLS sheet row number after output
     */
    public int outXLS(int rowCount, Sheet sheet) {
        Row typeRow = sheet.createRow(rowCount);
        Cell typeCell = typeRow.createCell(0);
        typeCell.setCellValue("Simple");
        Cell nameCell = typeRow.createCell(1);
        nameCell.setCellValue(sysName);
        Cell reqCell = typeRow.createCell(2);
        Cell baseCell = typeRow.createCell(3);
        if (restriction != null && restriction.getBase() != null) baseCell.setCellValue(restriction.getBase());
        Cell descCell = typeRow.createCell(4);
        if (doc != null) descCell.setCellValue(doc.toString());
        Cell consCell = typeRow.createCell(5);
        if (restriction != null) consCell.setCellValue(restriction.toString());
        rowCount++;
        return rowCount;
    }
}
