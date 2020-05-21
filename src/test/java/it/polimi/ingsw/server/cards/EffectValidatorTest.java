package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.cards.enums.AllowType;
import it.polimi.ingsw.server.cards.exceptions.InvalidRuleEffectException;
import it.polimi.ingsw.server.cards.enums.EffectType;
import it.polimi.ingsw.server.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

class EffectValidatorTest {

    /**
     * Test ALLOW with null data
     */
    @Test
    void testAllowStandardNullData(){
        RuleEffect allowOK = new RuleEffectImpl(EffectType.ALLOW, AllowType.STANDARD,null, null);
        try{
            EffectValidator.checkRuleEffect(allowOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        allowOK = new RuleEffectImpl(EffectType.ALLOW, AllowType.STANDARD, PlayerState.TURN_STARTED, null);
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
    void testAllowStandardNotNullData(){
        String data = "PUSH";

        RuleEffect allowWrong = new RuleEffectImpl(EffectType.ALLOW, AllowType.STANDARD,null, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        allowWrong = new RuleEffectImpl(EffectType.ALLOW, AllowType.STANDARD, PlayerState.TURN_STARTED, data);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test BUILD_UNDER with null data
     */
    @Test
    void testBuildUnderWithNullData(){
        RuleEffect buildUnderOK = new RuleEffectImpl(EffectType.ALLOW, AllowType.BUILD_UNDER,null, null);
        try{
            EffectValidator.checkRuleEffect(buildUnderOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        buildUnderOK = new RuleEffectImpl(EffectType.ALLOW, AllowType.BUILD_UNDER, PlayerState.TURN_STARTED, null);
        try{
            EffectValidator.checkRuleEffect(buildUnderOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
    }

    /**
     * Test BUILD_UNDER with not null data
     */
    @Test
    void testBuildUnderWithNotNullData(){
        String data = "SWAP";

        RuleEffect buildUnderWrong = new RuleEffectImpl(EffectType.ALLOW, AllowType.BUILD_UNDER,null, data);
        try{
            EffectValidator.checkRuleEffect(buildUnderWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        buildUnderWrong = new RuleEffectImpl(EffectType.ALLOW, AllowType.BUILD_UNDER, PlayerState.TURN_STARTED, data);
        try{
            EffectValidator.checkRuleEffect(buildUnderWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test DENY with null data or allow subtype
     */
    @Test
    void testDenyNullData(){
        RuleEffect denyOK = new RuleEffectImpl(EffectType.DENY, null);
        try{
            EffectValidator.checkRuleEffect(denyOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        RuleEffect denyWrong = new RuleEffectImpl(EffectType.DENY, PlayerState.TURN_STARTED);
        try{
            EffectValidator.checkRuleEffect(denyWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        denyWrong = new RuleEffectImpl(EffectType.DENY, AllowType.STANDARD, PlayerState.TURN_STARTED, null);
        try{
            EffectValidator.checkRuleEffect(denyWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test DENY with not null data or allow subtype
     */
    @Test
    void testDenyNotNullData(){
        String data = "SWAP";

        RuleEffect denyWrong = new RuleEffectImpl(EffectType.DENY, null, null, data);
        try{
            EffectValidator.checkRuleEffect(denyWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        denyWrong = new RuleEffectImpl(EffectType.DENY, null,PlayerState.TURN_STARTED, data);
        try{
            EffectValidator.checkRuleEffect(denyWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        denyWrong = new RuleEffectImpl(EffectType.DENY, AllowType.SET_OPPONENT,PlayerState.TURN_STARTED, data);
        try{
            EffectValidator.checkRuleEffect(denyWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test WIN with null data or allow subtype
     */
    @Test
    void testWinNullData(){
        RuleEffect winOK = new RuleEffectImpl(EffectType.WIN, null);
        try{
            EffectValidator.checkRuleEffect(winOK);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        RuleEffect winWrong = new RuleEffectImpl(EffectType.WIN, PlayerState.TURN_STARTED);
        try{
            EffectValidator.checkRuleEffect(winWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        winWrong = new RuleEffectImpl(EffectType.WIN, AllowType.STANDARD, PlayerState.TURN_STARTED, null);
        try{
            EffectValidator.checkRuleEffect(winWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test WIN with not null data or allow subtype
     */
    @Test
    void testWinNotNullData(){
        String data = "WW";

        RuleEffect winWrong = new RuleEffectImpl(EffectType.WIN, null,null, data);
        try{
            EffectValidator.checkRuleEffect(winWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        winWrong = new RuleEffectImpl(EffectType.WIN, null, PlayerState.TURN_STARTED, data);
        try{
            EffectValidator.checkRuleEffect(winWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        winWrong = new RuleEffectImpl(EffectType.WIN, AllowType.STANDARD, PlayerState.TURN_STARTED, data);
        try{
            EffectValidator.checkRuleEffect(winWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }

    /**
     * Test SET_OPPONENT with null data
     */
    @Test
    void testAllowSetOpponentNullData(){
        RuleEffect allowWrong = new RuleEffectImpl(EffectType.ALLOW, AllowType.SET_OPPONENT,null, null);
        try{
            EffectValidator.checkRuleEffect(allowWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
        allowWrong = new RuleEffectImpl(EffectType.ALLOW, AllowType.SET_OPPONENT, PlayerState.TURN_STARTED, null);
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
        RuleEffect effectOkay = new RuleEffectImpl(EffectType.ALLOW, AllowType.SET_OPPONENT,null, "SWAP");
        try{
            EffectValidator.checkRuleEffect(effectOkay);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }
        effectOkay = new RuleEffectImpl(EffectType.ALLOW, AllowType.SET_OPPONENT, PlayerState.TURN_STARTED, "PUSH_STRAIGHT");
        try{
            EffectValidator.checkRuleEffect(effectOkay);
            assert true;
        } catch (InvalidRuleEffectException e) {
            assert false;
        }

        //Test with data tag with empty data
        String emptyData = "";
        RuleEffect effectWrong = new RuleEffectImpl(EffectType.ALLOW, AllowType.SET_OPPONENT, null, emptyData);
        try{
            EffectValidator.checkRuleEffect(effectWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }

        //Test with data tag with wrong data
        String wrongData = "PULL";
        effectWrong = new RuleEffectImpl(EffectType.ALLOW, AllowType.SET_OPPONENT, null, wrongData);
        try{
            EffectValidator.checkRuleEffect(effectWrong);
            assert false;
        } catch (InvalidRuleEffectException e) {
            assert true;
        }
    }
}