package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.cardReader.RuleEffect;
import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;

/**
 * Translates Rule Effects into Compiled Effects
 */
public class EffectCompiler {

    /** Compiles the given effect
     * @param model the internal model is needed to incapsulate it in the lambdas
     * @param effect the effect to compile
     * @param owner the owner of the rule in which the effect is
     * @return the compiled effect
     */
    public static LambdaEffect compileEffect(InternalModel model, RuleEffect effect, Player owner) {
        // TODO implement here
        return null;
    }


    private static LambdaEffect compileMoveEffect(InternalModel model, RuleEffect effect, Player owner) {
        // TODO implement here
        return null;
    }


    private static LambdaEffect compileBuildEffect(InternalModel model, RuleEffect effect, Player owner) {
        // TODO implement here
        return null;
    }


    private static LambdaEffect compileSetOpponentPositionEffect(InternalModel model, RuleEffect effect, Player owner) {
        // TODO implement here
        return null;
    }

}