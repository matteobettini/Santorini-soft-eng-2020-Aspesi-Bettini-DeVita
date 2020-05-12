package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.enums.BuildingType;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InputUtilities {

    private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    private static final String POSITIONS_REGEXP = "^(([A-E]|[a-e])[1-5])$";
    public static final Pattern POSITION_PATTERN = Pattern.compile(POSITIONS_REGEXP);
    private static final String BUILDINGS_REGEXP = "^(([A-E]|[a-e])[1-5][ ][1-4])$";
    public static final Pattern BUILDINGS_PATTERN = Pattern.compile(BUILDINGS_REGEXP);

    public static String getLine(){
        String name;
        do {
            try {
                while (!input.ready()) {
                    Thread.sleep(200);
                }
                name = input.readLine();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        } while ("".equals(name));
        return name;
    }


    public static Integer getInt(String errorMessage){
        String numString;
        Integer num = null;
        boolean fin = false;

        try {
            do {
                while (!input.ready()) {
                    Thread.sleep(200);
                }
                numString = input.readLine();
                try {
                    num = Integer.parseInt(numString);
                    fin = true;
                } catch (NumberFormatException e) {
                    System.out.print(errorMessage);
                }
            }while(!fin);

        }catch (InterruptedException | IOException e){
            Thread.currentThread().interrupt();
            return null;
        }
        return num;
    }

    public static Boolean getBoolean(){
        String boolString;
        Boolean bool = null;
        boolean fin = false;
        try {
            do {
                while (!input.ready()) {
                    Thread.sleep(200);
                }
                boolString = input.readLine();
                try {
                    if(!boolString.equals("true") && !boolString.equals("false"))
                        throw new NumberFormatException();
                    bool = Boolean.parseBoolean(boolString);
                    fin = true;
                } catch (NumberFormatException e) {
                    System.out.println("Retry");
                }
            }while(!fin);

        }catch (InterruptedException | IOException e){
            Thread.currentThread().interrupt();
            return null;
        }

        return bool;
    }

    public static String getWorkerChoice(List<String> possibleWorkers){
        //FIRST WE ORDER THE LIST OF POSSIBLE WORKERS BASED ON THE ASSUMPTION THAT WORKERS' IDS ARE IN LEXICOGRAPHICAL ORDER

        MatchData matchData = MatchData.getInstance();


        List<Integer> availableWorkers = possibleWorkers.stream().map(matchData::getWorkerNumber).sorted().collect(Collectors.toList());

        Integer workerChoice = availableWorkers.get(0);
        boolean showCardsInGame = true;
        //IN CASE THERE ARE MULTIPLE CHOICES THE PLAYER CAN CHOOSE THE DESIRED WORKER
        if(possibleWorkers.size() > 1){
            do{
                System.out.print("Choose one Worker between ");
                int end = availableWorkers.size();
                int count = 0;

                //DISPLAY THE POSSIBLE CHOICES
                for(Integer wNumber: availableWorkers){
                    count++;
                    if(count < end) System.out.print((wNumber) + ", ");
                    else{
                        if(showCardsInGame) System.out.print((wNumber)  + " by entering its number or press " + (possibleWorkers.size() + 1) + " to see the cards in game: ");
                        else System.out.print((wNumber)  + ": ");
                    }

                }

                //ASK THE CHOICE
                workerChoice = InputUtilities.getInt("Not a valid worker number, worker number: ");
                if (workerChoice == null) return null;
                else if(workerChoice == possibleWorkers.size() + 1){
                    OutputUtilities.printCards();
                    showCardsInGame = false;
                }
            }while(!availableWorkers.contains(workerChoice));
        }

        Integer finalWorkerChoice = workerChoice;
        return possibleWorkers.stream().filter(w -> matchData.getWorkerNumber(w).equals(finalWorkerChoice)).findFirst().orElse(null);

    }

    public static Integer getActionChoice(boolean makeChoiceForbidden, boolean restartForbidden, boolean confirmActionForbidden){
        String choiceMessage;
        List<Integer> mapChoices = new ArrayList<>();

        if(makeChoiceForbidden && restartForbidden && confirmActionForbidden){
            assert false;
            return -1; //IMPOSSIBLE CONFIGURATION
        }
        else if(makeChoiceForbidden && restartForbidden) return 3;
        else if(makeChoiceForbidden && confirmActionForbidden) return 2;
        else if(restartForbidden && confirmActionForbidden) return 1;
        else if(makeChoiceForbidden){
            choiceMessage = "Do you want to restart the selection(1) or confirm the current actions(2)? ";
            mapChoices.add(2);
            mapChoices.add(3);

        }
        else if(restartForbidden){
            choiceMessage = "Do you want to make a choice(1) or confirm the current actions(2)? ";
            mapChoices.add(1);
            mapChoices.add(3);

        }
        else if(confirmActionForbidden){
            choiceMessage = "Do you want to make a choice (1) or restart the selection(2)? ";
            mapChoices.add(1);
            mapChoices.add(2);

        }
        else{
            choiceMessage = "Do you want to make a choice(1), restart the selection(2) or confirm the current actions(3)? ";
            mapChoices.add(1);
            mapChoices.add(2);
            mapChoices.add(3);
        }

        Integer choice;
        do{
            System.out.print(choiceMessage);
            choice = InputUtilities.getInt("Not a valid action number, choose an action: ");
            if (choice == null) return -1;
        }while(choice <= 0 || choice > mapChoices.size());

        return mapChoices.get(choice - 1);
    }

    public static Point getChosenPosition(List<Point> availablePositions, Board board, String worker){

        MatchData matchData = MatchData.getInstance();

        if(!matchData.isHardcore()){
            StringBuilder positionsBuilder = new StringBuilder();
            for(Point position : availablePositions){
                positionsBuilder.append("- ").append(board.getCoordinates(position)).append("\n");
            }

            System.out.println("Available positions: ");
            System.out.println(positionsBuilder.toString());
        }

        int workerNumber = matchData.getWorkerNumber(worker);


        //THE PLAYER CAN NOW CHOOSE HIS WORKER'S NEXT POSITION
        String point;
        Point chosenPosition;
        boolean error = false;
        boolean suggestion = true;
        do{
            if(error) System.out.println("Invalid position for worker" + (workerNumber) + ", retry");

            do{
                if(suggestion)  System.out.print("Choose your next worker" + (workerNumber) + "'s position (ex A1, B2...): ");
                else System.out.print("Choose your next worker" + (workerNumber) + "'s position: ");
                suggestion = false;
                point = InputUtilities.getLine();
                if(point == null) return null;
            }while(!POSITION_PATTERN.matcher(point).matches());

            chosenPosition = board.getPoint(Character.getNumericValue(point.charAt(1)), Character.toUpperCase(point.charAt(0)));
            assert board.getCell(chosenPosition) == null;
            error = !availablePositions.contains(chosenPosition);
        }while(error);

        return chosenPosition;
    }

    public static boolean getChosenBuildingsInPoint(Map<Point, List<BuildingType>> possibleBuildingsInPositions, Board board, String worker, List<Point> currentDataOrder, Map<Point, List<BuildingType>> currentBuilds){

        MatchData matchData = MatchData.getInstance();

        int workerNumber = matchData.getWorkerNumber(worker);

        if(!matchData.isHardcore()){
            StringBuilder possibleBuildsBuilder = new StringBuilder();
            for(Point position : possibleBuildingsInPositions.keySet()){
                possibleBuildsBuilder.append("- ").append(board.getCoordinates(position));
                for(BuildingType building : possibleBuildingsInPositions.get(position)){
                    possibleBuildsBuilder.append(" ").append(building.toString()).append("(").append(InputUtilities.buildingTypeToChar(building)).append(")");
                }
                possibleBuildsBuilder.append("\n");
            }

            System.out.println("Available buildings: ");
            System.out.println(possibleBuildsBuilder.toString());
        }


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
            }while(!InputUtilities.BUILDINGS_PATTERN.matcher(command).matches());

            chosenPosition = board.getPoint(Character.getNumericValue(command.charAt(1)), Character.toUpperCase(command.charAt(0)));
            chosenBuilding = InputUtilities.charToBuildingType(command.charAt(3));
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

    public static BuildingType charToBuildingType(Character building){
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

    public static Character buildingTypeToChar(BuildingType building){
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
