package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.common.utils.ResourceScanner;
import it.polimi.ingsw.server.cards.enums.*;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.model.enums.PlayerState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * This class allows to read cards from a file.
 * The cards returned (in form of List of CardFile) are completely checked syntactically and semantically.
 *
 * Syntax exceptions are maintained even if DTD syntax checking was introduced.
 * Checks were maintained anyway in order to be fully covered from external DTD manipulation, during reading phase.
 */
class CardReader {

    /**
     * Read a list of cards from an XML file
     * @param defaultCard Default strategy card, in the form of CardFile
     * @param file The path where the cards' XML is placed
     * @return List of CardFile of the indicated cards
     * @throws InvalidCardException If one of the card has problems syntactically or semantically.
     *                              It's always indicated the cause as message
     */
    public static List<CardFile> readCards(CardFile defaultCard, String file) throws InvalidCardException{
        assert (defaultCard != null && file != null);
        Document document;
        try{
            document = getXMLDocument(file);
        } catch (Exception e) {
            throw new InvalidCardException("[XML PARSE][" + e.getClass().toString() + "]" + e.getMessage());
        }
        return parseCards(defaultCard,document);
    }

    /**
     * Read XML Document from file
     * @param cardFile The path of the file
     * @return Document containing parsed XML data
     * @throws IOException If the file could not be read
     * @throws ParserConfigurationException If the parser finds invalid config
     * @throws SAXException If syntax check goes wrong
     */
    private static Document getXMLDocument(String cardFile) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(
                new ErrorHandler() {
                    public void warning(SAXParseException e) {
                        System.err.println("[XML PARSE]: " + e.getMessage()); // do nothing
                    }
                    public void error(SAXParseException e) throws SAXException {
                        throw e;
                    }
                    public void fatalError(SAXParseException e) throws SAXException {
                        throw e;
                    }
                }
        );

