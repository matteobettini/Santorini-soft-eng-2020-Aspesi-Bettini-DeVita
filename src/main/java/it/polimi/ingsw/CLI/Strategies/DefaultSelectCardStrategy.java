package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.packets.PacketCardsFromClient;
import it.polimi.ingsw.packets.PacketCardsFromServer;

import java.util.*;

public class DefaultSelectCardStrategy implements SelectCardStrategy {
    @Override
    public void handleCardStrategy(PacketCardsFromServer packetCardsFromServer,boolean isRetry) {

        ViewModel viewModel = ViewModel.getInstance();

        if (!packetCardsFromServer.getTo().equals(viewModel.getPlayerName())) {
            String name = packetCardsFromServer.getTo();
            int num = packetCardsFromServer.getNumberToChoose();
            if (num > 1) System.out.println("\n" + name + " is the challenger and he is choosing " + num + " cards...");
            else System.out.println("\n" + name + " is choosing his card...");
            return;
        }

        GraphicalCardsMenu graphicalCardsMenu = new GraphicalCardsMenu();
        if (packetCardsFromServer.getAllCards() != null) {
            graphicalCardsMenu.setGodCards(packetCardsFromServer.getAllCards());
            //graphicalCardsMenu.setGodCards(board.getAllCards());
            //board.setAllCards(packetCardsFromServer.getAllCards());
        }
        graphicalCardsMenu.setAvailableCards(packetCardsFromServer.getAvailableCards());
        for(String card: packetCardsFromServer.getAvailableCards()) System.out.print(card + " ");
        System.out.println();
        if(!isRetry){
            CharStream stream = new CharStream(graphicalCardsMenu.getRequiredWidth(), graphicalCardsMenu.getRequiredHeight());
            graphicalCardsMenu.setStream(stream);
            graphicalCardsMenu.draw();
            stream.print(System.out);
            stream.reset();
        }


        int number = packetCardsFromServer.getNumberToChoose();
        if (number > 1) System.out.println("\n" + "You are the challenger!");
        String chosenCards;
        List<String> chosenCardsList;
        System.out.println("Choose " + number + " " + (number == 1 ? "card" : "cards (ex. Athena, Apollo, ...)"));
        chosenCards = InputUtilities.getLine();
        if (chosenCards == null) return;
        chosenCardsList = Arrays.asList(chosenCards.split("\\s*,\\s*"));

        PacketCardsFromClient packetCardsFromClient = new PacketCardsFromClient(chosenCardsList);
        viewModel.getClient().send(packetCardsFromClient);
    }
}
