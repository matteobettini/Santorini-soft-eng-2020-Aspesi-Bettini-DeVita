package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RuleEffectImplTest {
    /**
     * Verify that data provided is reachable via getters
     */
    @Test
    void testGetters() {
        EffectType typeT = EffectType.ALLOW;
        String element = "TEST";

        RuleEffect ruleEffect = new RuleEffectImpl(typeT,null,element);
        assertEquals(ruleEffect.getType(), typeT);
        assertNull(ruleEffect.getNextState());
        assertEquals(ruleEffect.getData(), element);
    }

    /**
     * Test nextState setter
     */
    @Test
    void testSetters(){
        EffectType typeT = EffectType.ALLOW;

        RuleEffectImpl ruleEffect = new RuleEffectImpl(typeT, null,null);
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

        RuleEffect ruleEffect = new RuleEffectImpl(typeT,null,null);
        assertEquals(ruleEffect.getType(), typeT);
        assertNull(ruleEffect.getNextState());
        assertNull(ruleEffect.getData());
    }

    /**
     * Verify equals and hashcode with null data
     */
    @Test
    void testEqualsAndHashNullData(){
        EffectType typeT = EffectType.ALLOW;

        RuleEffect ruleEffect1 = new RuleEffectImpl(typeT,null,null);
        RuleEffect ruleEffect2 = new RuleEffectImpl(typeT,null,null);

        assertEquals(ruleEffect1,ruleEffect2);
        assertEquals(ruleEffect1.hashCode(), ruleEffect2.hashCode());

        //With not null data
        RuleEffect ruleEffect3 = new RuleEffectImpl(typeT,null,"TEST");
        assertNotEquals(ruleEffect1,ruleEffect3);
    }
    /**
     * Verify equals and hashcode with not null data
     */
    @Test
    void testEqualsAndHash(){
        EffectType typeT = EffectType.ALLOW;

        //With data not null
        String data = "Test";
        RuleEffect ruleEffect1 = new RuleEffectImpl(typeT,null,data);
        RuleEffect ruleEffect2 = new RuleEffectImpl(typeT,null,data);
        assertEquals(ruleEffect1,ruleEffect2);
        assertEquals(ruleEffect1.hashCode(), ruleEffect2.hashCode());

        //With different
        RuleEffect ruleEffect3 = new RuleEffectImpl(typeT,null,"Test1");
        assertNotEquals(ruleEffect1,ruleEffect3);
    }

    /**
     * Helpers for other tests
     */
    public static RuleEffectImpl getRuleEffectWithData(){
        EffectType typeT = EffectType.SET_OPPONENT_POSITION;
        String element = "SWAP";

        return new RuleEffectImpl(typeT,null,element);
    }
    public static RuleEffectImpl getRuleEffectWithNullData(){
        EffectType typeT = EffectType.ALLOW;

        return new RuleEffectImpl(typeT,null,null);
    }
    public static RuleEffectImpl getRuleEffectWithWrongData(){
        EffectType typeT = EffectType.SET_OPPONENT_POSITION;

        return new RuleEffectImpl(typeT,null,null);
    }
    public static RuleEffectImpl getRuleEffect(EffectType effectType, PlayerState playerNextState, String data){
        return new RuleEffectImpl(effectType, playerNextState, data);
    }
}