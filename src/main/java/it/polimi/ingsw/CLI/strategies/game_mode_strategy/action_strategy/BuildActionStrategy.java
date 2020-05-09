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

    private static final String BUILDINGS_REGEXP = "^(([A-E]|[a-e])[1-5][ ][1-4])$";
    private static final Pattern BUILDINGS_PATTERN = Pattern.compile(BUILDINGS_REGEXP);

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
        List<String> workersID = matchData.getIds().get(player);


        //ELEMENT USED TO DISPLAY CHANGES
        Board board = matchData.getBoard();
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();

        boolean restartForbidden = false; //FALSE IF THE PLAYER CAN CHOSE THE WORKER AGAIN
        boolean makeChoiceForbidden = false; //TRUE IF THE PLAYER CAN'T MAKE A CHOICE BECAUSE THERE ARE NO POSSIBLE MOVES
        boolean confirmActionForbidden = false; //TRUE IF THE PLAYER CAN'T CONFIRM THE ACTION SINCE HE HAS NOT CHOSEN A WORKER

        if(lastUsedWorker == null){

            System.out.println("Make your build!");

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

        matchData.printMatch();

        graphicalBoard.resetPossibleActions();

        //IF THERE ARE NO POSSIBLE POSITIONS THE PLAYER CAN'T MAKE A CHOICE
        if(possibleBuildingsInPositions.isEmpty()) makeChoiceForbidden = true;

        Integer choice = InputUtilities.getActionChoice(makeChoiceForbidden,restartForbidden, confirmActionForbidden);

        if(choice == -1) return false;

        switch(choice){
            case 1:
                boolean getChoice = getChosenBuildingsInPoint(possibleBuildingsInPositions, board);

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
                return true;
            case 3:
                PacketBuild packetBuildConfirmation = new PacketBuild(player,lastUsedWorker, false, currentBuilds, currentDataOrder);
                client.send(packetBuildConfirmation);
                break;
        }


        return false;
    }

    private boolean getChosenBuildingsInPoint(Map<Point, List<BuildingType>> possibleBuildingsInPositions, Board board){
        StringBuilder possibleBuildsBuilder = new StringBuilder();
        int workerNumber = Character.getNumericValue(lastUsedWorker.charAt(lastUsedWorker.length() - 1));

        for(Point position : possibleBuildingsInPositions.keySet()){
            possibleBuildsBuilder.append("- ").append(board.getCoordinates(position));
            for(BuildingType building : possibleBuildingsInPositions.get(position)){
                possibleBuildsBuilder.append(" ").append(building.toString()).append("(").append(buildingTypeToChar(building)).append(")");
            }
            possibleBuildsBuilder.append("\n");
        }

        System.out.println("Available buildings: ");
        System.out.println(possibleBuildsBuilder.toString());

        String command;
        Point chosenPosition;
        BuildingType chosenBuilding;
        List<BuildingType> possibleBuildings;
        boolean error = false;
        boolean suggestion = true;
        do{
            if(error) System.out.println("Invalid buildings for worker" + (workerNumber) + ", retry");

            do{
                if(suggestion) System.out.print("Choose your next worker" + (workerNumber) + "'s buildings (ex A1 1, B2 4...): ");
                else System.out.print("Choose your next worker" + (workerNumber) + "'s buildings: ");
                suggestion = false;
                command = InputUtilities.getLine();
                if(command == null) return false;
            }while(!BUILDINGS_PATTERN.matcher(command).matches());

            chosenPosition = board.getPoint(Character.getNumericValue(command.charAt(1)), Character.toUpperCase(command.charAt(0)));
            chosenBuilding = charToBuildingType(command.charAt(3));
            possibleBuildings = possibleBuildingsInPositions.get(chosenPosition);

            error = board.getCell(chosenPosition) == null || possibleBuildings == null || !possibleBuildings.contains(chosenBuilding);
        }while(error);

        List<BuildingType> helper = new ArrayList<>();

        if(currentBuilds.containsKey(chosenPosition)) helper = currentBuilds.get(chosenPosition);
        helper.add(chosenBuilding);
        currentBuilds.put(chosenPosition, helper);
        currentDataOrder.add(chosenPosition);

        return true;
    }

    private BuildingType charToBuildingType(Character building){
        switch (building){
            case '1':
                return BuildingType.FIRST_FLOOR;
            case '2':
                return  BuildingType.SECOND_FLOOR;
            case '3':
                return BuildingType.THIRD_FLOOR;
            case '4':
                return BuildingType.DOME;
        }
        assert false;
        return BuildingType.DOME;
    }

    private Character buildingTypeToChar(BuildingType building){
        switch (building){
            case FIRST_FLOOR:
                return '1';
            case SECOND_FLOOR:
                return  '2';
            case THIRD_FLOOR:
                return '3';
            case DOME:
                return '4';
        }
        assert false;
        return '0';
    }

}
