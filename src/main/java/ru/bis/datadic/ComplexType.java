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

public class ComplexType {
    private String sysName;
    private String name;
    private DocList doc;
    private AttributeList attrs;
    private ElementList sequence;
    private ElementList choice;
    private ElementList all;
    private String extBaseType;
    private ComplexType extContent;
    private SchemeFileList otherFiles;

    public ComplexType() { }

    public ComplexType(String sysName, String name, DocList doc, AttributeList attrs, ElementList sequence,
                       ElementList choice, ElementList all, String baseType, ComplexType extContent, SchemeFileList otherFiles) {
        this.sysName = sysName;
        this.name = name;
        this.doc = doc;
        this.attrs = attrs;
        this.sequence = sequence;
        this.choice = choice;
        this.all = all;
        this.extBaseType = baseType; // Base type to extend
        this.extContent = extContent; // Extend base type as nested complex type
        this.otherFiles = otherFiles; // Reference to other files
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocList getDoc() {
        return doc;
    }

    public void setDoc(DocList description) {
        this.doc = description;
    }

    public AttributeList getAttrs() {
        return attrs;
    }

    public void setAttrs(AttributeList attrs) {
        this.attrs = attrs;
    }

    public ElementList getSequence() {
        return sequence;
    }

    public void setSequence(ElementList sequence) {
        this.sequence = sequence;
    }

    public ElementList getChoice() {
        return choice;
    }

    public void setChoice(ElementList choice) {
        this.choice = choice;
    }

    public ElementList getAll() { return all; }

    public void setAll(ElementList all) { this.all = all; }

    public String getExtBaseType() { return extBaseType; }

    public void setExtBaseType(String extBaseType) { this.extBaseType = extBaseType; }

    public ComplexType getExtContent() { return extContent; }

    public void setExtContent(ComplexType extContent) { this.extContent = extContent; }

    public ComplexType getComplexContent() { return extContent; }

    public void setComplexContent(ComplexType complexContent) { this.extContent = complexContent; }

    /**
     * Process one simpleType element
     * @param element - element to parse
     */
    public void readComplexType(Node element) {
        NamedNodeMap attr = element.getAttributes();
        Node nameNode = attr.getNamedItem("name");
        if (nameNode != null) sysName = nameNode.getNodeValue();
        attrs = new AttributeList();
        sequence = new ElementList();
        choice = new ElementList();
        all = new ElementList();

        NodeList nestedNodes = element.getChildNodes();
        for (int i = 0; i < nestedNodes.getLength(); i++) {
            Node edChildNode = nestedNodes.item(i);
            if (edChildNode.getNodeName().equals("xs:annotation")) {
                doc = new DocList();
                doc.readDoc(edChildNode);
            }
            else if (edChildNode.getNodeName().equals("xs:attribute")) {
                Attribute attrDesc = new Attribute();
                attrDesc.readAttribute(edChildNode, otherFiles);
                attrs.add(attrDesc);
            }
            else if (edChildNode.getNodeName().equals("xs:complexContent")) {
                boolean isExtends = false;
                NodeList complexNodes = edChildNode.getChildNodes();
                for (int j = 0; j < complexNodes.getLength(); j++) {
                    Node sNode = complexNodes.item(j);
                    if (sNode.getNodeType() != Node.TEXT_NODE) {
                        if (sNode.getNodeName().equals("xs:extension")) {
                            NamedNodeMap attrsList = sNode.getAttributes();
                            Node baseNode = attrsList.getNamedItem("base");
                            if (baseNode != null) extBaseType = baseNode.getNodeValue();
                        }
                        else {
                            // ComplexContent has some elements that extends base type - read these elements after
                            isExtends = true;
                        }
                    }
                }
                if (isExtends) {
                    extContent = new ComplexType();
                    extContent.readComplexType(edChildNode);
                }
            }
            else if (edChildNode.getNodeName().equals("xs:sequence")) {
                NodeList seqNodes = edChildNode.getChildNodes();
                for (int j = 0; j < seqNodes.getLength(); j++) {
                    Node node = seqNodes.item(j);
                    if (node.getNodeType() != Node.TEXT_NODE) {
                        if (node.getNodeName().equals("xs:element")) {
                            Element elementDesc = new Element();
                            elementDesc.readElement(node);
                            sequence.add(elementDesc);
                        }
                    }
                }
            }
            else if (edChildNode.getNodeName().equals("xs:choice")) {
                NodeList choiceNodes = edChildNode.getChildNodes();
                for (int j = 0; j < choiceNodes.getLength(); j++) {
                    Node node = choiceNodes.item(j);
                    if (node.getNodeType() != Node.TEXT_NODE) {
                        if (node.getNodeName().equals("xs:element")) {
                            Element elementDesc = new Element();
                            elementDesc.readElement(node);
                            choice.add(elementDesc);
                        }
                    }
                }
            }
            else if (edChildNode.getNodeName().equals("xs:all")) {
                NodeList allNodes = edChildNode.getChildNodes();
                for (int j = 0; j < allNodes.getLength(); j++) {
                    Node node = allNodes.item(j);
                    if (node.getNodeType() != Node.TEXT_NODE) {
                        if (node.getNodeName().equals("xs:element")) {
                            Element elementDesc = new Element();
                            elementDesc.readElement(node);
                            all.add(elementDesc);
                        }
                    }
                }
            }
        }
    }


    @Override
    public String toString() {
        return  "COMPLEX: " + (sysName == null ? "" : sysName + " ") +
                ((attrs != null && attrs.getMap().size() > 0) ? "ATTRS: (" + attrs + ") " : "") +
                ((sequence != null && sequence.getMap().size() > 0) ? "SEQ: (" + sequence + ") " : "") +
                ((choice != null && choice.getMap().size() > 0) ? "CHOICE: (" + choice + ") " : "") +
                ((all != null && all.getMap().size() > 0) ? "ALL: (" + all + ") " : "") +
                (extBaseType != null ? "EXT " + extBaseType : "") +
                (extContent != null ? ": (" + extContent.toString() + ") " : "") +
                (doc != null ? doc : "");
    }

    /**
     * Outputs one complex type to XLS sheet with grouping
     * @param rowCount - current XLS sheet row number
     * @param sheet - XLS sheet
     * @return - current XLS sheet row number after output
     */
    public int outXLS(int rowCount, Sheet sheet) {
        int startRow = rowCount; // Start row for grouping
        Row typeRow = sheet.createRow(rowCount);
        Cell typeCell = typeRow.createCell(0);
        if (sysName != null) typeCell.setCellValue("Complex");
        Cell nameCell = typeRow.createCell(1);
        nameCell.setCellValue(sysName != null? sysName : "Complex");
        Cell baseCell = typeRow.createCell(3);
        if (extBaseType != null) baseCell.setCellValue(extBaseType);
        Cell descCell = typeRow.createCell(4);
        if (doc != null) descCell.setCellValue(doc.toString());
        rowCount++;
        if (attrs != null) rowCount = attrs.outXLS(rowCount, sheet);
        if (sequence != null && sequence.getMap().size() > 0) {
            Row rowSeq = sheet.createRow(rowCount);
            Cell seqSt = rowSeq.createCell(1);
            seqSt.setCellValue("Sequence");
            rowCount++;
            rowCount = sequence.outXLS(rowCount, sheet);
        }
        if (choice != null && choice.getMap().size() > 0) {
            Row rowCh = sheet.createRow(rowCount);
            Cell chSt = rowCh.createCell(1);
            chSt.setCellValue("Choice");
            rowCount++;
            rowCount = choice.outXLS(rowCount, sheet);
        }
        if (all != null && all.getMap().size() > 0) {
            Row rowAll = sheet.createRow(rowCount);
            Cell allSt = rowAll.createCell(1);
            allSt.setCellValue("All");
            rowCount++;
            rowCount = all.outXLS(rowCount, sheet);
        }
        if (rowCount - startRow - 1 > 0) {
            if (sysName != null) { // Grouping only for complex type definition (not for anonymous complex types)
                sheet.groupRow(startRow + 1, rowCount - 1);
            }
        }
        return rowCount;
    }
}
