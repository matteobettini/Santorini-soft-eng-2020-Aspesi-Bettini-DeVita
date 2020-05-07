package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.Board;
import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.GraphicalMatchMenu;
import it.polimi.ingsw.CLI.ViewModel;
import it.polimi.ingsw.packets.PacketSetup;

import java.util.HashMap;
import java.util.Map;

public class DefaultSetupStrategy implements SetupStrategy {
    @Override
    public void handleSetup(PacketSetup packetSetup, CLI cli) {
        ViewModel viewModel = ViewModel.getInstance();
        Board board = viewModel.getBoard();
        GraphicalMatchMenu graphicalMatchMenu = viewModel.getGraphicalMatchMenu();
        viewModel.setIds(packetSetup.getIds());
        viewModel.setPlayersColor(packetSetup.getColors());
        viewModel.setPlayersCards(packetSetup.getCards());
        viewModel.setHardcore(packetSetup.isHardcore());
        cli.setActionStrategy(packetSetup.isHardcore());

        //UPDATE THE MATH MENU
        graphicalMatchMenu.setPlayers(packetSetup.getColors());
        Map<String, String> playersCardAssociation = new HashMap<>();
        for(String player : packetSetup.getCards().keySet()){
            playersCardAssociation.put(player, packetSetup.getCards().get(player).getKey());
        }
        graphicalMatchMenu.setPlayersGodCardAssociation(playersCardAssociation);
    }
}
