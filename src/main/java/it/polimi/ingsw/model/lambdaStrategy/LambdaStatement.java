package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.model.turnInfoMatteo.BuildData;
import it.polimi.ingsw.model.turnInfoMatteo.MoveData;

/**
 * 
 */
@FunctionalInterface
public interface LambdaStatement {

    /**
     * @param moveData 
     * @param buildData 
     * @return
     */
    public boolean evaluate(MoveData moveData, BuildData buildData);

}