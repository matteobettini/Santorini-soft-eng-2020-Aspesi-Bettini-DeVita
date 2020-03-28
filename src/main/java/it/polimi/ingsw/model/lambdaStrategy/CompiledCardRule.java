package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;

import java.util.*;

/**
 * 
 */
public class CompiledCardRule {


    /**
     * 
     */
    private TriggerType trigger;

    /**
     * 
     */
    private List<LambdaStatement> statements;

    /**
     * 
     */
    private LambdaEffect effect;


    /**
     * Default constructor
     */
    public CompiledCardRule() {
    }





    /**
     * @param moveData 
     * @param buildData 
     * @param simulate 
     * @return
     */
    public boolean execute(MoveData moveData, BuildData buildData, boolean simulate) {
        // TODO implement here
        return false;
    }

    /**
     * @param moveData 
     * @param buildData 
     * @return
     */
    public boolean applyEffect(MoveData moveData, BuildData buildData) {
        // TODO implement here
        return false;
    }

}