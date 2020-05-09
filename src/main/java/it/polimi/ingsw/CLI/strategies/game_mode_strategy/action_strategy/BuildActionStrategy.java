package it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy;

import it.polimi.ingsw.CLI.Board;
import it.polimi.ingsw.CLI.GraphicalBoard;
import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.CLI.MatchData;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BuildActionStrategy implements ActionStrategy{

    private static final String BUILDINGS_REGEXP = "^(([A-E]|[a-e])[1-5][ ][1-4][1-4]?[1-4]?[1-4]?)$";
    private static final Pattern BUILDINGS_PATTERN = Pattern.compile(BUILDINGS_REGEXP);

    private Integer lastUsedWorker;
    Map<Point, List<BuildingType>> currentBuilds;
    List<Point> currentDataOrder;

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
        List<String> workersID = matchData.getIds().get(player);


        //ELEMENT USED TO DISPLAY CHANGES
        Board board = matchData.getBoard();
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();

        boolean restartForbidden = false; //FALSE IF THE PLAYER CAN CHOSE THE WORKER AGAIN
        boolean makeChoiceForbidden = false; //TRUE IF THE PLAYER CAN'T MAKE A CHOICE BECAUSE THERE ARE NO POSSIBLE MOVES
        boolean confirmActionForbidden = false; //TRUE IF THE PLAYER CAN'T CONFIRM THE ACTION SINCE HE HAS NOT CHOSEN A WORKER

        if(lastUsedWorker == null){

            confirmActionForbidden = true;

            List<String> possibleWorkers = new ArrayList<>(packetPossibleBuilds.getPossibleBuilds().keySet());

            lastUsedWorker = InputUtilities.getWorkerChoice(possibleWorkers, workersID);
            if(lastUsedWorker == null) return false;

        }

        //POSSIBLE POSITIONS CONTAINS THE POSITIONS THAT THE PLAYER CAN CHOOSE AT THIS TIME DURING THE BUILD
        Map<Point, List<BuildingType>> possibleBuildingsInPositions = new HashMap<>(packetPossibleBuilds.getPossibleBuilds().get(workersID.get(lastUsedWorker)));
        graphicalBoard.setPossibleActions(new ArrayList<>(possibleBuildingsInPositions.keySet()));

        matchData.printMatch();

        graphicalBoard.resetPossibleActions();

        //IF THERE ARE NO POSSIBLE POSITIONS THE PLAYER CAN'T MAKE A CHOICE
        if(possibleBuildingsInPositions.isEmpty()) makeChoiceForbidden = true;

        Integer choice = InputUtilities.getActionChoice(makeChoiceForbidden,restartForbidden, confirmActionForbidden);

        if(choice == -1) return false;

        switch(choice){
            case 1:
                Map<Point, List<BuildingType>> chosenBuildingsInPoint = getChosenBuildingsInPoint(possibleBuildingsInPositions, board);

                if(chosenBuildingsInPoint == null) return false;

                //ADD CHOSEN BUILDINGS TO CURRENT BUILDINGS
                for(Point position : chosenBuildingsInPoint.keySet()){
                    if(currentBuilds.containsKey(position)) currentBuilds.get(position).addAll(chosenBuildingsInPoint.get(position));
                    else currentBuilds.put(position, chosenBuildingsInPoint.get(position));

                    //UPDATE THE GRAPHICAL BOARD WITH NEW BUILDINGS
                    graphicalBoard.getCell(position).addBuildings(chosenBuildingsInPoint.get(position));
                }

                //WE THEN ASK FOR A NEW PACKETBUILD

                PacketBuild packetBuild = new PacketBuild(player, workersID.get(lastUsedWorker), true, currentBuilds, currentDataOrder);
                client.send(packetBuild);
                break;
            case 2:
                //WE RESET CHANGES TO THE GRAPHICAL BOARD, THE CHECKPOINT IS THE BOARD OBJECT IN THE MATCHDATA
                matchData.makeGraphicalBoardEqualToBoard();
                return true;
            case 3:
                PacketBuild packetBuildConfirmation = new PacketBuild(player, workersID.get(lastUsedWorker), false, currentBuilds, currentDataOrder);
                client.send(packetBuildConfirmation);
                break;
        }


        return false;
    }

    private Map<Point, List<BuildingType>> getChosenBuildingsInPoint(Map<Point, List<BuildingType>> possibleBuildingsInPositions, Board board){
        StringBuilder possibleBuildsBuilder = new StringBuilder();

        for(Point position : possibleBuildingsInPositions.keySet()){
            possibleBuildsBuilder.append("- ").append(board.getCoordinates(position));
            for(BuildingType building : possibleBuildingsInPositions.get(position)){
                possibleBuildsBuilder.append(" ").append(building.toString());
            }
            possibleBuildsBuilder.append("\n");
        }

        System.out.println("Available buildings: ");
        System.out.println(possibleBuildsBuilder.toString());

        String command;
        Point chosenPosition;
        List<BuildingType> chosenBuildings;
        List<BuildingType> possibleBuildings;
        Map<Point, List<BuildingType>> performedBuild = new HashMap<>();
        boolean error = false;
        int count = 0;
        do{
            if(error) System.out.println("Invalid buildings for worker " + (lastUsedWorker + 1) + ", retry");

            do{
                count++;
                if(count > 1) System.out.print("Choose your next worker" + (lastUsedWorker + 1) + "'s buildings: ");
                else System.out.print("Choose your next worker" + (lastUsedWorker + 1) + "'s buildings (ex A1 12, B2 4, A3 34 ...): ");
                command = InputUtilities.getLine();
                if(command == null) return null;
            }while(!BUILDINGS_PATTERN.matcher(command).matches());

            chosenPosition = board.getPoint(Character.getNumericValue(command.charAt(1)), Character.toUpperCase(command.charAt(0)));
            chosenBuildings = command.substring(3).chars().mapToObj(i -> (char) i).map(this::charToBuildingType).collect(Collectors.toList());
            performedBuild.put(chosenPosition, chosenBuildings);
            possibleBuildings = possibleBuildingsInPositions.get(chosenPosition);
            //TODO: FIX LAST CONDITION, CHOSEN BUILDINGS HAVE TO BE CONTAINED FROM THE BEGINNING OF POSSIBLE BUILDINGS
            error = board.getCell(chosenPosition) == null || chosenBuildings.isEmpty() || possibleBuildings == null || !possibleBuildings.equals(chosenBuildings);
        }while(error);

        currentDataOrder.add(chosenPosition);

        return performedBuild;
    }

    private BuildingType charToBuildingType(char building){
        switch (building){
            case '1':
                return BuildingType.FIRST_FLOOR;
            case '2':
                return  BuildingType.SECOND_FLOOR;
            case '3':
                return BuildingType.THIRD_FLOOR;
        }
        return BuildingType.DOME;
    }

}
