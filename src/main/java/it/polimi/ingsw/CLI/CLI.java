package it.polimi.ingsw.CLI;
import it.polimi.ingsw.CLI.enums.BuildingType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLI {
    public static void main(String[] args){
        CharStream stream = new CharStream(159, 50);
        GraphicalBoard graphicalBoard = new GraphicalBoard(stream);
        GraphicalOcean graphicalOcean = new GraphicalOcean(stream,159, 50);
        graphicalBoard.getCell(new Point(0,0)).addBuilding(BuildingType.FIRST_FLOOR);
        graphicalBoard.getCell(new Point(0,0)).addBuilding(BuildingType.SECOND_FLOOR);
        graphicalBoard.getCell(new Point(0,0)).addBuilding(BuildingType.THIRD_FLOOR);
        graphicalBoard.getCell(new Point(1,2)).addBuilding(BuildingType.FIRST_FLOOR);
        graphicalBoard.getCell(new Point(1,2)).addBuilding(BuildingType.SECOND_FLOOR);
        graphicalBoard.getCell(new Point(1,2)).addBuilding(BuildingType.THIRD_FLOOR);
        graphicalBoard.getCell(new Point(1,2)).addBuilding(BuildingType.DOME);
        graphicalBoard.getCell(new Point(0, 0)).setWorker( Color.WHITE ,'1', "Mirko");
        graphicalBoard.getCell(new Point(0,1)).addBuilding(BuildingType.FIRST_FLOOR);
        graphicalBoard.getCell(new Point(0,2)).addBuilding(BuildingType.DOME);
        graphicalBoard.getCell(new Point(1,1)).addBuilding(BuildingType.FIRST_FLOOR);
        graphicalBoard.getCell(new Point(1,1)).addBuilding(BuildingType.SECOND_FLOOR);
        graphicalBoard.getCell(new Point(4,4)).addBuilding(BuildingType.FIRST_FLOOR);
        graphicalBoard.getCell(new Point(4,4)).addBuilding(BuildingType.SECOND_FLOOR);
        graphicalBoard.getCell(new Point(1, 1)).setWorker(Color.ORANGE,'1', "Matteo");
        graphicalBoard.getCell(new Point(0, 1)).setWorker(Color.ORANGE, '2', "Matteo");
        graphicalBoard.getCell(new Point(4, 4)).setWorker(Color.CYAN, '1', "Andrea");
        graphicalBoard.getCell(new Point(4, 2)).setWorker(Color.CYAN, '2', "Andrea");
        graphicalOcean.draw();
        List<Point> possiblePositions = new ArrayList<>();
        possiblePositions.add(new Point(1,1));
        graphicalBoard.setPossibleActions(possiblePositions);
        List<Point> notPossiblePositions = new ArrayList<>();
        notPossiblePositions.add(new Point(1,2));
        graphicalBoard.setNotPossibleActions(notPossiblePositions);
        graphicalBoard.draw();
        Map<String , Color> players = new HashMap<>();
        players.put("123456789012345", Color.CYAN);
        players.put("Andrea", Color.ORANGE);
        players.put("Matteo", Color.WHITE);
        Map<String , String> playerGodCard = new HashMap<>();
        playerGodCard.put("123456789012345", "123456789012345");
        playerGodCard.put("Andrea", "Prometheus");
        playerGodCard.put("Matteo", "Persephone");
        GraphicalMatchMenu graphicalMatchMenu = new GraphicalMatchMenu(stream, players, playerGodCard);
        graphicalMatchMenu.setActivePlayer("Matteo");
        graphicalMatchMenu.setCurrentAction("BUILD");
        graphicalMatchMenu.setLoser("Andrea");
        graphicalMatchMenu.setGameOver(true);
        graphicalMatchMenu.setYouWin(false);
        graphicalMatchMenu.draw();
        stream.print(System.out);

        stream.reset();
        Map<String, String> godCards = new HashMap<>();
        godCards.put("Athena", "Opponent’s Turn: If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.");
        godCards.put("Apollo","Your Move: Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated.");
        godCards.put("Artemis", "Your Move: Your Worker may move one additional time, but not back to its initial space.");
        godCards.put("Atlas", "Your Build: Your Worker may build a dome at any level.");
        godCards.put("Demeter", "Your Build: Your Worker may build one additional time, but not on the same space.");
        godCards.put("Hephaestus", "Your Build: Your Worker may build one additional block (not dome) on top of your first block.");
        godCards.put("Minotaur", "Your Move: Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards to an unoccupied space at any level.");
        godCards.put("Pan", "Win Condition: You also win if your Worker moves down two or more levels.");
        godCards.put("Prometheus", "Your Turn: If your Worker does not move up, it may build both before and after moving.");

        GraphicalCardsMenu graphicalCardsMenu = new GraphicalCardsMenu(godCards);
        /*List<String> chosenCards = new ArrayList<>();
        chosenCards.add("Athena");
        chosenCards.add("Apollo");
        chosenCards.add("Pan");
        graphicalCardsMenu.setChosenCards(chosenCards);*/
        stream = new CharStream(graphicalCardsMenu.getRequiredWidth(),graphicalCardsMenu.getRequiredHeight());
        graphicalCardsMenu.setStream(stream);
        graphicalCardsMenu.draw();
        stream.print(System.out);
        stream.reset();


        stream = new CharStream(150, 30);
        GraphicalStartMenu graphicalStartMenu = new GraphicalStartMenu(stream,159, 30);
        graphicalStartMenu.draw();
        stream.print(System.out);
    }
}
