package it.polimi.ingsw.client.cli.strategies.game_mode_strategy.action_strategy;

import it.polimi.ingsw.client.cli.graphical.GraphicalBoard;
import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.client.cli.utilities.InputUtilities;
import it.polimi.ingsw.client.cli.utilities.OutputUtilities;
import it.polimi.ingsw.client.communication.Client;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.common.packets.PacketBuild;
import it.polimi.ingsw.common.packets.PacketPossibleBuilds;
import it.polimi.ingsw.common.packets.PacketPossibleMoves;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildActionStrategy implements ActionStrategy{

    private String lastUsedWorker;
    private final Map<Point, List<BuildingType>> currentBuilds;
    private final List<Point> currentDataOrder;

    /**
     * This method is the constructor for the normal build strategy.
     * lastUsedWorker is the String containing the id of the last used Worker during the build.
     * currentBuilds maps the chosen buildings to their position on the board.
     * currentDataOrder is the list of points that contains the info about the order of the performed builds.
     */
    public BuildActionStrategy(){
        this.lastUsedWorker = null;
        this.currentBuilds = new HashMap<>();
        this.currentDataOrder = new ArrayList<>();
    }


    /**
     * In case a packet of possible moves is received during the build turn it will be ignored.
     * @param packetPossibleMoves is the object containing the workers' ids and their possible moves.
     * @return false
     */
    @Override
    public boolean handleMoveAction(PacketPossibleMoves packetPossibleMoves) {
        return false;
    }

    /**
     * This method will guide the player trough the choice of his builds.
     * Firstly, if the lastUsedWorker is null, the player is queried on its preference based on the possible workers in the packet.
     * After the display of the possible position of the builds through the graphical board and the possible buildings through the command line,
     * the player has three different choices:
     * - make a choice and then ask a new packet possible builds till an empty one is received.
     * - restart the entire process again.
     * - confirm the performed builds.
     * @param packetPossibleBuilds is the object containing the workers' ids and their possible builds.
     * @return true if the entire action is restarted, false if the action is confirmed or another possible builds packet is requested.
     */
    @Override
    public boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds) {
        MatchData matchData = MatchData.getInstance();
        Client client = matchData.getClient();
        String player = matchData.getPlayerName();


        //ELEMENT USED TO DISPLAY CHANGES
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

        OutputUtilities.printMatchAndActions(new ArrayList<>(possibleBuildingsInPositions.keySet()));

        if(currentDataOrder.isEmpty())  System.out.println("Make your build!");

        //IF THERE ARE NO POSSIBLE POSITIONS THE PLAYER CAN'T MAKE A CHOICE
        if(possibleBuildingsInPositions.isEmpty()) makeChoiceForbidden = true;

        Integer choice = InputUtilities.getActionChoice(makeChoiceForbidden,restartForbidden, confirmActionForbidden);

        if(choice == InputUtilities.ERROR) return false;

        switch(choice){
            case InputUtilities.MAKE_CHOICE:
                boolean getChoice = InputUtilities.getChosenBuildingsInPoint(possibleBuildingsInPositions, lastUsedWorker, currentDataOrder, currentBuilds);

                if(!getChoice) return false;

                //UPDATE TO THE GRAPHICAL BOARD
                for(Point position : currentBuilds.keySet()) graphicalBoard.getCell(position).addBuildings(currentBuilds.get(position));

                //WE THEN ASK FOR A NEW PACKET BUILD

                PacketBuild packetBuild = new PacketBuild(player, lastUsedWorker, true, currentBuilds, currentDataOrder);
                client.send(packetBuild);
                break;
            case InputUtilities.RESTART:
                //WE RESET CHANGES TO THE GRAPHICAL BOARD, THE CHECKPOINT IS THE BOARD OBJECT IN THE MATCHDATA
                matchData.makeGraphicalBoardEqualToBoard();
                OutputUtilities.printMatch();
                return true;
            case InputUtilities.CONFIRM:
                PacketBuild packetBuildConfirmation = new PacketBuild(player,lastUsedWorker, false, currentBuilds, currentDataOrder);
                client.send(packetBuildConfirmation);
                break;
        }


        return false;
    }

}
