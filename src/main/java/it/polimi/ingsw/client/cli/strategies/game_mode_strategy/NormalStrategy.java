package it.polimi.ingsw.client.cli.strategies.game_mode_strategy;

import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.client.cli.strategies.game_mode_strategy.action_strategy.ActionStrategy;
import it.polimi.ingsw.client.cli.strategies.game_mode_strategy.action_strategy.BuildActionStrategy;
import it.polimi.ingsw.client.cli.strategies.game_mode_strategy.action_strategy.MoveActionStrategy;
import it.polimi.ingsw.client.cli.utilities.InputUtilities;
import it.polimi.ingsw.client.cli.utilities.OutputUtilities;
import it.polimi.ingsw.common.packets.*;

public class NormalStrategy implements GameModeStrategy{

    private ActionStrategy actionStrategy;
    private PacketDoAction lastAction;

    /**
     * This method is the handler for the move or build actions when the normal mode is set.
     * It calls the different handlers based on the ActionType and manage the choice of the move or the build if permitted.
     * @param packetDoAction is the packet containing the ActionType and its receiver.
     * @param isRetry is true if the action is requested another time, false otherwise.
     */
    @Override
    public void handleAction(PacketDoAction packetDoAction, boolean isRetry) {
        assert !isRetry;

        MatchData matchData = MatchData.getInstance();
        if(!packetDoAction.getTo().equals(matchData.getPlayerName())){
            OutputUtilities.displayOthersActions(packetDoAction.getActionType(), packetDoAction.getTo());
            return;
        }
        lastAction = packetDoAction;
        switch (packetDoAction.getActionType()){
            case MOVE:
                handleMove();
                break;
            case BUILD:
                handleBuild();
                break;
            case MOVE_BUILD:
                Integer choice;
                boolean firstLoop = true;
                do{
                    if(!firstLoop)
                        System.out.print("Not a valid choice, ");
                    System.out.print("Do you want to make a move(1) or a build(2): ");
                    choice = InputUtilities.getInt("Not a valid choice, choose an action: ");
                    if (choice == null) return;
                    firstLoop = false;
                }while(choice != 1 && choice != 2);

                if(choice == 1) handleMove();
                else handleBuild();

                break;
        }
    }

    /**
     * This method calls the handleMoveAction of the ActionStrategy and if the said method returns true the action is restarted.
     * @param packetPossibleMoves is the packet containing the workers' ids and their possible moves.
     */
    @Override
    public void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves) {
        if(actionStrategy.handleMoveAction(packetPossibleMoves)){
            handleAction(lastAction, false);
        }
    }

    /**
     * This method calls the handleBuildAction of the ActionStrategy and if the said method returns true the action is restarted.
     * @param packetPossibleBuilds is the packet containing the workers' ids and their possible builds.
     */
    @Override
    public void handlePossibleBuilds(PacketPossibleBuilds packetPossibleBuilds) {
        if(actionStrategy.handleBuildAction(packetPossibleBuilds)){
            handleAction(lastAction, false);
        }
    }

    /**
     * This method asks the server for a PacketPossibleMoves so that the action can start once received.
     */
    private void handleMove(){
        MatchData matchData = MatchData.getInstance();
        actionStrategy = new MoveActionStrategy();
        PacketMove packetMove = new PacketMove(matchData.getPlayerName());
        matchData.getClient().send(packetMove);
    }

    /**
     * This method asks the server for a PacketPossibleBuilds so that the action can start once received.
     */
    private void handleBuild(){
        MatchData matchData = MatchData.getInstance();
        actionStrategy = new BuildActionStrategy();
        PacketBuild packetBuild = new PacketBuild(matchData.getPlayerName());
        matchData.getClient().send(packetBuild);
    }
}
