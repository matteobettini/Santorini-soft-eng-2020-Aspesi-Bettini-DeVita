package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.packets.PacketCardsFromClient;
import it.polimi.ingsw.packets.PacketCardsFromServer;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultSelectCardStrategy implements SelectCardStrategy {
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
        if (packetCardsFromServer.getAllCards() != null) {
            graphicalCardsMenu.setGodCards(packetCardsFromServer.getAllCards());
        }
        graphicalCardsMenu.setAvailableCards(packetCardsFromServer.getAvailableCards());

        int number = packetCardsFromServer.getNumberToChoose();
        if(!isRetry){
            CharStream stream = new CharStream(graphicalCardsMenu.getRequiredWidth(), graphicalCardsMenu.getRequiredHeight());
            graphicalCardsMenu.setStream(stream);
            graphicalCardsMenu.draw();
            stream.print(System.out);
            stream.reset();


            if (number > 1) System.out.println("\n" + "You are the challenger!");
            System.out.print("Choose " + number + " " + (number == 1 ? "card" : "cards (ex. Athena, Apollo, ...)") + ": ");
        }
        else{
            System.out.print("Choose " + number + " valid " + (number == 1 ? "card" : "cards") + ": ");
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
