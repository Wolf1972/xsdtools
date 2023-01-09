package ru.bis.datadic;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Class for describe restriction
 */
public class Restriction {
    private String base;
    private DocList doc;
    private String pattern;
    private String minInclusive; // Can be integer or decimal (e.g. minInclusive="0.0001")
    private String maxInclusive;
    private String minExclusive;
    private String maxExclusive;
    private Integer totalDigits;
    private Integer fractionDigits;
    private Integer length;
    private Integer minLength;
    private Integer maxLength;
    private DocList enumMap; // Enumeration map

    public Restriction() { this.enumMap = new DocList(); }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public DocList getDoc() {
        return doc;
    }

    public void setDoc(DocList doc) {
        this.doc = doc;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getMinInclusive() { return minInclusive; }

    public void setMinInclusive(String minInclusive) { this.minInclusive = minInclusive; }

    public String getMaxInclusive() { return maxInclusive; }

    public void setMaxInclusive(String maxInclusive) { this.maxInclusive = maxInclusive; }

    public String getMinExclusive() { return minExclusive; }

    public void setMinExclusive(String minExclusive) { this.minExclusive = minExclusive; }

    public String getMaxExclusive() { return maxExclusive; }

    public void setMaxExclusive(String maxExclusive) { this.maxExclusive = maxExclusive; }

    public Integer getTotalDigits() { return totalDigits; }

    public void setTotalDigits(Integer totalDigits) { this.totalDigits = totalDigits; }

    public Integer getFractionDigits() { return fractionDigits; }

    public void setFractionDigits(Integer fractionDigits) { this.fractionDigits = fractionDigits; }

    public Integer getLength() { return length; }

    public void setLength(Integer length) { this.length = length; }

    public Integer getMinLength() { return minLength; }

    public void setMinLength(Integer minLength) { this.minLength = minLength; }

    public Integer getMaxLength() { return maxLength; }

    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }

    public DocList getEnumMap() { return enumMap; }

    public void setEnumMap(DocList enumMap) { this.enumMap = enumMap; }

    /**
     * Process restriction definition from child element <xs:restriction>
     * @param node - element
     */
    public void readRestriction(Node node) {
        NamedNodeMap attrs = node.getAttributes();
        Node nameNode = attrs.getNamedItem("base");
        if (nameNode != null) base = nameNode.getNodeValue();
        NodeList childElements = node.getChildNodes();

        for (int i = 0; i < childElements.getLength(); i++) {
            // Each nested node: simpleType, empty text, etc
            Node childElement = childElements.item(i);
            if (childElement.getNodeType() != Node.TEXT_NODE) {
                if (childElement.getNodeName().equals("xs:pattern")) {
                    NamedNodeMap patternAttrs = childElement.getAttributes();
                    Node patternNode = patternAttrs.getNamedItem("value");
                    if (patternNode != null) pattern = patternNode.getTextContent(); // Unable to prevent automatic macro resolution?
                    // Get comments
                    NodeList patternElements = childElement.getChildNodes();
                    for (int j = 0; j < patternElements.getLength(); j++) {
                        Node patternChild = patternElements.item(j);
                        if (patternChild.getNodeType() != Node.TEXT_NODE) {
                            if (patternChild.getNodeName().equals("xs:annotation")) {
                                if (doc == null) doc = new DocList();
                                doc.readDoc(patternNode);
                            }
                        }
                    }
                }
                else if (childElement.getNodeName().equals("xs:length")) {
                    NamedNodeMap lengthAttrs = childElement.getAttributes();
                    Node lengthNode = lengthAttrs.getNamedItem("value");
                    if (lengthNode != null) length = Integer.parseInt(lengthNode.getTextContent());
                }
                else if (childElement.getNodeName().equals("xs:minLength")) {
                    NamedNodeMap minlengthAttrs = childElement.getAttributes();
                    Node minLengthNode = minlengthAttrs.getNamedItem("value");
                    if (minLengthNode != null) minLength = Integer.parseInt(minLengthNode.getTextContent());
                }
                else if (childElement.getNodeName().equals("xs:maxLength")) {
                    NamedNodeMap maxLengthAttrs = childElement.getAttributes();
                    Node maxLengthNode = maxLengthAttrs.getNamedItem("value");
                    if (maxLengthNode != null) maxLength = Integer.parseInt(maxLengthNode.getTextContent());
                }
                else if (childElement.getNodeName().equals("xs:minInclusive")) {
                    NamedNodeMap minAttrs = childElement.getAttributes();
                    Node minNode = minAttrs.getNamedItem("value");
                    if (minNode != null) minInclusive = minNode.getTextContent();
                }
                else if (childElement.getNodeName().equals("xs:maxInclusive")) {
                    NamedNodeMap maxAttrs = childElement.getAttributes();
                    Node maxNode = maxAttrs.getNamedItem("value");
                    if (maxNode != null) maxInclusive = maxNode.getTextContent();
                }
                else if (childElement.getNodeName().equals("xs:minExclusive")) {
                    NamedNodeMap minAttrs = childElement.getAttributes();
                    Node minNode = minAttrs.getNamedItem("value");
                    if (minNode != null) minExclusive = minNode.getTextContent();
                }
                else if (childElement.getNodeName().equals("xs:maxExclusive")) {
                    NamedNodeMap maxAttrs = childElement.getAttributes();
                    Node maxNode = maxAttrs.getNamedItem("value");
                    if (maxNode != null) maxExclusive = maxNode.getTextContent();
                }
                else if (childElement.getNodeName().equals("xs:enumeration")) {
                    NamedNodeMap enumAttrs = childElement.getAttributes();
                    Node enumNode = enumAttrs.getNamedItem("value");
                    if (enumNode != null) enumMap.add(enumNode.getTextContent());
                }
                else if (childElement.getNodeName().equals("xs:totalDigits")) {
                    NamedNodeMap totalAttrs = childElement.getAttributes();
                    Node totalNode = totalAttrs.getNamedItem("value");
                    if (totalNode != null) totalDigits = Integer.parseInt(totalNode.getTextContent());
                }
                else if (childElement.getNodeName().equals("xs:fractionDigits")) {
                    NamedNodeMap fracAttrs = childElement.getAttributes();
                    Node fracNode = fracAttrs.getNamedItem("value");
                    if (fracNode != null) fractionDigits = Integer.parseInt(fracNode.getTextContent());
                }
            }
        }
    }

