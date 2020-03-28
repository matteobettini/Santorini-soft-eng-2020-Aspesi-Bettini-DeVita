package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.cardReader.CardRule;
import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;

/**
 *  Translates a CardRule into a CompiledCardRule
 */
public class RuleCompiler {


    /**
     * Compiles the CardRule using the StatementCompiler and EffectCompiler
     * @param internalModel the internal model is needed to incapsulate it in the lambdas
     * @param cardRule the rule to be compiled
     * @param owner the owner of the rule to be compiled
     * @return the compiled rule
     **/
    public static CompiledCardRule compile(InternalModel internalModel, CardRule cardRule, Player owner){
        return null;
    }

}