package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.cardReader.CardRule;
import it.polimi.ingsw.cardReader.RuleStatement;
import it.polimi.ingsw.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.List;

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

        assert (internalModel != null && cardRule != null && owner != null);

        List<LambdaStatement> compiledStatements = new ArrayList<>();
        LambdaEffect compiledEffect = null;
        TriggerType trigger = cardRule.getTrigger();

        for(RuleStatement s : cardRule.getStatements()){
            compiledStatements.add(StatementCompiler.compileStatement(internalModel, s, owner));
        }

        compiledEffect = EffectCompiler.compileEffect(internalModel, cardRule.getEffect());

        return new CompiledCardRule(compiledStatements, compiledEffect, trigger);

    }

}