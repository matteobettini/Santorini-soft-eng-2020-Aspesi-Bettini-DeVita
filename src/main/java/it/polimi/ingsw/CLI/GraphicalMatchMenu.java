package it.polimi.ingsw.CLI;


import it.polimi.ingsw.CLI.buildings.BuildingFactory;
import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;
import it.polimi.ingsw.model.enums.BuildingType;
import javafx.util.Pair;

import java.awt.*;
import java.util.Map;

public class GraphicalMatchMenu implements CharFigure{

    private final CharStream stream;
    //private Map<String, Color> players;
    //private Map<String, String> playersGodCardAssociation;
    //private String loser;
    //private Map<BuildingType, Integer> buildingsCounter;
    private boolean gameOver;
    private boolean youWin;

    public GraphicalMatchMenu(CharStream stream){
        this.stream = stream;
        this.gameOver = false;
        this.youWin = false;
    }

    public void setGameOver(boolean set){
        this.gameOver = set;
    }

    public void setYouWin(boolean set){
        this.youWin = set;
    }

    @Override
    public void draw() {
        draw(0, 0);
    }

    @Override
    public void draw(int relX, int relY) {
        MatchData matchData = MatchData.getInstance();
        Map<String, Color> players = matchData.getPlayersColor();
        Map<String, Pair<String, String>> playersGodCardAssociation = matchData.getPlayersCards();
        Map<BuildingType, Integer>  buildingsCounter = matchData.getBuildingsCounter();
        String loser = matchData.getLoser();
        String activePlayer = matchData.getCurrentActivePlayer();

        if(players.isEmpty() || playersGodCardAssociation.isEmpty()) return;

        BackColor col;
        int nextLine = 5;
        GraphicalPane playersBox = new GraphicalPane(stream, 35, 8, BackColor.ANSI_BG_BLUE);
        playersBox.draw(relX + 5 , relY + nextLine);
        stream.addString(relX + 10, relY + nextLine + 1, "PLAYERS", BackColor.ANSI_BG_BLUE);
        stream.addString(relX + 25, relY + + nextLine + 1, "GODCARD", BackColor.ANSI_BG_BLUE);
        nextLine += 3;
        for(String player : players.keySet()){
            if(players.get(player).equals(Color.CYAN)){
                col = BackColor.ANSI_BRIGHT_BG_CYAN;
            }
            else if(players.get(player).equals(Color.WHITE)){
                col = BackColor.ANSI_BG_PURPLE;
            }
            else if(players.get(player).equals(Color.ORANGE)){
                col = BackColor.ANSI_BG_YELLOW;
            }
            else col = BackColor.ANSI_BG_WHITE;
            if(activePlayer != null && !activePlayer.equals(player)){
                if(loser != null && loser.equals(player)){
                    stream.addColor(relX + 7, relY + nextLine, BackColor.ANSI_BRIGHT_BG_RED);
                    stream.addColor(relX + 8, relY + + nextLine, BackColor.ANSI_BRIGHT_BG_RED);
                }
                else{
                    stream.addColor(relX + 7, relY + nextLine, BackColor.ANSI_BRIGHT_BG_YELLOW);
                    stream.addColor(relX + 8, relY + nextLine, BackColor.ANSI_BRIGHT_BG_YELLOW);
                }
            }
            else {
                stream.addColor(relX + 7, relY + nextLine, BackColor.ANSI_BRIGHT_BG_GREEN);
                stream.addColor(relX + 8, relY + nextLine, BackColor.ANSI_BRIGHT_BG_GREEN);
            }
            String godCard = playersGodCardAssociation.get(player).getKey();
            if(godCard.length() >= 15){
                godCard = godCard.substring(0, 12);
                godCard = godCard.concat("...");
            }
            stream.addString(relX + 25, relY + nextLine, godCard, BackColor.ANSI_BG_BLUE);
            if(player.length() >= 15){
                player = player.substring(0, 12);
                player = player.concat("...");
            }
            stream.addString(relX + 10, relY + nextLine, player, col);
            nextLine += 2;
        }

        GraphicalPane buildingsBox = new GraphicalPane(stream, 35, 25, BackColor.ANSI_BG_RED);
        buildingsBox.draw(relX + 5, relY + 18);
        stream.addString(relX + 14, relY + 19, "AVAILABLE BUILDINGS", BackColor.ANSI_BG_RED);
        CharFigure dome = BuildingFactory.getBuilding(stream, BuildingType.DOME, 20, 8);
        if(dome != null) dome.draw(relX + 12,relY + 18);
        stream.addString(relX + 22, relY + 22, buildingsCounter.get(BuildingType.DOME).toString(), BackColor.ANSI_BG_BLUE);
        CharFigure third = BuildingFactory.getBuilding(stream, BuildingType.THIRD_FLOOR, 20, 8);
        stream.addString(relX + 22, relY + 26, buildingsCounter.get(BuildingType.THIRD_FLOOR).toString());
        if(third != null) third.draw(relX + 12,relY + 22);
        CharFigure second = BuildingFactory.getBuilding(stream, BuildingType.SECOND_FLOOR, 20, 8);
        stream.addString(relX + 22, relY + 31, buildingsCounter.get(BuildingType.SECOND_FLOOR).toString());
        if(second != null) second.draw(relX + 13,relY + 27);
        GraphicalPane first = new GraphicalPane(stream, 18, 7, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_WHITE);
        first.draw(relX + 14,relY + 35);
        stream.addString(relX + 22, relY + 38, buildingsCounter.get(BuildingType.FIRST_FLOOR).toString(), BackColor.ANSI_BG_WHITE);

        if(gameOver){
            int marginY = 18;
            int marginX = 68;
            GraphicalPane gameOverBox = new GraphicalPane(stream, 69, 16, BackColor.ANSI_BG_BLACK);
            gameOverBox.draw(relX + 50, relY + 15);
            String title = "GAME";
            stream.setMessage(title, relX + marginX, relY + marginY, ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_RED, BackColor.ANSI_BG_BLACK);
            marginY += 5;
            title = "OVER";
            stream.setMessage(title, relX + marginX, relY + marginY, ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_RED, BackColor.ANSI_BG_BLACK);
        }

        if(youWin){
            int marginY = 18;
            int marginX = 70;
            GraphicalPane gameOverBox = new GraphicalPane(stream, 64, 16, BackColor.ANSI_BRIGHT_BG_BLUE);
            gameOverBox.draw(relX + 50, relY + 15);
            String title = "YOU";
            stream.setMessage(title, relX + marginX, relY + marginY, ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_YELLOW, BackColor.ANSI_BRIGHT_BG_BLUE);

            marginX -= 3;
            marginY += 5;
            title = "WIN!";
            stream.setMessage(title, relX + marginX, relY + marginY, ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_YELLOW, BackColor.ANSI_BRIGHT_BG_BLUE);
        }

    }
}