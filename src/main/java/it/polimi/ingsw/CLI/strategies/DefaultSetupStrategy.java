package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.Board;
import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.packets.PacketSetup;

public class DefaultSetupStrategy implements SetupStrategy {
    @Override
    public void handleSetup(PacketSetup packetSetup) {
        MatchData matchData = MatchData.getInstance();
        Board board = matchData.getBoard();
        matchData.setIds(packetSetup.getIds());
        matchData.setPlayersColor(packetSetup.getColors());
        matchData.setPlayersCards(packetSetup.getCards());
        matchData.setCounter(BuildingType.FIRST_FLOOR, packetSetup.getNUM_OF_FIRST_FLOOR());
        matchData.setCounter(BuildingType.SECOND_FLOOR, packetSetup.getNUM_OF_SECOND_FLOOR());
        matchData.setCounter(BuildingType.THIRD_FLOOR, packetSetup.getNUM_OF_THIRD_FLOOR());
        matchData.setCounter(BuildingType.DOME, packetSetup.getNUM_OF_DOME());
    }
}
