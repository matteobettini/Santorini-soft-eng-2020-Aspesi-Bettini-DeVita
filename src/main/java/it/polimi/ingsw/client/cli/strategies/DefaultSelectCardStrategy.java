package it.polimi.ingsw.client.cli.strategies;

import it.polimi.ingsw.client.cli.graphical.GraphicalCardsMenu;
import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.utilities.InputUtilities;
import it.polimi.ingsw.common.packets.PacketCardsFromClient;
import it.polimi.ingsw.common.packets.PacketCardsFromServer;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultSelectCardStrategy implements SelectCardStrategy {

    /**
     * This handler displays the available cards to the user. If the player is the challenger all the possible cards are displayed
     * through the GraphicalCardsMenu. If not the player can choose one card among the cards previously chosen by the challenger.
     * @param packetCardsFromServer is the packet containing the available cards and the number of choices to make.
     * @param isRetry is true if the cards are requested another time, false otherwise.
     */
    @Override
    public void handleCardStrategy(PacketCardsFromServer packetCardsFromServer,boolean isRetry) {

        MatchData matchData = MatchData.getInstance();

        if (!packetCardsFromServer.getTo().equals(matchData.getPlayerName())) {
            String name = packetCardsFromServer.getTo();
            int num = packetCardsFromServer.getNumberToChoose();
            if (num > 1) System.out.println("\n" + name + " is the challenger and he is choosing " + num + " cards...");
            else System.out.println("\n" + name + " is choosing his card...");
            return;
        }

        GraphicalCardsMenu graphicalCardsMenu = new GraphicalCardsMenu();
        graphicalCardsMenu.setGodCards(packetCardsFromServer.getAllCards());
        matchData.setAllCards(packetCardsFromServer.getAllCards());
        graphicalCardsMenu.setAvailableCards(packetCardsFromServer.getAvailableCards());

        int numberToChoose = packetCardsFromServer.getNumberToChoose();
        if(!isRetry){
            CharStream stream = new CharStream(graphicalCardsMenu.getRequiredWidth(), graphicalCardsMenu.getRequiredHeight());
            graphicalCardsMenu.setStream(stream);
            graphicalCardsMenu.draw();
            stream.print(System.out);
            stream.reset();


            if (numberToChoose > 1) System.out.println("\n" + "You are the challenger!");
            System.out.print("Choose " + numberToChoose + " " + (numberToChoose == 1 ? "card" : "cards (ex. Athena, Apollo, ...)") + ": ");
        }
        else{
            System.out.print("Choose " + numberToChoose + " valid " + (numberToChoose == 1 ? "card" : "cards") + ": ");
        }

        List<String> chosenCardsList;
        String chosenCards;
        chosenCards = InputUtilities.getLine();
        if (chosenCards == null) return;
        chosenCardsList = Arrays.stream(chosenCards.split("\\s*,\\s*")).map(s -> Character.toString(s.charAt(0)).toUpperCase().concat(s.substring(1).toLowerCase())).collect(Collectors.toList());

        PacketCardsFromClient packetCardsFromClient = new PacketCardsFromClient(chosenCardsList);
        matchData.getClient().send(packetCardsFromClient);
    }
}
