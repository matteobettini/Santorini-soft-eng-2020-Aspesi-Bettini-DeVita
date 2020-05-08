package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.Board;
import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.ViewModel;
import it.polimi.ingsw.packets.PacketSetup;

public class DefaultSetupStrategy implements SetupStrategy {
    @Override
    public void handleSetup(PacketSetup packetSetup, CLI cli) {
        ViewModel viewModel = ViewModel.getInstance();
        Board board = viewModel.getBoard();
        viewModel.setIds(packetSetup.getIds());
        viewModel.setPlayersColor(packetSetup.getColors());
        viewModel.setPlayersCards(packetSetup.getCards());
        viewModel.setHardcore(packetSetup.isHardcore());
        cli.setGameModeStrategy(packetSetup.isHardcore());
    }
}
