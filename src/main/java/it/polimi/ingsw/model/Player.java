package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.enums.PlayerFlag;
import it.polimi.ingsw.model.enums.PlayerState;

import java.util.*;
import java.util.List;

/**
 * This class contains the info about a Player.
 * Each Player is uniquely identified by his nickname.
 * Furthermore each Player has an associated GodCard and can have one of
 * the states specified in PlayerState during his turn.
 */
class Player {

    private final String nickname;
    private Set<PlayerFlag> flags;
    private List<Worker> workers;
    private PlayerState state;
    private CardFile card;
    private Map<PlayerState,Set<TriggerType>>  statePossibleActions;

    Player(String nickname){
        assert (nickname != null);
        this.nickname = nickname;
        this.statePossibleActions = new HashMap<>();
        this.flags = new HashSet<>();
        this.workers = new ArrayList<>();
        this.workers.add(new Worker(this.nickname + ".1", this.nickname));
        this.workers.add(new Worker(this.nickname + ".2", this.nickname));
        this.state = PlayerState.TURN_STARTED;
    }

    /**
     * Get possible actions (MOVE, BUILD) for the player from the current state
     * @return Set of possible actions (MOVE, BUILD)
     */
    public Set<TriggerType> getPossibleActions(){
        if (statePossibleActions.containsKey(state)){
            return new HashSet<>(statePossibleActions.get(state));
        }
        return null;
    }

    /**
     * Set the possible action data of this player
     * @param association Map where for each possible state is specified some possible actions (or none)
     */
    public void addActionData(Map<PlayerState, Set<TriggerType>> association){
        assert (association != null);
        statePossibleActions = new HashMap<>();
        for(PlayerState state : PlayerState.values()){
            assert (association.containsKey(state));
            statePossibleActions.put(state, new HashSet<>(association.get(state)));
        }
    }

    /**
     * Getter that returns the nickname of the Player.
     * @return a String containing the nickname.
     */
    public String getNickname(){ return this.nickname; }

    /**
     * Getter that returns the CardFile of the GodCard associated to the Player.
     * @return an instance of CardFile.
     */
    public CardFile getCard(){ return this.card; }

    /**
     * Getter that returns the current state of the Player.
     * @return one of the states contained in PlayerState.
     */
    public PlayerState getState(){ return this.state; }

    /**
     * Getter that returns the Workers associated to the Player.
     * @return a List of all the Workers possessed by the Player.
     */
    public List<Worker> getWorkers(){ return new ArrayList<>(this.workers); }


    /**
     * Setter that sets the state of the Player to one of
     * the possible states in PlayerState.
     * @param ps is the state of the Player to set.
     */
    public void setPlayerState(PlayerState ps){
        assert ps != null;
        this.state = ps;
    }

    /**
     * Setter that sets the GodCard associated to the Player.
     * @param c is an instance of CardFile to set.
     */
    public void setCard(CardFile c){
        assert c != null;
        this.card = c;
    }

    /**
     * This method adds a flag to the Player which indicates
     * that he has performed a certain action.
     * @param flag is the performed action to add to the Player List flags.
     */
    public void addFlag(PlayerFlag flag){
        assert flag != null;
        this.flags.add(flag);
    }

    /**
     * This method checks if the Player has performed a given action during the match.
     * @param flag is the action to check.
     * @return true if the action is contained in the List flags, false otherwise.
     */
    public boolean hasFlag(PlayerFlag flag){
        assert flag != null;
        return flags.contains(flag);
    }

    /**
     * This method deletes all the flags contained in the List flags.
     */
    public void clearFlags(){ flags.clear(); }

    /**
     * This method checks if the given obj equals the Player.
     * @param obj is an instance of Object to check.
     * @return true if obj and the Player are identical, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null) return false;
        if(this.getClass() != obj.getClass()) return false;
        Player other = (Player)obj;
        return this.nickname.equals(other.nickname);
    }

    /**
     * This method returns a clone of the Player.
     * @return an cloned instance of the Player.
     */
    @Override
    protected Player clone(){
        Set<PlayerFlag> clonedFlags = new HashSet<>(this.flags);
        List<Worker> clonedWorkers = new ArrayList<>();
        Player clonedPlayer = new Player(this.nickname);

        clonedPlayer.setPlayerState(this.state);

        if(this.card != null)
            clonedPlayer.setCard(this.card);

        for(Worker w : this.workers){
            clonedWorkers.add(w.clone());
        }

        clonedPlayer.workers = clonedWorkers;
        clonedPlayer.flags = clonedFlags;

        return clonedPlayer;
    }

    /**
     * Get the hash code for this player
     * @return Hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.nickname);
    }
}
