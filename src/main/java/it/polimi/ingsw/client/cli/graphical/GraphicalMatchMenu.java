package it.polimi.ingsw.client.cli.graphical;


import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.client.cli.graphical.buildings.BuildingFactory;
import it.polimi.ingsw.client.cli.utilities.colors.BackColor;
import it.polimi.ingsw.client.cli.utilities.colors.ForeColor;
import it.polimi.ingsw.client.cli.utilities.OutputUtilities;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.common.utils.Pair;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class GraphicalMatchMenu implements CharFigure {

    private final CharStream stream;
    private boolean gameOver;
    private boolean youWin;
    private static final int playersBoxWidth = 35;
    private static final int playersBoxHeight = 8;
    private static final BackColor playersBoxColor = BackColor.ANSI_BG_BLUE;
    private static final int buildingsBoxWidth = 35;
    private static final int buildingsBoxHeight = 25;
    private static final BackColor buildingsBoxColor = BackColor.ANSI_BG_RED;
    private static final int gameOverBoxWidth = 69;
    private static final int gameOverBoxHeight = 16;
    private static final BackColor gameOverBoxColor = BackColor.ANSI_BG_BLACK;
    private static final int youWinBoxWidth = 64;
    private static final int youWinBoxHeight = 16;
    private static final BackColor youWinBoxColor = BackColor.ANSI_BRIGHT_BG_BLUE;
    private static final int marginX_MessageEndGame = 50;
    private static final int marginY_MessageEndGame = 15;
    private static final int maximumPlayerNameLength = 15;
    private static final int maximumGodNameLength = 15;
    private static final int marginGameOverY = 18;
    private static final int marginGameOverX = 68;
    private static final int marginYouWinY = 18;
    private static final int marginYouWinX = 70;
    private static final int buildingsMenuRatioX = 20;
    private static final int buildingsMenuRatioY = 8;
    private static final int playersBoxMarginX = 5;
    private static final int playersBoxMarginY = 5;
    private static final int buildingsBoxMarginX = 5;
    private static final int buildingsBoxMarginY = 18;
    private static final int playerNameMarginX = 5;
    private static final int godNameMarginX = 20;
    private static final int FF_WIDTH = 18;
    private static final int FF_HEIGHT = 7;
    private static final ForeColor characterMessageColor = ForeColor.ANSI_BLACK;
    private static final BackColor gameOverMessageColor = BackColor.ANSI_BRIGHT_BG_RED;
    private static final BackColor youWinMessageColor = BackColor.ANSI_BRIGHT_BG_YELLOW;
    private static final BackColor lostIconColor = BackColor.ANSI_BRIGHT_BG_RED;
    private static final BackColor inactiveIconColor = BackColor.ANSI_BG_YELLOW;
    private static final BackColor activeIconColor = BackColor.ANSI_BRIGHT_BG_GREEN;



    /**
     * This constructor initializes the stream where the GraphicalMatchMenu print itself.
     * @param stream is the CharStream instanced used to print.
     */
    public GraphicalMatchMenu(CharStream stream){
        this.stream = stream;
        this.gameOver = false;
        this.youWin = false;
    }

    /**
     * This method is used to set the game over message.
     * @param set is true if the message has to be printed, false otherwise.
     */
    public void setGameOver(boolean set){
        this.gameOver = set;
    }

    /**
     * This method is used to set the you win message.
     * @param set is true if the message has to be printed, false otherwise.
     */
    public void setYouWin(boolean set){
        this.youWin = set;
    }

    /**
     * This method draws the GraphicalMatchMenu on the stream.
     */
    @Override
    public void draw() {
        draw(CharStream.defaultX, CharStream.defaultY);
    }

    /**
     * This method draws the GraphicalMatchMenu on the stream.
     * The drawn elements are:
     * - A box containing the players' nickname, their status (green during their turn, false it's not adn red if they have lost),
     * and the associated god card.
     * - A box containing the number of available buildings.
     * - A box containing the message of game over if set.
     * - A box containing the message of you win if set.
     */
    @Override
    public void draw(int relX, int relY) {

        printPlayersBox(relX, relY);

        printAvailableBuildings(relX, relY);

        if(gameOver) printGameOver(relX, relY);

        if(youWin) printYouWin(relX, relY);

    }

    /**
     * This method draws the message 'You Win' if set.
     * @param relX is the menu's X coordinate.
     * @param relY is the menu's Y coordinate.
     */
    private void printYouWin(int relX, int relY){
        GraphicalPane gameOverBox = new GraphicalPane(stream, youWinBoxWidth, youWinBoxHeight, youWinBoxColor);
        gameOverBox.draw(relX + marginX_MessageEndGame, relY + marginY_MessageEndGame);
        String title = "YOU";
        stream.setMessage(title, relX + marginYouWinX, relY + marginYouWinY, characterMessageColor, youWinMessageColor, youWinBoxColor);

        title = "WIN!";
        stream.setMessage(title, relX + marginYouWinX - 3, relY + marginYouWinY + 5, characterMessageColor, youWinMessageColor, youWinBoxColor);
    }

    /**
     * This method draws the message 'Game Over' if set.
     * @param relX is the menu's X coordinate.
     * @param relY is the menu's Y coordinate.
     */
    private void printGameOver(int relX, int relY){
        GraphicalPane gameOverBox = new GraphicalPane(stream, gameOverBoxWidth, gameOverBoxHeight, gameOverBoxColor);
        gameOverBox.draw(relX + marginX_MessageEndGame, relY + marginY_MessageEndGame);
        String title = "GAME";
        stream.setMessage(title, relX + marginGameOverX, relY + marginGameOverY, characterMessageColor, gameOverMessageColor, gameOverBoxColor);
        title = "OVER";
        stream.setMessage(title, relX + marginGameOverX, relY + marginGameOverY + 5, characterMessageColor, gameOverMessageColor, gameOverBoxColor);
    }

    /**
     * This method draws the box of the available buildings.
     * @param relX is the menu's X coordinate.
     * @param relY is the menu's Y coordinate.
     */
    private void printAvailableBuildings(int relX, int relY){
        MatchData matchData = MatchData.getInstance();
        Map<BuildingType, Integer>  buildingsCounter = matchData.getBuildingsTempCounter();

        int counterMarginX = 17;

        GraphicalPane buildingsBox = new GraphicalPane(stream, buildingsBoxWidth, buildingsBoxHeight, buildingsBoxColor);
        buildingsBox.draw(relX + buildingsBoxMarginX, relY + buildingsBoxMarginY);
        stream.addString(relX + buildingsBoxMarginX + 9, relY + buildingsBoxMarginY + 1, "AVAILABLE BUILDINGS", buildingsBoxColor);
        CharFigure dome = BuildingFactory.getBuilding(stream, BuildingType.DOME, buildingsMenuRatioX, buildingsMenuRatioY);
        if(dome != null) dome.draw(relX + buildingsBoxMarginX + 7,relY + buildingsBoxMarginY);
        stream.addString(relX + buildingsBoxMarginX + counterMarginX, relY + buildingsBoxMarginY + 4, buildingsCounter.get(BuildingType.DOME).toString(), BuildingFactory.domeColor);
        CharFigure third = BuildingFactory.getBuilding(stream, BuildingType.THIRD_FLOOR, buildingsMenuRatioX, buildingsMenuRatioY);
        stream.addString(relX + buildingsBoxMarginX + counterMarginX, relY + buildingsBoxMarginY + 8, buildingsCounter.get(BuildingType.THIRD_FLOOR).toString());
        if(third != null) third.draw(relX + buildingsBoxMarginX + 7,relY + buildingsBoxMarginY + 4);
        CharFigure second = BuildingFactory.getBuilding(stream, BuildingType.SECOND_FLOOR, buildingsMenuRatioX, buildingsMenuRatioY);
        stream.addString(relX + buildingsBoxMarginX + counterMarginX, relY + buildingsBoxMarginY + 13, buildingsCounter.get(BuildingType.SECOND_FLOOR).toString());
        if(second != null) second.draw(relX + buildingsBoxMarginX + 8,relY + buildingsBoxMarginY + 9);
        GraphicalPane first = new GraphicalPane(stream, FF_WIDTH, FF_HEIGHT, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_WHITE);
        first.draw(relX + buildingsBoxMarginX + 9,relY + buildingsBoxMarginY + 17);
        stream.addString(relX + buildingsBoxMarginX + counterMarginX, relY + buildingsBoxMarginY + 20, buildingsCounter.get(BuildingType.FIRST_FLOOR).toString(), BuildingFactory.floorColor);
    }

    /**
     * This method draws the players's with their nicknames, their status and their god cards.
     * @param relX is the menu's X coordinate.
     * @param relY is the menu's Y coordinate.
     */
    private void printPlayersBox(int relX, int relY){
        MatchData matchData = MatchData.getInstance();
        Map<String, Color> playersColor = matchData.getPlayersColor();
        Map<String, Pair<String, String>> playersGodCardAssociation = matchData.getPlayersCards();
        List<String> losers = matchData.getLosers();
        String activePlayer = matchData.getCurrentActivePlayer();

        GraphicalPane playersBox = new GraphicalPane(stream, playersBoxWidth, playersBoxHeight, playersBoxColor);
        playersBox.draw(relX + playersBoxMarginX , relY + playersBoxMarginY);
        stream.addString(relX + playersBoxMarginX + playerNameMarginX, relY + playersBoxMarginY + 1, "PLAYER", playersBoxColor);
        stream.addString(relX + playersBoxMarginX + godNameMarginX, relY + playersBoxMarginY + 1, "GOD CARD", playersBoxColor);
        int nextLine = playersBoxMarginY + 3;
        for(String player : playersColor.keySet()){

            if(activePlayer != null && !activePlayer.equals(player)){
                if(losers.contains(player)){
                    stream.addColor(relX + playersBoxMarginX + 2, relY + nextLine, lostIconColor);
                    stream.addColor(relX + playersBoxMarginX + 3, relY + nextLine, lostIconColor);
                }
                else{
                    stream.addColor(relX + playersBoxMarginX + 2, relY + nextLine, inactiveIconColor);
                    stream.addColor(relX + playersBoxMarginX + 3, relY + nextLine, inactiveIconColor);
                }
            }
            else {
                stream.addColor(relX + playersBoxMarginX + 2, relY + nextLine, activeIconColor);
                stream.addColor(relX + playersBoxMarginX + 3, relY + nextLine, activeIconColor);
            }
            String godCard = playersGodCardAssociation.get(player).getFirst();
            if(godCard.length() >= maximumGodNameLength){
                godCard = godCard.substring(0, maximumGodNameLength - "...".length());
                godCard = godCard.concat("...");
            }
            stream.addString(relX + playersBoxMarginX + godNameMarginX, relY + nextLine, godCard, playersBoxColor);

            BackColor col = OutputUtilities.fromColorToBackColor(playersColor.get(player));

            if(player.length() >= maximumPlayerNameLength){
                player = player.substring(0, maximumPlayerNameLength - "...".length());
                player = player.concat("...");
            }

            stream.addString(relX + playersBoxMarginX + playerNameMarginX, relY + nextLine, player, col);
            nextLine += 2;
        }
    }
}