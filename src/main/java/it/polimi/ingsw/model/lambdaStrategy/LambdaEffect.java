package it.polimi.ingsw.model.lambdaStrategy;


import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;

/**
 * 
 */
@FunctionalInterface
public interface LambdaEffect {

    /**
     * @param moveData 
     * @param buildData 
     * @param simulate 
     * @return
     */
    public boolean apply(MoveData moveData, BuildData buildData, boolean simulate);

}