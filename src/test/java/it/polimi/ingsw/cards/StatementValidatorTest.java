package it.polimi.ingsw.cards;

import it.polimi.ingsw.cards.exceptions.InvalidStatementObjectException;
import it.polimi.ingsw.cards.exceptions.InvalidStatementSubjectException;
import it.polimi.ingsw.cards.enums.StatementVerbType;
import org.junit.jupiter.api.Test;

class StatementValidatorTest {

    /**
     * Test PLAYER_EQUALS
     * - Normal
     */
    @Test
    void testPlayerEquals() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.PLAYER_EQUALS, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.PLAYER_EQUALS, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test STATE_EQUALS
     * - Normal
     */
    @Test
    void testStateEquals() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.STATE_EQUALS, "TURN_STARTED");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.STATE_EQUALS, "MOVED");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.STATE_EQUALS, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.STATE_EQUALS, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test HAS_FLAG
     * - Normal
     */
    @Test
    void testHasFlag() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.HAS_FLAG, "MOVED_UP_ONCE");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.HAS_FLAG, "MOVED_UP_ONCE");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("CARD_OWNER", StatementVerbType.HAS_FLAG, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.HAS_FLAG, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test MOVE_LENGTH
     * - Normal
     */
    @Test
    void testMoveLength() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.MOVE_LENGTH, "1");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.MOVE_LENGTH, "1");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.MOVE_LENGTH, "0");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check correct subject and wrong object
        stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.MOVE_LENGTH, "-1");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check correct subject and wrong object (not a number)
        stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.MOVE_LENGTH, "XX");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.MOVE_LENGTH, "-2");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test EXISTS_DELTA_MORE
     * - Normal
     */
    @Test
    void testExistsDeltaMore() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.EXISTS_DELTA_MORE, "1");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and object
        stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.EXISTS_DELTA_MORE, "0");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and object
        stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.EXISTS_DELTA_MORE, "-1");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.EXISTS_DELTA_MORE, "1");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.EXISTS_DELTA_MORE, "NOT_NUMBER");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.EXISTS_DELTA_MORE, "NaN");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test EXISTS_DELTA_LESS
     * - Normal
     */
    @Test
    void testExistsDeltaLess() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.EXISTS_DELTA_LESS, "1");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and object
        stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.EXISTS_DELTA_LESS, "0");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and object
        stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.EXISTS_DELTA_LESS, "-1");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.EXISTS_DELTA_LESS, "1");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.EXISTS_DELTA_LESS, "NOT_NUMBER");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.EXISTS_DELTA_LESS, "NaN");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test LEVEL_TYPE
     * - Normal
     */
    @Test
    void testLevelType() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("START_POSITION", StatementVerbType.LEVEL_TYPE, "GROUND");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.LEVEL_TYPE, "GROUND");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("FINAL_POSITION", StatementVerbType.LEVEL_TYPE, "WRONG_LEVEL");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.LEVEL_TYPE, "WRONG_LEVEL");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test INTERACTION_NUM
     * - Normal
     */
    @Test
    void testInteractionNum() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.INTERACTION_NUM, "1");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.INTERACTION_NUM, "0");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.INTERACTION_NUM, "1");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.INTERACTION_NUM, "-1");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check correct subject and wrong object (not a number)
        stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.INTERACTION_NUM, "XX");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.INTERACTION_NUM, "-2");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test POSITION_EQUALS
     * - Normal
     */
    @Test
    void testPositionEquals() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "START_POSITION");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.POSITION_EQUALS, "OPPONENTS");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.POSITION_EQUALS, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test BUILD_NUM
     * - Normal
     */
    @Test
    void testBuildNum() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.BUILD_NUM, "1");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.BUILD_NUM, "1");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.BUILD_NUM, "0");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.BUILD_NUM, "-1");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.BUILD_NUM, "XX");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.BUILD_NUM, "NaN");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test BUILD_DOME
     * - Normal
     */
    @Test
    void testBuildDome() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.BUILD_DOME, "FIRST_FLOOR");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.BUILD_DOME, "FIRST_FLOOR");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.BUILD_DOME, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.BUILD_DOME, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test BUILD_DOME_EXCEPT
     * - Normal
     */
    @Test
    void testBuildExceptDome() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.BUILD_DOME_EXCEPT, "FIRST_FLOOR");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.BUILD_DOME_EXCEPT, "FIRST_FLOOR");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.BUILD_DOME_EXCEPT, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.BUILD_DOME_EXCEPT, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }

    /**
     * Test BUILD_IN_SAME_SPOT
     * - Normal
     */
    @Test
    void testBuildInSameSpot() {
        //Check correct subject and object
        RuleStatement stmOkay = RuleStatementImplTest.getStatement("YOU", StatementVerbType.BUILD_IN_SAME_SPOT, "ALL");
        try {
            StatementValidator.checkRuleStatement(stmOkay);
            assert true;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e) {
            assert false;
        }
        //Check wrong subject and correct object
        RuleStatement stmWrongSubject = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.BUILD_IN_SAME_SPOT, "ALL");
        try {
            StatementValidator.checkRuleStatement(stmWrongSubject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert true;
        }catch (InvalidStatementObjectException e) {
            assert false;
        }
        //Check correct subject and wrong object
        RuleStatement stmWrongObject = RuleStatementImplTest.getStatement("YOU", StatementVerbType.BUILD_IN_SAME_SPOT, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrongObject);
            assert false;
        } catch (InvalidStatementSubjectException e){
            assert false;
        }catch (InvalidStatementObjectException e) {
            assert true;
        }
        //Check wrong subject and wrong object
        RuleStatement stmWrong = RuleStatementImplTest.getStatement("WRONG_SUBJECT", StatementVerbType.BUILD_IN_SAME_SPOT, "WRONG_OBJECT");
        try {
            StatementValidator.checkRuleStatement(stmWrong);
            assert false;
        } catch (InvalidStatementSubjectException | InvalidStatementObjectException e){
            assert true;
        }
    }
}