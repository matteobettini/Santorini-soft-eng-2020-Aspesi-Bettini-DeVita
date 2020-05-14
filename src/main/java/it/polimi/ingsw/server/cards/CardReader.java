package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.cards.enums.*;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.model.enums.PlayerState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class allows to read a card from a file.
 * The card returned (in form of CardFile) is completely checked syntactically and semantically.
 *
 * Syntax exceptions are maintained even if DTD syntax checking was introduced.
 * Checks were maintained anyway in order to be fully covered from external DTD manipulation, during reading phase.
 */
class CardReader {

    /**
     * Read a card from an XML file
     * @param defaultCard Default strategy card, in the form of CardFile
     * @param file File initialized with the path where the card's XML is placed
     * @return CardFile of the indicated card
     * @throws InvalidCardException If card has problems syntactically or semantically.
     *                              It's always indicated the cause as message
     */
    public static CardFile readCard(CardFile defaultCard, File file) throws InvalidCardException{
        assert (defaultCard != null && file != null);
        Document document;
        try{
            document = getXMLDocument(file);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new InvalidCardException("[XML PARSE][" + e.getClass().toString() + "]" + e.getMessage());
        }
        CardFileImpl cardFile = parseCard(document);
        CardValidator.checkCardFile(cardFile);
        CardPatcher.patchCard(defaultCard,cardFile);
        return cardFile;
    }

    /**
     * Read XML Document from file
     * @param cardFile File initialized with the path of the card
     * @return Document containing parsed XML data
     * @throws IOException If the file could not be read
     * @throws ParserConfigurationException If the parser finds invalid config
     * @throws SAXException If syntax check goes wrong
     */
    private static Document getXMLDocument(File cardFile) throws IOException, ParserConfigurationException, SAXException {
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
        Document document = builder.parse(cardFile);
        document.getDocumentElement().normalize();
        return document;
    }

    /**
     * Parse the CardFile from the document XML
     * @param xml Document to be parsed
     * @return CardFileImpl containing CardFile data
     * @throws InvalidCardException If Syntax error occurs during the process
     */
    private static CardFileImpl parseCard(Document xml) throws InvalidCardException {
        //Check root node
        Element root = xml.getDocumentElement();
        if (!root.getNodeName().equals("Card")){
            throw new InvalidCardException("[CARD PARSER]Wrong root tag");
        }

        //Get card name
        String cardName;
        NodeList nList = root.getElementsByTagName("name");
        if (nList.getLength() != 1){
            throw new InvalidCardException("[CARD PARSER]Missing/Multiple tag name");
        }
        cardName = nList.item(0).getTextContent().trim();
        if (cardName.length() == 0){
            throw new InvalidCardException("[CARD PARSER]Invalid card name");
        }

        //Get card description
        String cardDescription;
        nList = xml.getElementsByTagName("description");
        if (nList.getLength() != 1){
            throw new InvalidCardException("[CARD PARSER]Missing/Multiple tag description");
        }
        cardDescription = nList.item(0).getTextContent().trim();
        if (cardDescription.length() == 0){
            throw new InvalidCardException("[CARD PARSER]Invalid card description");
        }

        //Parse the rules
        List<CardRuleImpl> cardRules = extractCardRules(xml);
        return new CardFileImpl(cardName,cardDescription,cardRules);
    }

    private static List<CardRuleImpl> extractCardRules(Document xml) throws InvalidCardException{
        List<CardRuleImpl> rulesList = new LinkedList<>();

        //Get card rules
        NodeList nRules = xml.getElementsByTagName("rules");
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
