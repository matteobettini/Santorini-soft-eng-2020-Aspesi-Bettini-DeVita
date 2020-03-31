package it.polimi.ingsw.model.cardReader.cardValidator;

import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.CardFileTest;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import org.junit.jupiter.api.Test;

class CardValidatorTest {

    @Test
    void checkCardFile() {
        //Check correct CardFile
        CardFile cardFileOkay = CardFileTest.getNormalCardFile();
        try{
            CardValidator.checkCardFile(cardFileOkay);
            assert true;
        } catch (InvalidCardException e) {
            assert false;
        }
        //Check CardFile with at least one rule with one statement with wrong subject
        CardFile cardFileWrong = CardFileTest.getCardFileWithWrongStatementSubject();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
        //Check CardFile with at least one rule with one statement with wrong object
        cardFileWrong = CardFileTest.getCardFileWithWrongStatementObject();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
        //Check CardFile with at least one rule with one effect with wrong data
        cardFileWrong = CardFileTest.getCardFileWithWrongEffect();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
        //Check CardFile with at least one rule with mixed statements
        cardFileWrong = CardFileTest.getCardFileWithMixedStatementsOnMove();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
        cardFileWrong = CardFileTest.getCardFileWithMixedStatementsOnBuild();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
    }
}