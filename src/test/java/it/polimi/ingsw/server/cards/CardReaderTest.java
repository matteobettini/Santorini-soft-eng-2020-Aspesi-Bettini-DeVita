package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

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
        try{
            List<CardFile> card = CardReader.readCards(defaultFile, "/test.xml");
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
        try{
            List<CardFile> card = CardReader.readCards(defaultFile, "/ExampleCards/ValidCardWithEffectData.xml");
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
        try{
            List<CardFile> card = CardReader.readCards(defaultFile, "/ExampleCards/ValidCard.xml");
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
        try{
            List<CardFile> card = CardReader.readCards(defaultFile, "/ExampleCards/InvalidSyntaxCard.xml");
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
        try{
            List<CardFile> card = CardReader.readCards(defaultFile, "/ExampleCards/InvalidSyntaxCardNoDTD.xml");
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
        try{
            List<CardFile> card = CardReader.readCards(defaultFile, "/ExampleCards/InvalidSyntaxCardInvalidDTD.xml");
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
    }
}