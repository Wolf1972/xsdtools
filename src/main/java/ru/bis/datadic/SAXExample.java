package ru.bis.datadic;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

public class SAXExample {

    public static void main() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            XMLHandler handler = new XMLHandler();
            parser.parse(new File("cbr_ed_basetypes_v2018.3.0.xsd"), handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class XMLHandler extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("xs:pattern")) {
                String value = attributes.getValue("value");
                System.out.println("pattern@value=\"" + value + "\"");
            }
        }
    }
}