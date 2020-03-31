package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RuleEffectTest {
    /**
     * Verify that data provided is reachable via getters
     */
    @Test
    void testGetters() {
        EffectType typeT = EffectType.ALLOW;
        PlayerState stateT = PlayerState.UNKNOWN;
        String element = "TEST";

        RuleEffect ruleEffect = new RuleEffect(typeT,stateT,element);
        assertEquals(ruleEffect.getType(), typeT);
        assertEquals(ruleEffect.getNextState(), stateT);
        assertEquals(ruleEffect.getData(), element);
    }

    /**
     * Test nextState setter
     */
    @Test
    void testSetters(){
        EffectType typeT = EffectType.ALLOW;
        PlayerState stateT = PlayerState.UNKNOWN;

        RuleEffect ruleEffect = new RuleEffect(typeT,stateT,null);
        PlayerState nextState = PlayerState.TURN_STARTED;
        ruleEffect.setNextState(nextState);
        assertEquals(ruleEffect.getNextState(), nextState);
    }

    /**
     * Verify behaviour with null data
     */
    @Test
    void testNullData(){
        EffectType typeT = EffectType.ALLOW;
        PlayerState stateT = PlayerState.UNKNOWN;

        RuleEffect ruleEffect = new RuleEffect(typeT,stateT,null);
        assertEquals(ruleEffect.getType(), typeT);
        assertEquals(ruleEffect.getNextState(), stateT);
        assertNull(ruleEffect.getData());
    }

    /**
     * Verify equals and hashcode with null data
     */
    @Test
    void testEqualsAndHashNullData(){
        EffectType typeT = EffectType.ALLOW;
        PlayerState stateT = PlayerState.UNKNOWN;

        RuleEffect ruleEffect1 = new RuleEffect(typeT,stateT,null);
        RuleEffect ruleEffect2 = new RuleEffect(typeT,stateT,null);

        assertEquals(ruleEffect1,ruleEffect2);
        assertEquals(ruleEffect1.hashCode(), ruleEffect2.hashCode());

        //With not null data
        RuleEffect ruleEffect3 = new RuleEffect(typeT,stateT,"TEST");
        assertNotEquals(ruleEffect1,ruleEffect3);
    }
    /**
     * Verify equals and hashcode with not null data
     */
    @Test
    void testEqualsAndHash(){
        EffectType typeT = EffectType.ALLOW;
        PlayerState stateT = PlayerState.UNKNOWN;

        //With data not null
        String data = "Test";
        RuleEffect ruleEffect1 = new RuleEffect(typeT,stateT,data);
        RuleEffect ruleEffect2 = new RuleEffect(typeT,stateT,data);
        assertEquals(ruleEffect1,ruleEffect2);
        assertEquals(ruleEffect1.hashCode(), ruleEffect2.hashCode());

        //With different
        RuleEffect ruleEffect3 = new RuleEffect(typeT,stateT,"Test1");
        assertNotEquals(ruleEffect1,ruleEffect3);
    }

    /**
     * Helpers for other tests
     */
    public static RuleEffect getRuleEffectWithData(){
        EffectType typeT = EffectType.SET_OPPONENT_POSITION;
        PlayerState stateT = PlayerState.UNKNOWN;
        String element = "SWAP";

        return new RuleEffect(typeT,stateT,element);
    }
    public static RuleEffect getRuleEffectWithNullData(){
        EffectType typeT = EffectType.ALLOW;
        PlayerState stateT = PlayerState.UNKNOWN;

        return new RuleEffect(typeT,stateT,null);
    }
    public static RuleEffect getRuleEffectWithWrongData(){
        EffectType typeT = EffectType.SET_OPPONENT_POSITION;
        PlayerState stateT = PlayerState.UNKNOWN;

        return new RuleEffect(typeT,stateT,null);
    }
}