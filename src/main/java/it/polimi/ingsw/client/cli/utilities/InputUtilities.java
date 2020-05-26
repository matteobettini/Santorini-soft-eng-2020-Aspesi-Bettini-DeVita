package it.polimi.ingsw.client.cli.utilities;

import it.polimi.ingsw.client.cli.graphical.GraphicalBoard;
import it.polimi.ingsw.client.cli.match_data.Board;
import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.common.enums.BuildingType;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InputUtilities {

    private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    public static final String POSITIONS_REGEXP = "^(([A-E]|[a-e])[1-5])$";
    public static final Pattern POSITION_PATTERN = Pattern.compile(POSITIONS_REGEXP);
    public static final String BUILDINGS_REGEXP = "^(([A-E]|[a-e])[1-5][ ][1-4])$";
    public static final Pattern BUILDINGS_PATTERN = Pattern.compile(BUILDINGS_REGEXP);
    public static final int MAKE_CHOICE = 1;
    public static final int RESTART = 2;
    public static final int CONFIRM = 3;
    public static final int ERROR = -1;

    /**
     * This method gets a line from from the command line asynchronously.
     * @return the String in input.
     */
    public static String getLine(){
        String input;
        do {
            try {
                while (!InputUtilities.input.ready()) {
                    Thread.sleep(200);
                }
                input = InputUtilities.input.readLine();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        } while ("".equals(input));
        return input;
    }


    /**
     * This method gets an integer from from the command line asynchronously.
     * @return the Integer in input.
     */
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

    /**
     * This method gets a boolean from from the command line asynchronously.
     * @return the boolean in input.
     */
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

    /**
     * This method makes the player choose the worker that will be used in his next action given the possible workers (the ones who can move/build).
     * @param possibleWorkers is the list of workers' ids that re possible to choose.
     * @return the chosen worker's id.
     */
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

    /**
     * This method makes the player choose three different behaviours during his action:
     * - 1 if the player wants to make a choice.
     * - 2 if the player wants to restart the entire action.
     * - 3 if the player wants to confirm his current decisions.
     * @param makeChoiceForbidden is true if the choice is forbidden (there are no possible ones). false otherwise.
     * @param restartForbidden is true if restart is forbidden (the player can't restart because there are no past actions to redo), false otherwise.
     * @param confirmActionForbidden is true if the player cannot confirm his current actions (there are no current actions), false otherwise.
     * @return an Integer corresponding to the chosen behaviour.
     */
    public static Integer getActionChoice(boolean makeChoiceForbidden, boolean restartForbidden, boolean confirmActionForbidden){
        String choiceMessage;
        List<Character> mapChoices = new ArrayList<>();

        if(makeChoiceForbidden && restartForbidden && confirmActionForbidden){
            assert false;
            return ERROR; //IMPOSSIBLE CONFIGURATION
        }
        else if(makeChoiceForbidden && restartForbidden) return CONFIRM;
        else if(makeChoiceForbidden && confirmActionForbidden) return RESTART;
        else if(restartForbidden && confirmActionForbidden) return MAKE_CHOICE;
        else if(makeChoiceForbidden){
            choiceMessage = "Do you want to restart the selection(r) or confirm the current actions(c)? ";
            mapChoices.add('r');
            mapChoices.add('c');

        }
        else if(restartForbidden){
            choiceMessage = "Do you want to make a choice(m) or confirm the current actions(c)? ";
            mapChoices.add('m');
            mapChoices.add('c');

        }
        else if(confirmActionForbidden){
            choiceMessage = "Do you want to make a choice (m) or restart the selection(r)? ";
            mapChoices.add('m');
            mapChoices.add('r');

        }
        else{
            choiceMessage = "Do you want to make a choice(m), restart the selection(r) or confirm the current actions(c)? ";
            mapChoices.add('m');
            mapChoices.add('r');
            mapChoices.add('c');
        }

        char choice;
        do{
            System.out.print(choiceMessage);
            String line = InputUtilities.getLine();
            if (line == null) return ERROR;
            if(line.length() != 1) choice = ' ';
            else choice = line.toLowerCase().charAt(0);
        }while(!mapChoices.contains(choice));

        if(choice == 'm') return MAKE_CHOICE;
        else  if (choice == 'r') return RESTART;
        else if (choice == 'c') return CONFIRM;

        assert false;

        return ERROR;
    }

    /**
     * This method returns a Point given the coordinates used in the GraphicalBoard
     * to display the board' positions to the user.
     * @param x is the coordinate X that goes from 1 to #columns.
     * @param y is the coordinate Y that goes from A to the (#rows)th letter of the alphabet.
     * @return a Point containing the coordinates of the translated position on the board.
     */
    public static Point getPoint(int x, char y){
        y = Character.toUpperCase(y);
        if(x <= 0 || x > Board.getRows() || y < 'A' || y > 'E') return null;
        x--;
        int helper = Character.getNumericValue(y) - Character.getNumericValue('A');
        return new Point(x, helper);
    }

    /**
     * This method returns a Point given the coordinates used in the GraphicalBoard
     * to display the board' positions to the user.
     * @param point is a String containing the coordinates used to display positions in the GraphicalBoard (ex. A1, B3,...).
     * @return a Point containing the coordinates of the translated position on the board.
     */
    public static Point getPoint(String point){
        return InputUtilities.POSITION_PATTERN.matcher(point).matches() ? getPoint(Character.getNumericValue(point.charAt(1)), Character.toUpperCase(point.charAt(0))) : null;
    }

    /**
     * This method returns the coordinates used to display positions on the GraphicalBoard given the real positions'
     * coordinates in the board.
     * @param position is a Point containing the coordinate X that goes from 0...columns and the coordinate Y that goes from 0...rows.
     * @return a String containing the coordinates used to display positions in the GraphicalBoard (ex. A1, B3,...).
     */
    public static String getCoordinates(Point position){
        if(position.x < 0 || position.x >= Board.getRows() || position.y < 0 || position.y >= Board.getColumns()) return null;
        String coordinates = Character.toString((char) ('A' + position.y));
        coordinates = coordinates.concat(Integer.toString(position.x + 1));
        return coordinates;
    }


    /**
     * This method makes the player choose his next move given the available positions.
     * @param availablePositions is the List of possible choices.
     * @param worker is the worker's id used during the move.
     * @return a Point containing the chosen coordinates, null if there are problem with connections during the choice.
     */
    public static Point getChosenPosition(List<Point> availablePositions, String worker){

        MatchData matchData = MatchData.getInstance();

        if(!matchData.isHardcore()){
            StringBuilder positionsBuilder = new StringBuilder();
            for(Point position : availablePositions){
                positionsBuilder.append("- ").append(getCoordinates(position)).append("\n");
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

            chosenPosition = getPoint(point);
            error = !availablePositions.contains(chosenPosition);
        }while(error);

        return chosenPosition;
    }

    /**
     * This method makes the player choose his next building action given the possible buildings and their position on the board.
     * @param possibleBuildingsInPositions is the map that associates positions to the possible buildings.
     * @param worker is the worker's id used during the build.
     * @param currentDataOrder is the current order of positions where the player built.
     * @param currentBuilds is a map that associates the positions to the buildings built during the action.
     * @return true if the choice is performed, false otherwise (errors due to disconnections).
     */
    public static boolean getChosenBuildingsInPoint(Map<Point, List<BuildingType>> possibleBuildingsInPositions, String worker, List<Point> currentDataOrder, Map<Point, List<BuildingType>> currentBuilds){

        MatchData matchData = MatchData.getInstance();

        int workerNumber = matchData.getWorkerNumber(worker);

        if(!matchData.isHardcore()){
            StringBuilder possibleBuildsBuilder = new StringBuilder();
            for(Point position : possibleBuildingsInPositions.keySet()){
                possibleBuildsBuilder.append("- ").append(getCoordinates(position));
                for(BuildingType building : possibleBuildingsInPositions.get(position)){
                    possibleBuildsBuilder.append(" ").append(building.toString()).append("(").append(InputUtilities.fromBuildingTypeToInt(building)).append(")");
                }
                possibleBuildsBuilder.append("\n");
            }

            System.out.println("Available buildings: ");
            System.out.println(possibleBuildsBuilder.toString());
        }


        String point;
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
                point = InputUtilities.getLine();
                if(point == null) return false;
            }while(!InputUtilities.BUILDINGS_PATTERN.matcher(point).matches());

            chosenPosition = getPoint(point.substring(0, 2));
            chosenBuilding = InputUtilities.fromIntToBuildingType(point.charAt(3) - '0');
            possibleBuildings = possibleBuildingsInPositions.get(chosenPosition);

            error = possibleBuildings == null || !possibleBuildings.contains(chosenBuilding);
        }while(error);

        List<BuildingType> helper = new ArrayList<>();

        if(currentBuilds.containsKey(chosenPosition)) helper = currentBuilds.get(chosenPosition);
        helper.add(chosenBuilding);
        currentBuilds.put(chosenPosition, helper);
        currentDataOrder.add(chosenPosition);

        return true;
    }

    public static Map<String, Point> getInitialPositions(){

        MatchData matchData = MatchData.getInstance();
        List<String> workersID = matchData.getIds().get(matchData.getPlayerName());

        Map<String, Point> positions = new HashMap<>();

        for(int i = 0; i < workersID.size(); ++i){
            String choice;
            Point position = null;
            boolean error = false;
            do{
                if(error) System.out.println("Invalid position for worker " + (i + 1) + ", retry");

                do{
                    if(i > 0) System.out.print("Choose your worker" + (i + 1) + "'s position or enter r to restart the selection: ");
                    else System.out.print("Choose your worker" + (i + 1) + "'s position (ex A1, B2, ...): ");
                    choice = InputUtilities.getLine();
                    if(choice == null) return null;
                }while(!InputUtilities.POSITION_PATTERN.matcher(choice).matches() && !choice.equals("r"));

                if(!choice.equals("r")){
                    position = InputUtilities.getPoint(choice);
                    error = position == null || positions.containsValue(position);
                }

            }while(error);

            if(!choice.equals("r")){
                matchData.getGraphicalBoard().getCell(position).setWorker(workersID.get(i));
                if(i != workersID.size() - 1) OutputUtilities.printMatch();
                positions.put(workersID.get(i), position);
            }
            else{
                positions.clear();
                i = -1;
                matchData.makeGraphicalBoardEqualToBoard();
                OutputUtilities.printMatch();
            }
        }

        return positions;
    }

    /**
     * This method map BuildingType to the corresponding number. (ex. DOME -> 4)
     * @param buildingType is the BuildingType enum to map.
     * @return an integer associated with the given enum.
     */
    public static int fromBuildingTypeToInt(BuildingType buildingType){

        if(buildingType == null) return 0;

        switch (buildingType){
            case FIRST_FLOOR:
                return 1;
            case SECOND_FLOOR:
                return 2;
            case THIRD_FLOOR:
                return 3;
            case DOME:
                return 4;
        }

        return 0;
    }

    /**
     * This method map an integer to the corresponding BuildingType. (ex. 2 -> SECOND_FLOOR)
     * @param level is the integer enum to map.
     * @return a BuildingType enum associated with the given integer.
     */
    public static BuildingType fromIntToBuildingType(int level){

        switch(level){
            case 1:
                return BuildingType.FIRST_FLOOR;
            case 2:
                return BuildingType.SECOND_FLOOR;
            case 3:
                return BuildingType.THIRD_FLOOR;
        }
        return BuildingType.DOME;
    }
}
