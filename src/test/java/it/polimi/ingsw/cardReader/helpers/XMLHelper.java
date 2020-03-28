package it.polimi.ingsw.cardReader.helpers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class XMLHelper {

    private static final String XML = "<?xml version=\"1.0\"?>" +
            "<Element>" +
            "</Element>";

    public static Element getElement(){
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(XML));
            Document doc = builder.parse(is);
            Element element = doc.getDocumentElement();
            assert (element != null);
            return element;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            assert false;
        }
        return null;
    }
}
