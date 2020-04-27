package it.polimi.ingsw.model;


import it.polimi.ingsw.model.exceptions.PlayerLostSignal;
import it.polimi.ingsw.model.exceptions.PlayerWonSignal;
import it.polimi.ingsw.model.BuildData;
import it.polimi.ingsw.model.MoveData;

/**
 * Thi is a lmabda function that encapsulates the logic of the effect of a certain rule.
 * It carries out the move on the board by setting new workers' positions or adding buildings.
 * It can be both the effect of a move rule or of a build rule.
 */
@FunctionalInterface
interface LambdaEffect {

    /**
     * This method applies the effect to the internal model
     * @param moveData if it is a move effect, this parameter will contain the associated data otherwise it will be null
     * @param buildData if it is a build effect, this parameter will contain the associated data otherwise it will be null
     * @param simulate if true gives the possibility to simulate the effect without modifying the internal model
     * @return returns true if the effect is consistent and false if it violates board consistency
     */
    boolean apply(MoveData moveData, BuildData buildData, boolean simulate) throws PlayerWonSignal, PlayerLostSignal;
}