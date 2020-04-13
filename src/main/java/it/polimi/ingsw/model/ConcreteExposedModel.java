package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;

import java.util.List;

public class ConcreteExposedModel {

    private final CardFactory factory;
    private final SetupManager setupManager;
    private final TurnLogic turnLogic;
    private final InternalModel internalModel;

    public ConcreteExposedModel(List<String> players, CardFactory cardFactory){
        this.factory = cardFactory;
        this.internalModel = new InternalModel(players, factory);
        this.setupManager = new SetupManager(internalModel, factory.getCards());
        this.turnLogic = new TurnLogic(internalModel);
    }

}
