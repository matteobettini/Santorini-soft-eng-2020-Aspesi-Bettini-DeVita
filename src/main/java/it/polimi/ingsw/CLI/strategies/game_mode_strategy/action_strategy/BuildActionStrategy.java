package it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.packets.PacketBuild;
import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildActionStrategy implements ActionStrategy{

    private String lastUsedWorker;
    private Map<Point, List<BuildingType>> currentBuilds;
    private List<Point> currentDataOrder;

    public BuildActionStrategy(){
        this.lastUsedWorker = null;
        this.currentBuilds = new HashMap<>();
        this.currentDataOrder = new ArrayList<>();
    }


    @Override
    public boolean handleMoveAction(PacketPossibleMoves packetPossibleMoves) {
        return false;
    }

    @Override
    public boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds) {
        MatchData matchData = MatchData.getInstance();
        Client client = matchData.getClient();
        String player = matchData.getPlayerName();


        //ELEMENT USED TO DISPLAY CHANGES
        Board board = matchData.getBoard();
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();

        boolean restartForbidden = false; //FALSE IF THE PLAYER CAN CHOOSE THE WORKER AGAIN
        boolean makeChoiceForbidden = false; //TRUE IF THE PLAYER CAN'T MAKE A CHOICE BECAUSE THERE ARE NO POSSIBLE BUILDS
        boolean confirmActionForbidden = false; //TRUE IF THE PLAYER CAN'T CONFIRM THE ACTION SINCE HE HAS NOT CHOSEN A WORKER

        if(lastUsedWorker == null){

            confirmActionForbidden = true;

            List<String> possibleWorkers = new ArrayList<>();

            for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
                if(!packetPossibleBuilds.getPossibleBuilds().get(worker).isEmpty()) possibleWorkers.add(worker);
            }

            if(possibleWorkers.size() == 1) restartForbidden = true;

            lastUsedWorker = InputUtilities.getWorkerChoice(possibleWorkers);
            if(lastUsedWorker == null) return false;

        }

        //POSSIBLE POSITIONS CONTAINS THE POSITIONS THAT THE PLAYER CAN CHOOSE AT THIS TIME DURING THE BUILD
        Map<Point, List<BuildingType>> possibleBuildingsInPositions = packetPossibleBuilds.getPossibleBuilds().get(lastUsedWorker);
        graphicalBoard.setPossibleActions(new ArrayList<>(possibleBuildingsInPositions.keySet()));

        OutputUtilities.printMatch();

        if(currentDataOrder.isEmpty())  System.out.println("Make your build!");

        graphicalBoard.resetPossibleActions();

        //IF THERE ARE NO POSSIBLE POSITIONS THE PLAYER CAN'T MAKE A CHOICE
        if(possibleBuildingsInPositions.isEmpty()) makeChoiceForbidden = true;

        Integer choice = InputUtilities.getActionChoice(makeChoiceForbidden,restartForbidden, confirmActionForbidden);

        if(choice == -1) return false;

        switch(choice){
            case 1:
                boolean getChoice = InputUtilities.getChosenBuildingsInPoint(possibleBuildingsInPositions, board, lastUsedWorker, currentDataOrder, currentBuilds);

                if(!getChoice) return false;

                //UPDATE TO THE GRAPHICAL BOARD
                for(Point position : currentBuilds.keySet()) graphicalBoard.getCell(position).addBuildings(currentBuilds.get(position));

                //WE THEN ASK FOR A NEW PACKETBUILD

                PacketBuild packetBuild = new PacketBuild(player, lastUsedWorker, true, currentBuilds, currentDataOrder);
                client.send(packetBuild);
                break;
            case 2:
                //WE RESET CHANGES TO THE GRAPHICAL BOARD, THE CHECKPOINT IS THE BOARD OBJECT IN THE MATCHDATA
                matchData.makeGraphicalBoardEqualToBoard();
                OutputUtilities.printMatch();
                return true;
            case 3:
                PacketBuild packetBuildConfirmation = new PacketBuild(player,lastUsedWorker, false, currentBuilds, currentDataOrder);
                client.send(packetBuildConfirmation);
                break;
        }


        return false;
    }

}
