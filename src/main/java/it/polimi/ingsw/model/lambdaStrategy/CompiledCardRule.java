package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerLostSignal;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerWonSignal;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;

import java.util.*;

/**
 * This is a compiled car rule, it is the same as a normal card rule but has some differencies:
 * It is sepcific of a given player and istead of rule statements and rule effects it has compiled ones.
 * It represents a certain rule pf a specific card and has to be run every turn checking the consistancy
 * of the turn itself.
 * 
 */
public class CompiledCardRule {



    /**
     * The type of the rule: it can be a BUILD rule or a MOVE rule
     */
    private final TriggerType trigger;

    /**
     * The type of the effect: ALLOW, SET_OPPONENT_POSITION, DENY, WIN
     */
    private final LambdaEffect effect;

    /**
     * A list of all the compiled statements that belong to the rule
     */
    private final List<LambdaStatement> statements;




    /**
     * The constructor of the class
     */
    public CompiledCardRule(List<LambdaStatement> lambdaStatements, LambdaEffect lambdaEffect, TriggerType trigger) {
        assert (lambdaStatements != null && lambdaEffect != null && trigger != null);
        this.effect = lambdaEffect;
        this.statements = lambdaStatements;
        this.trigger = trigger;
    }



    /**
     * Execute all the statements and the effect of the rule either
     * in simulation mode or in normal mode
     * @param moveData if it is a move rule, this parameter will contain the associated data otherwise it will be null
     * @param buildData if it is a build rule, this parameter will contain the associated data otherwise it will be null
     * @param simulate this flag enables the to simulate the whole execution of the rule without changing the internal model
     * @return returns true if the statements are all valid or false if it at least one fails
     */
    public boolean execute(MoveData moveData, BuildData buildData, boolean simulate) throws PlayerLostSignal, PlayerWonSignal {

        assert (this.trigger == TriggerType.MOVE ? buildData == null && moveData != null : moveData == null && buildData != null);

        boolean result = true;

        for(LambdaStatement s : statements) {
            result = s.evaluate(moveData, buildData);
            if(!result)
                break;
        }

        result = result && effect.apply(moveData, buildData, simulate);

        return result;
    }

    /**
     * Only applies the effect of the rule
     * @param moveData if it is a move effect, this parameter will contain the associated data otherwise it will be null
     * @param buildData if it is a build effect, this parameter will contain the associated data otherwise it will be null
     * @return returns true if the effect has been applied or false if it has failes
     */
    public boolean applyEffect(MoveData moveData, BuildData buildData) throws PlayerLostSignal, PlayerWonSignal {

        assert (this.trigger == TriggerType.MOVE ? buildData == null && moveData != null : moveData == null && buildData != null);


        return effect.apply(moveData, buildData, false);
    }

    /**
     * getter for the trigger type of the rule: BUILD, MOVE
     * @return the trigger type
     */
    public TriggerType getTrigger() {
        return trigger;
    }
}