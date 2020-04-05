package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import org.junit.jupiter.api.Test;

class CardValidatorTest {

    @Test
    void checkCardFile() {
        //Check correct CardFile
        CardFile cardFileOkay = CardFileImplTest.getNormalCardFile();
        try{
            CardValidator.checkCardFile(cardFileOkay);
            assert true;
        } catch (InvalidCardException e) {
            assert false;
        }
        //Check CardFile with at least one rule with one statement with wrong subject
        CardFile cardFileWrong = CardFileImplTest.getCardFileWithWrongStatementSubject();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
        //Check CardFile with at least one rule with one statement with wrong object
        cardFileWrong = CardFileImplTest.getCardFileWithWrongStatementObject();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
        //Check CardFile with at least one rule with one effect with wrong data
        cardFileWrong = CardFileImplTest.getCardFileWithWrongEffect();
        try{
            CardValidator.checkCardFile(cardFileWrong);
            assert false;
        } catch (InvalidCardException e) {
            assert true;
        }
        //Check CardFile with at least one rule with mixed statements
        cardFileWrong = CardFileImplTest.getCardFileWithMixedStatementsOnMove();
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
}