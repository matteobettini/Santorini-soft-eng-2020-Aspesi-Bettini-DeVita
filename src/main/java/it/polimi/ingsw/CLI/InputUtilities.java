package it.polimi.ingsw.CLI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InputUtilities {

    private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

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
        possibleWorkers = possibleWorkers.stream().sorted().collect(Collectors.toList());


        List<Integer> availableWorkers = possibleWorkers.stream().map( w -> Character.getNumericValue(w.charAt(w.length() - 1))).collect(Collectors.toList());

        Integer workerChoice = availableWorkers.get(0);
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
                    else System.out.print((wNumber)  + ": ");
                }

                //ASK THE CHOICE
                workerChoice = InputUtilities.getInt("Not a valid worker number, worker number: ");
                if (workerChoice == null) return null;
            }while(!availableWorkers.contains(workerChoice));
        }

        return possibleWorkers.get(availableWorkers.indexOf(workerChoice));
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
            choiceMessage = "Do you want to make a choice (1), restart the selection(2)? ";
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
}