    /**
     * Saves one restriction
     * @param tabs - tabs level
     * @param writer - file to save
     * @throws IOException
     */
    public void saveRestriction(String tabs, BufferedWriter writer) throws IOException {
        writer.write(tabs + "<xs:restriction base=\"" + base + "\">" + System.lineSeparator());
        if (pattern != null) {
            writer.write(tabs + "\t<xs:pattern value=\"" + pattern + "\">" + System.lineSeparator());
            if (doc != null) doc.saveDoc(tabs + "\t\t", writer);
            writer.write(tabs + "\t</xs:pattern>" + System.lineSeparator());
        }
        writer.write(tabs + "</xs:restriction>" + System.lineSeparator());
        // TODO: add save other kinds of restrictions
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restriction that = (Restriction) o;
        return Objects.equals(base, that.base) &&
                Objects.equals(doc, that.doc) &&
                Objects.equals(pattern, that.pattern) &&
                Objects.equals(minInclusive, that.minInclusive) &&
                Objects.equals(maxInclusive, that.maxInclusive) &&
                Objects.equals(minExclusive, that.minExclusive) &&
                Objects.equals(maxExclusive, that.maxExclusive) &&
                Objects.equals(length, that.length) &&
                Objects.equals(minLength, that.minLength) &&
                Objects.equals(maxLength, that.maxLength) &&
                Objects.equals(totalDigits, that.totalDigits) &&
                Objects.equals(fractionDigits, that.fractionDigits) &&
                Objects.equals(enumMap, that.enumMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, doc, pattern, minInclusive, maxInclusive, minExclusive, maxExclusive,
                length, minLength, maxLength, totalDigits, fractionDigits, enumMap);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (pattern != null) result.append(" pattern='" + pattern + '\'');
        if (minInclusive != null) result.append(" minInc=" + minInclusive);
        if (maxInclusive != null) result.append(" maxInc=" + maxInclusive);
        if (minExclusive != null) result.append(" minExc=" + minExclusive);
        if (maxExclusive != null) result.append(" maxExc=" + maxExclusive);
        if (length != null) result.append(" len=" + length);
        if (minLength != null) result.append(" minLen=" + minLength);
        if (maxLength != null) result.append(" maxLen=" + maxLength);
        if (totalDigits != null) result.append(" digits=" + totalDigits);
        if (fractionDigits != null) result.append(" frac=" + fractionDigits);
        if (enumMap.getMap().size() > 0) {
            result.append(" enum={");
            boolean isFirst = true;
            for (Integer key : enumMap.getMap().keySet()) {
                if (isFirst) isFirst = false;
                else result.append(',');
                result.append(enumMap.getMap().get(key));
            }
            result.append("} ");
        }
        return result.toString().trim();
    }
}
