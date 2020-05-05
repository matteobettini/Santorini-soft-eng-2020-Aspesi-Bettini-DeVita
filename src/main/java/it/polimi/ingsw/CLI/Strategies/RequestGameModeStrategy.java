package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;

public interface RequestGameModeStrategy {
    public void handleRequestGameMode(String message, CLI cli);
}
