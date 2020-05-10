package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.packets.PacketSetup;

public class DefaultSetupStrategy implements SetupStrategy {
    @Override
    public void handleSetup(PacketSetup packetSetup) {
        MatchData matchData = MatchData.getInstance();
        matchData.setIds(packetSetup.getIds());
        matchData.setPlayersColor(packetSetup.getColors());
        matchData.setPlayersCards(packetSetup.getCards());
        matchData.setCounter(packetSetup.getBuildingsCounter());
    }
}
