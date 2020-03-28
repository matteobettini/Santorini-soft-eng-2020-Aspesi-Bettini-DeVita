package it.polimi.ingsw.cardReader.helpers;

import it.polimi.ingsw.cardReader.RuleEffect;
import it.polimi.ingsw.cardReader.enums.EffectType;
import it.polimi.ingsw.model.enums.PlayerState;
import org.w3c.dom.Element;

public class EffectHelper {

    public static RuleEffect getRuleEffect(){
        EffectType typeT = EffectType.ALLOW;
        PlayerState stateT = PlayerState.UNKNOWN;
        Element element = XMLHelper.getElement();

        return new RuleEffect(typeT,stateT,element);
    }
}
