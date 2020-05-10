package it.polimi.ingsw.CLI.strategies.game_mode_strategy;

import it.polimi.ingsw.CLI.Board;
import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.packets.PacketDoAction;
import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

import java.awt.*;
import java.util.regex.Pattern;

public class HardcoreStrategy implements GameModeStrategy {

    private static final String POSITIONS_REGEXP = "^(([A-E]|[a-e])[1-5])$";
    private static final Pattern POSITION_PATTERN = Pattern.compile(POSITIONS_REGEXP);

    private static final String BUILDINGS_REGEXP = "^(([A-E]|[a-e])[1-5][ ][1-4])$";
    private static final Pattern BUILDINGS_PATTERN = Pattern.compile(BUILDINGS_REGEXP);

    private PacketDoAction lastAction;
    private String lastUsedWorker;

    public HardcoreStrategy(){
        this.lastUsedWorker = null;
    }

    @Override
    public void handleAction(PacketDoAction packetDoAction, boolean isRetry){
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

    private void handleMove(){
    }

    private void handleBuild(){

    }

    @Override
    public void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves){ }

    @Override
    public void handlePossibleBuilds(PacketPossibleBuilds packetPossibleBuilds) { }

    private boolean thereIsDomeOrWorker(Point p1, Board board){
        return board.getCell(p1).getBuildings().contains(BuildingType.DOME) || board.getCell(p1).getWorker() != null;
    }
}
