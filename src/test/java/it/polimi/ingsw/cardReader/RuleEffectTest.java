package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.EffectType;
import it.polimi.ingsw.cardReader.helpers.XMLHelper;
import it.polimi.ingsw.model.enums.PlayerState;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.*;

class RuleEffectTest {
    /**
     * Verify that data provided is reachable via getters
     */
    @Test
    void testGetters() {
        EffectType typeT = EffectType.ALLOW;
        PlayerState stateT = PlayerState.UNKNOWN;
        Element element = XMLHelper.getElement();

        RuleEffect ruleEffect = new RuleEffect(typeT,stateT,element);
        assertEquals(ruleEffect.getType(), typeT);
        assertEquals(ruleEffect.getNextState(), stateT);
        assertEquals(ruleEffect.getData(), element);
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
    }
    /**
     * Verify equals and hashcode with not null data
     */
    @Test
    void testEqualsAndHash(){
        EffectType typeT = EffectType.ALLOW;
        PlayerState stateT = PlayerState.UNKNOWN;
        Element data = XMLHelper.getElement();

        RuleEffect ruleEffect1 = new RuleEffect(typeT,stateT,data);
        RuleEffect ruleEffect2 = new RuleEffect(typeT,stateT,data);

        assertEquals(ruleEffect1,ruleEffect2);
        assertEquals(ruleEffect1.hashCode(), ruleEffect2.hashCode());
    }

}