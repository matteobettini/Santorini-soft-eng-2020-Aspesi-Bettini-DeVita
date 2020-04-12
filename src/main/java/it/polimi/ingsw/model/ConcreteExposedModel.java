package it.polimi.ingsw.model;

import it.polimi.ingsw.TurnLogic;
import it.polimi.ingsw.model.cardReader.CardFactory;

import java.util.List;

public class ConcreteExposedModel {

    private final List<String> players;
    private final CardFactory factory;
    private final SetupManager setupManager;
    private final TurnLogic turnLogic;

    public ConcreteExposedModel(InternalModel model, List<String> players, CardFactory factory){
        this.players = players;
        this.factory = factory;
        this.setupManager = new SetupManager(model, factory.getCards());
        this.turnLogic = new TurnLogic(model);
    }

}