        ResourceScanner scanner = ResourceScanner.getInstance();
        Document document = builder.parse(scanner.getResourceAsStream(cardFile));
        document.getDocumentElement().normalize();
        return document;
    }

    /**
     * Parses a file containing cards into a list of CardFile
     * @param defaultCard Default strategy to patch the cards
     * @param xml Document xml of the file
     * @return List of CardFile
     * @throws InvalidCardException If syntax error occurs during the process
     */
    private static List<CardFile> parseCards(CardFile defaultCard, Document xml) throws InvalidCardException{
        List<CardFile> cardFiles = new LinkedList<>();
        //Check root node
        Element root = xml.getDocumentElement();
        if (!root.getNodeName().equals("Cards")){
            throw new InvalidCardException("[CARDS PARSER]Wrong root tag");
        }
        //Get cards
        NodeList nList = root.getElementsByTagName("Card");
        if (nList.getLength() == 0){
            throw new InvalidCardException("[CARDS PARSER]At least one card must be defined");
        }
        //For each card, parse it
        for(int i = 0; i<nList.getLength();i++){
            Node cardNode = nList.item(i);
            if (cardNode.getNodeType() != Node.ELEMENT_NODE){
                throw new InvalidCardException("[CARD PARSER]Cannot read card " + (i+1));
            }
            CardFileImpl cardFile = parseCard((Element)cardNode);
            //Check and patch the card
            CardValidator.checkCardFile(cardFile);
            CardPatcher.patchCard(defaultCard,cardFile);
            //If everything okay, add it to the list
            cardFiles.add(cardFile);
        }
        return cardFiles;
    }

    /**
     * Parse the CardFile from the xml element
     * @param cardElement Card tag to be parsed
     * @return CardFileImpl containing CardFile data
     * @throws InvalidCardException If syntax error occurs during the process
     */
    private static CardFileImpl parseCard(Element cardElement) throws InvalidCardException {
        //Get card name
        String cardName;
        NodeList nList = cardElement.getElementsByTagName("name");
        if (nList.getLength() != 1){
            throw new InvalidCardException("[CARD PARSER]Missing/Multiple tag name");
        }
        cardName = nList.item(0).getTextContent().trim();
        if (cardName.length() == 0){
            throw new InvalidCardException("[CARD PARSER]Invalid card name");
        }

        //Get card description
        String cardDescription;
        nList = cardElement.getElementsByTagName("description");
        if (nList.getLength() != 1){
            throw new InvalidCardException("[CARD PARSER]Missing/Multiple tag description");
        }
        cardDescription = nList.item(0).getTextContent().trim();
        if (cardDescription.length() == 0){
            throw new InvalidCardException("[CARD PARSER]Invalid card description");
        }

        //Parse the rules
        List<CardRuleImpl> cardRules = extractCardRules(cardElement);
        return new CardFileImpl(cardName,cardDescription,cardRules);
    }

    private static List<CardRuleImpl> extractCardRules(Element cardElement) throws InvalidCardException{
        List<CardRuleImpl> rulesList = new LinkedList<>();

        //Get card rules
        NodeList nRules = cardElement.getElementsByTagName("rules");
        if (nRules.getLength() != 1){
            throw new InvalidCardException("[RULE PARSER]Missing/Multiple tag rules");
        }
        Node rulesNode = nRules.item(0);
        if (rulesNode.getNodeType() != Node.ELEMENT_NODE){
            throw new InvalidCardException("[RULE PARSER]Missing tags rule");
        }
        Element rulesElement = (Element)rulesNode;

        //Let's extract all rules
        nRules = rulesElement.getElementsByTagName("rule");
        for(int i = 0; i<nRules.getLength();i++){ //For each rule
            Node ruleNode = nRules.item(i);
            TriggerType eventType;
            //Get rule trigger
            try{
                eventType = TriggerType.valueOf(ruleNode.getAttributes().getNamedItem("event").getNodeValue());
            }catch (IllegalArgumentException ex){
                throw  new InvalidCardException("[RULE PARSER]Event type '" + ruleNode.getAttributes().getNamedItem("event").getNodeValue() + "' not supported");
            }catch (NullPointerException ex){
                throw new InvalidCardException("[RULE PARSER]Cannot read tag event");
            }
            if (ruleNode.getNodeType() != Node.ELEMENT_NODE){
                throw new InvalidCardException("[RULE PARSER]Missing rule attributes");
            }
            Element ruleElement = (Element)ruleNode;

            //Get rule statements
            List<RuleStatementImpl> statements = new LinkedList<>();
            NodeList statementList = ruleElement.getElementsByTagName("statements");
            if (statementList.getLength() != 1){
                throw new InvalidCardException("[RULE PARSER]Missing/Invalid tag statements");
            }
            Node statementNode = statementList.item(0);
            if (statementNode.getNodeType() != Node.ELEMENT_NODE){
                throw new InvalidCardException("[RULE PARSER]Missing tags of statements");
            }
            NodeList children = statementNode.getChildNodes();
            for(int j=0;j<children.getLength();j++){
                Node child = children.item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE) { //Skipping spaces and return characters
                    statements.add(nodeToRuleStatement((Element)child));
                }
            }
            if(statements.size() == 0){ //A rule must have at least one statement
                throw new InvalidCardException("[RULE PARSER]Rule has no statements");
            }

            //Get Rule Effect
            RuleEffectImpl ruleEffect;
            NodeList nList = ruleElement.getElementsByTagName("effect");
            if (nList.getLength() != 1){
                throw new InvalidCardException("[EFFECT PARSER]Missing/Multiple tag effect");
            }
            Node effectNode = nList.item(0);
            if (effectNode.getNodeType() != Node.ELEMENT_NODE){
                throw new InvalidCardException("[EFFECT PARSER]Missing tags of effect");
            }
            ruleEffect = nodeToRuleEffect((Element)effectNode);

            //Add rule to list
            rulesList.add(new CardRuleImpl(eventType, statements, ruleEffect));
        }
        return rulesList;
    }

    private static RuleStatementImpl nodeToRuleStatement(Element node) throws InvalidCardException{
        String stmType = node.getNodeName();
        if (!node.hasChildNodes()){
            throw new InvalidCardException("[STATEMENT PARSER]Rule statement empty");
        }
        NodeList children = node.getChildNodes();
        List<Node> trueChildren = new LinkedList<>();
        for(int i=0;i<children.getLength();i++){
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) { //Skipping spaces and return characters
                trueChildren.add(children.item(i));
            }
        }
        if (trueChildren.size() != 3){
            throw new InvalidCardException("[STATEMENT PARSER]A rule statement must contain 3 tags (subject, verb, object)");
        }
        //Parse statement
        String subject = trueChildren.get(0).getTextContent();
        String verb = trueChildren.get(1).getTextContent();
        String object = trueChildren.get(2).getTextContent();
        try{
            StatementType statementType = StatementType.valueByContent(stmType);
            StatementVerbType verbType = StatementVerbType.valueOf(verb);
            return new RuleStatementImpl(statementType,subject,verbType,object);
        }catch (IllegalArgumentException ex){
            throw  new InvalidCardException("[STATEMENT PARSER]Rule statement contains not supported items");
        }catch (NullPointerException ex){
            throw new InvalidCardException("[STATEMENT PARSER]Cannot read tags in statement");
        }
    }

    private static RuleEffectImpl nodeToRuleEffect(Element effectElement) throws InvalidCardException{
        //Parse effect type
        EffectType effectType;
        NodeList nList = effectElement.getElementsByTagName("type");
        if (nList.getLength() != 1){
            throw new InvalidCardException("[EFFECT PARSER]Missing/Multiple tag type");
        }
        try{
            effectType = EffectType.valueOf(nList.item(0).getTextContent());
        }catch (IllegalArgumentException ex){
            throw  new InvalidCardException("[EFFECT PARSER]Effect type '" + nList.item(0).getTextContent() + "' not supported");
        }catch (NullPointerException ex){
            throw new InvalidCardException("[EFFECT PARSER]Cannot read tag effect");
        }

        //Parse tag allow subtype if present
        AllowType allowType = null;
        nList = effectElement.getElementsByTagName("subtype");
        if (nList.getLength() == 1){
            try{
                allowType = AllowType.valueOf(nList.item(0).getTextContent());
            }catch (IllegalArgumentException ex){
                throw  new InvalidCardException("[EFFECT PARSER]Allow subtype '" + nList.item(0).getTextContent() + "' not supported");
            }catch (NullPointerException ex){
                throw new InvalidCardException("[EFFECT PARSER]Cannot read tag effect");
            }
        }else if (nList.getLength() > 1){
            throw new InvalidCardException("[EFFECT PARSER]Multiple tag subtype");
        }

        //Parse tag data if present
        String effectData = null;
        nList = effectElement.getElementsByTagName("data");
        if (nList.getLength() == 1){
            effectData = nList.item(0).getTextContent();
        }else if (nList.getLength() > 1){
            throw new InvalidCardException("[EFFECT PARSER]Multiple tag data");
        }

        //Parse tag next state
        PlayerState nextState = null;
        nList = effectElement.getElementsByTagName("nextstate");
        if (nList.getLength() == 1){
            try{
                nextState = PlayerState.valueOf(nList.item(0).getTextContent());
            }catch (IllegalArgumentException ex){
                throw  new InvalidCardException("[EFFECT PARSER]Player state '" + nList.item(0).getTextContent() + "' unknown");
            }catch (NullPointerException ex){
                throw new InvalidCardException("[EFFECT PARSER]Cannot read tag nextstate");
            }
        }else if (nList.getLength() > 1){
            throw new InvalidCardException("[EFFECT PARSER]Multiple tag nextstate");
        }

        //Return Rule Effect
        return new RuleEffectImpl(effectType, allowType, nextState, effectData);
    }
}
