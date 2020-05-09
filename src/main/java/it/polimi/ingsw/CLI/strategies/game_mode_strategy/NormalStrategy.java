package it.polimi.ingsw.CLI.strategies.game_mode_strategy;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy.ActionStrategy;
import it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy.BuildActionStrategy;
import it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy.MoveActionStrategy;
import it.polimi.ingsw.packets.*;

public class NormalStrategy implements GameModeStrategy{

    private ActionStrategy actionStrategy;
    private PacketDoAction lastAction;

    @Override
    public void handleAction(PacketDoAction packetDoAction, boolean isRetry) {
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
                do{
                    System.out.print("Do you want to make a move(1) or a build(2): ");
                    choice = InputUtilities.getInt("Not a valid choice, choose an action: ");
                    if (choice == null) return;
                }while(choice != 1 && choice != 2);

                if(choice == 1) handleMove();
                else handleBuild();

                break;
        }
    }

    @Override
    public void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves) {
        if(actionStrategy.handleMoveAction(packetPossibleMoves)){
            handleAction(lastAction, false);
        }
    }

    @Override
    public void handlePossibleBuilds(PacketPossibleBuilds packetPossibleBuilds) {
        if(actionStrategy.handleBuildAction(packetPossibleBuilds)){
            handleAction(lastAction, false);
        }
    }

    private void handleMove(){
        MatchData matchData = MatchData.getInstance();
        actionStrategy = new MoveActionStrategy();
        PacketMove packetMove = new PacketMove(matchData.getPlayerName());
        matchData.getClient().send(packetMove);
    }

    private void handleBuild(){
        MatchData matchData = MatchData.getInstance();
        actionStrategy = new BuildActionStrategy();
        PacketBuild packetBuild = new PacketBuild(matchData.getPlayerName());
        matchData.getClient().send(packetBuild);
    }
}
