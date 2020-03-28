package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;

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