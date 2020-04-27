package it.polimi.ingsw.cards;

import it.polimi.ingsw.cards.exceptions.InvalidCardException;
import org.junit.jupiter.api.Test;

import java.io.File;

class CardReaderTest {

    /*
        Testing after adding DTD validating
     */
    /**
     * Test with wrong file path
     */
    @Test
    void testWrongFilePath(){
        CardFile defaultFile = CardPatcherTest.getDefaultStrategyExample();
        File cardFile = new File("src/test.xml");
        try{
            CardFile card = CardReader.readCard(defaultFile, cardFile);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
    }

    /**
     * Test valid card with effect data
     */
    @Test
    void testValidWithEffectData() {
        CardFile defaultFile = CardPatcherTest.getDefaultStrategyExample();
        File cardFile = new File("src/test/resources/ExampleCards/ValidCardWithEffectData.xml");
        try{
            CardFile card = CardReader.readCard(defaultFile, cardFile);
        } catch (InvalidCardException e) {
            assert false;
        }
    }

    /**
     * Test valid card with no effect data
     */
    @Test
    void testValidWithNoEffectData() {
        CardFile defaultFile = CardPatcherTest.getDefaultStrategyExample();
        File cardFile = new File("src/test/resources/ExampleCards/ValidCard.xml");
        try{
            CardFile card = CardReader.readCard(defaultFile, cardFile);
        } catch (InvalidCardException e) {
            assert false;
        }
    }

    /**
     * Test card with syntax errors
     */
    @Test
    void testInvalidSyntax() {
        CardFile defaultFile = CardPatcherTest.getDefaultStrategyExample();
        File cardFile = new File("src/test/resources/ExampleCards/InvalidSyntaxCard.xml");
        try{
            CardFile card = CardReader.readCard(defaultFile, cardFile);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
    }

    /**
     * Test card with invalid syntax and no DTD
     */
    @Test
    void testInvalidSyntaxWithoutDTD(){
        CardFile defaultFile = CardPatcherTest.getDefaultStrategyExample();
        File cardFile = new File("src/test/resources/ExampleCards/InvalidSyntaxCardNoDTD.xml");
        try{
            CardFile card = CardReader.readCard(defaultFile, cardFile);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
    }

    /**
     * Test card with invalid syntax and modified DTD
     */
    @Test
    void testInvalidSyntaxInvalidDTD(){
        CardFile defaultFile = CardPatcherTest.getDefaultStrategyExample();
        File cardFile = new File("src/test/resources/ExampleCards/InvalidSyntaxCardInvalidDTD.xml");
        try{
            CardFile card = CardReader.readCard(defaultFile, cardFile);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
    }
}