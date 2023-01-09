package ru.bis.datadic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Map;

/**
 * Class to describe one XSD file
 */
public class SchemeFile {
    Logger logger = LogManager.getLogger(SchemeFile.class);

    private String fileName;
    private SchemeFileList otherFiles;
    private String encoding;
    private ImportSectionList importList = new ImportSectionList();
    private SimpleTypeList simpleList = new SimpleTypeList();
    private NameSpaceList nsList = new NameSpaceList();
    private ComplexTypeList complexList = new ComplexTypeList();
    private ElementList elementList = new ElementList();
    private DocList docList = new DocList();
    private CommentList commentList = new CommentList();
    private String targetNs;
    private String version;
    private String elementFormDefault;
    private String attributeFormDefault;

    public SchemeFile(String fileName, SchemeFileList files) {
        this.otherFiles = files;
        this.fileName = fileName;
        if (fileName != null && fileName.length() > 0) {
            readFile(fileName);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SimpleTypeList getSimpleList() {
        return simpleList;
    }

    public void setSimpleList(SimpleTypeList listTypes) {
        this.simpleList = listTypes;
    }

    public ComplexTypeList getComplexList() {
        return complexList;
    }

    public void setComplexList(ComplexTypeList listComplex) {
        this.complexList = listComplex;
    }

    public ImportSectionList getImportList() {
        return importList;
    }

    public void setImportList(ImportSectionList importList) {
        this.importList = importList;
    }

    public DocList getDocList() {
        return docList;
    }

    public void setDocList(DocList doc) {
        this.docList = doc;
    }

    public CommentList getCommentList() { return commentList; }

    public void setCommentList(CommentList comments) { this.commentList = comments; }

    public NameSpaceList getNsList() {
        return nsList;
    }

    public void setListNs(NameSpaceList listNs) {
        this.nsList = listNs;
    }

    public String getTargetNs() {
        return targetNs;
    }

    public void setTargetNs(String targetNs) {
        this.targetNs = targetNs;
    }

    public ElementList getElementList() { return elementList; }

    public void setElementList(ElementList listElement) { this.elementList = listElement; }

    /**
     * Process one XSD file, fills types array
     * @param fileName - file name to parse (full path)
     * @return boolean: success or fail for file processing (true/false)
     */
    public boolean readFile(String fileName) {

        try {

            logger.trace("0803: XSD file read: " + fileName);

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String fileURI = fileName;
            if (!fileName.startsWith(".")) fileURI = "file:///" + fileName; // File name with cyrillic symbols or absolute path (?) doesn't work without it

            Document document = documentBuilder.parse(fileURI);

            encoding = document.getXmlEncoding();

            Node root = document.getDocumentElement();
            if (root.getNodeName().equals("xs:schema")) {
                // Get comments for root element
                commentList.readCommentList(root);
                // Get annotations for root element
                docList.readDoc(root);
                // Get imports
                importList.getImports(root);
                // Get namespaces
                getSchemeAttrs(root); // We rely that all namespaces declare in the root element only
                // Get types
                simpleList.getSimpleTypes(root);
                complexList.getComplexTypes(root);
                // Get elements describes
                elementList.getElements(root);
                return true;
            }
            else {
                logger.error("0805: File " + fileName + " contains unknown root element: " + root.getNodeName());
            }
            return false;
        }
        catch (ParserConfigurationException | SAXException e) {
            logger.error("0806: Error parsing file " + fileName, e);
        }
        catch (IOException e) {
            logger.error("0807. Error while file access: " + fileName);
        }
        return false;
    }

    /**
     * Reads all namespace definitions and target namespace from specified element
     * @param node - element
     */
    public void getSchemeAttrs(Node node) {
        // Get namespaces
        NamedNodeMap attrs = node.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            if (attrs.item(i).getNodeName().startsWith("xmlns:")) {
                nsList.add(Helper.getSimpleNodeName(attrs.item(i)), attrs.item(i).getNodeValue());
            }
            else if (attrs.item(i).getNodeName().equals("targetNamespace")) {
                targetNs = attrs.item(i).getNodeValue();
            }
            else if (attrs.item(i).getNodeName().equals("version")) {
                version = attrs.item(i).getNodeValue();
            }
            else if (attrs.item(i).getNodeName().equals("elementFormDefault")) {
                elementFormDefault = attrs.item(i).getNodeValue();
            }
            else if (attrs.item(i).getNodeName().equals("attributeFormDefault")) {
                attributeFormDefault = attrs.item(i).getNodeValue();
            }
        }
    }

    /**
     * Outputs XSD file info to logger
     */
    public void log() {
        logger.info("0004: Target namespace: " + targetNs);
        simpleList.log();
        complexList.log();
        elementList.log();
    }

    /**
     * Writes one XSD file, fills types array
     * @param fileName - file name to write (full path)
     * @return boolean: success or fail for file processing (true/false)
     */
    public boolean writeFile(String fileName) {

        try {
            OutputStream osp = new FileOutputStream(fileName);
            BufferedWriter schemeWriter = new BufferedWriter(new OutputStreamWriter(osp, encoding));
            // Prolog
            String rootElement = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>";
            schemeWriter.write(rootElement + System.lineSeparator());
            // Header comments
            for (Map.Entry<Integer, String> oneComment : commentList.getMap().entrySet()) {
                schemeWriter.write("<!--" + oneComment.getValue() + "-->");
            }
            schemeWriter.write(System.lineSeparator());
            // Root element begins
            schemeWriter.write("<xs:schema ");
            for (Map.Entry<Integer, NameSpace> oneNs : nsList.getMap().entrySet()) {
                schemeWriter.write("xmlns:" + oneNs.getValue().getPrefix() + "=\"" + oneNs.getValue().getNameSpace() + "\" ");
            }
            schemeWriter.write("targetNamespace=\"" + targetNs + "\" ");
            schemeWriter.write("elementFormDefault=\"" + elementFormDefault + "\" ");
            schemeWriter.write("attributeFormDefault=\"" + attributeFormDefault + "\" ");
            schemeWriter.write("version=\"" + version + "\">");
            schemeWriter.write(System.lineSeparator());
            // Annotation for root element
            docList.saveDoc("\t", schemeWriter);
            // Simple types
            for (Map.Entry<Integer, SimpleType> oneSt : simpleList.getMap().entrySet()) {
                oneSt.getValue().saveSimpleType("\t", schemeWriter);
            }
            // Root element ends
            schemeWriter.write("</xs:schema>");
            schemeWriter.close();
            logger.info("0813: Output scheme file created: " + fileName);
        }
        catch (IOException e) {
            logger.error("0812: Error write output file: " + fileName);
            return false;
        }
        return true;
    }
}
