package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.exceptions.InvalidRuleEffectException;
import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

class EffectValidatorTest {

    /**
     * Test ALLOW with null data
     */
    @Test
    void testAllowNullData(){
        RuleEffect allowOK = new RuleEffectImpl(EffectType.ALLOW, null, null);
        try{
            EffectValidator.checkRuleEffect(allowOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        allowOK = new RuleEffectImpl(EffectType.ALLOW, PlayerState.TURN_STARTED, null);
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

        RuleEffect allowWrong = new RuleEffectImpl(EffectType.ALLOW, null, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        allowWrong = new RuleEffectImpl(EffectType.ALLOW, PlayerState.TURN_STARTED, data);
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
        RuleEffect allowOK = new RuleEffectImpl(EffectType.DENY, null, null);
        try{
            EffectValidator.checkRuleEffect(allowOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        RuleEffect allowWrong = new RuleEffectImpl(EffectType.DENY, PlayerState.TURN_STARTED, null);
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

        RuleEffect allowWrong = new RuleEffectImpl(EffectType.DENY, null, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        allowWrong = new RuleEffectImpl(EffectType.DENY, PlayerState.TURN_STARTED, data);
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
        RuleEffect allowOK = new RuleEffectImpl(EffectType.WIN, null, null);
        try{
            EffectValidator.checkRuleEffect(allowOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        RuleEffect allowWrong = new RuleEffectImpl(EffectType.WIN, PlayerState.TURN_STARTED, null);
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

        RuleEffect allowWrong = new RuleEffectImpl(EffectType.WIN, null, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        allowWrong = new RuleEffectImpl(EffectType.WIN, PlayerState.TURN_STARTED, data);
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
        RuleEffect allowWrong = new RuleEffectImpl(EffectType.SET_OPPONENT_POSITION, null, null);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        allowWrong = new RuleEffectImpl(EffectType.SET_OPPONENT_POSITION, PlayerState.TURN_STARTED, null);
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
        RuleEffect effectOkay = new RuleEffectImpl(EffectType.SET_OPPONENT_POSITION, null, "SWAP");
        try{
            EffectValidator.checkRuleEffect(effectOkay);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        effectOkay = new RuleEffectImpl(EffectType.SET_OPPONENT_POSITION, PlayerState.TURN_STARTED, "PUSH_STRAIGHT");
        try{
            EffectValidator.checkRuleEffect(effectOkay);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }

        //Test with data tag with empty data
        String emptyData = "";
        RuleEffect effectWrong = new RuleEffectImpl(EffectType.SET_OPPONENT_POSITION, null, emptyData);
        try{
            EffectValidator.checkRuleEffect(effectWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }

        //Test with data tag with wrong data
        String wrongData = "PULL";
        effectWrong = new RuleEffectImpl(EffectType.SET_OPPONENT_POSITION, null, wrongData);
        try{
            EffectValidator.checkRuleEffect(effectWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }
}