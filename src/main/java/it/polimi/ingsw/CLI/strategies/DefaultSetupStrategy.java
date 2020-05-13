package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.packets.PacketSetup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSetupStrategy implements SetupStrategy {
    /**
     * This handler sets all the info received form the server in the MatchData instance.
     * @param packetSetup is the packet containing info such as the players and workers' ids, their colors,
     * the association between players and their cards and the buildings' counter.
     */
    @Override
    public void handleSetup(PacketSetup packetSetup) {
        MatchData matchData = MatchData.getInstance();
        matchData.setIds(packetSetup.getIds());
        matchData.setPlayersColor(packetSetup.getColors());
        matchData.setPlayersCards(packetSetup.getCards());
        matchData.setCounter(packetSetup.getBuildingsCounter());
    }
}
