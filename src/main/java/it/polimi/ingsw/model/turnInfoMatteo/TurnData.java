package it.polimi.ingsw.model.turnInfoMatteo;

import it.polimi.ingsw.model.Player;


/**
 * 
 */
public abstract class TurnData {

    /**
     * 
     */
    private final Player player;

    /**
     * 
     */
    private final Worker worker;


    /**
     * Default constructor
     */
    public TurnData(Player player, Worker worker) {
        this.player = player;
        this.worker = worker;
    }


    /**
     * @return
     */
    public Player getPlayer() {
        // TODO implement here
        return null;
    }

    /**
     * @return
     */
    public Worker getWorker() {
        // TODO implement here
        return null;
    }



}