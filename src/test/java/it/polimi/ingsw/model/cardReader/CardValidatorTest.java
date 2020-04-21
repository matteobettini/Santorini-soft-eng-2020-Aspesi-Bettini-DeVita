package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import org.junit.jupiter.api.Test;

class CardValidatorTest {

    /*
        Test1: Check correct CardFile
     */
    @Test
    void testCorrectCardFile(){
        CardFile cardFileOkay = CardFileImplTest.getNormalCardFile();
        try{
            CardValidator.checkCardFile(cardFileOkay);
            assert true;
        } catch (InvalidCardException e) {
            assert false;
        }
    }
    /*
        Test2: Check CardFile with at least one rule with one statement with wrong subject
     */
    @Test
    void checkWrongSubject(){
        CardFile cardFileWrong = CardFileImplTest.getCardFileWithWrongStatementSubject();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
    }
    /*
        Test3: Check CardFile with at least one rule with one statement with wrong object
     */
    @Test
    void checkWrongObject(){
        CardFile cardFileWrong = CardFileImplTest.getCardFileWithWrongStatementObject();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
    }
    /*
        Test4: Check CardFile with at least one rule with one effect with wrong data
     */
    @Test
    void checkWrongData(){
        CardFile cardFileWrong = CardFileImplTest.getCardFileWithWrongEffect();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
    }
    /*
        Test5: Check CardFile with at least one rule with mixed statements
     */
    @Test
    void checkMixedStatements(){
        CardFile cardFileWrong = CardFileImplTest.getCardFileWithMixedStatementsOnMove();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
        cardFileWrong = CardFileImplTest.getCardFileWithMixedStatementsOnBuild();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
    }

    /*
       Test6: Check CardFile with at least one rule with mixed allow subtypes
    */
    @Test
    void checkMixedAllowSubtypes() {
        CardFile cardFileWrong = CardFileImplTest.getCardFileWithMixedAllowSubtypesOnBuild();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            System.out.println(e.getMessage());
            assert true;
        }
    }
}