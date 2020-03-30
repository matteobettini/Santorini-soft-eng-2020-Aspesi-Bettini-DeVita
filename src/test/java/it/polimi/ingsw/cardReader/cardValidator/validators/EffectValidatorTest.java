package it.polimi.ingsw.cardReader.cardValidator.validators;

import it.polimi.ingsw.cardReader.RuleEffect;
import it.polimi.ingsw.cardReader.cardValidator.exceptions.InvalidRuleEffectException;
import it.polimi.ingsw.cardReader.enums.EffectType;
import it.polimi.ingsw.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

class EffectValidatorTest {

    /**
     * Test ALLOW with null data
     */
    @Test
    void testAllowNullData(){
        RuleEffect allowOK = new RuleEffect(EffectType.ALLOW, PlayerState.UNKNOWN, null);
        try{
            EffectValidator.checkRuleEffect(allowOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        allowOK = new RuleEffect(EffectType.ALLOW, PlayerState.TURN_STARTED, null);
        try{
            EffectValidator.checkRuleEffect(allowOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
    }

    /**
     * Test ALLOW with not null data
     */
    @Test
    void testAllowNotNullData(){
        String data = "PUSH";

        RuleEffect allowWrong = new RuleEffect(EffectType.ALLOW, PlayerState.UNKNOWN, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        allowWrong = new RuleEffect(EffectType.ALLOW, PlayerState.TURN_STARTED, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test DENY with null data
     */
    @Test
    void testDenyNullData(){
        RuleEffect allowOK = new RuleEffect(EffectType.DENY, PlayerState.UNKNOWN, null);
        try{
            EffectValidator.checkRuleEffect(allowOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        RuleEffect allowWrong = new RuleEffect(EffectType.DENY, PlayerState.TURN_STARTED, null);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test DENY with not null data
     */
    @Test
    void testDenyNotNullData(){
        String data = "SWAP";

        RuleEffect allowWrong = new RuleEffect(EffectType.DENY, PlayerState.UNKNOWN, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        allowWrong = new RuleEffect(EffectType.DENY, PlayerState.TURN_STARTED, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test WIN with null data
     */
    @Test
    void testWinNullData(){
        RuleEffect allowOK = new RuleEffect(EffectType.WIN, PlayerState.UNKNOWN, null);
        try{
            EffectValidator.checkRuleEffect(allowOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        RuleEffect allowWrong = new RuleEffect(EffectType.WIN, PlayerState.TURN_STARTED, null);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test WIN with not null data
     */
    @Test
    void testWinNotNullData(){
        String data = "WW";

        RuleEffect allowWrong = new RuleEffect(EffectType.WIN, PlayerState.UNKNOWN, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        allowWrong = new RuleEffect(EffectType.WIN, PlayerState.TURN_STARTED, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test SET_OPPONENT_POSITION with null data
     */
    @Test
    void testSetOpponentPositionNullData(){
        RuleEffect allowWrong = new RuleEffect(EffectType.SET_OPPONENT_POSITION, PlayerState.UNKNOWN, null);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        allowWrong = new RuleEffect(EffectType.SET_OPPONENT_POSITION, PlayerState.TURN_STARTED, null);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test SET_OPPONENT_POSITION with not null data
     */
    @Test
    void testSetOpponentPosition(){
        //Test with data tag with correct data
        RuleEffect effectOkay = new RuleEffect(EffectType.SET_OPPONENT_POSITION, PlayerState.UNKNOWN, "SWAP");
        try{
            EffectValidator.checkRuleEffect(effectOkay);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        effectOkay = new RuleEffect(EffectType.SET_OPPONENT_POSITION, PlayerState.TURN_STARTED, "PUSH_STRAIGHT");
        try{
            EffectValidator.checkRuleEffect(effectOkay);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }

        //Test with data tag with empty data
        String emptyData = "";
        RuleEffect effectWrong = new RuleEffect(EffectType.SET_OPPONENT_POSITION, PlayerState.UNKNOWN, emptyData);
        try{
            EffectValidator.checkRuleEffect(effectWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }

        //Test with data tag with wrong data
        String wrongData = "PULL";
        effectWrong = new RuleEffect(EffectType.SET_OPPONENT_POSITION, PlayerState.UNKNOWN, wrongData);
        try{
            EffectValidator.checkRuleEffect(effectWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }
}