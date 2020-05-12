package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.packets.PacketSetup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
