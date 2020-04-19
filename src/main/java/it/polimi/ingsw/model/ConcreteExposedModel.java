package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;
import it.polimi.ingsw.observe.ObservableInterface;
import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.PacketContainer;

import java.util.List;

public class ConcreteExposedModel implements ObservableInterface<PacketContainer> {

    private final CardFactory factory;
    private final SetupManager setupManager;
    private final TurnLogic turnLogic;
    private final InternalModel internalModel;

    public ConcreteExposedModel(List<String> players, CardFactory cardFactory, boolean isHardCore){
        this.factory = cardFactory;
        this.internalModel = new InternalModel(players, factory, isHardCore);
        this.setupManager = new SetupManager(internalModel, factory.getCards());
        this.turnLogic = new TurnLogic(internalModel);
    }

    @Override
    public void addObserver(Observer<PacketContainer> observer) {
        setupManager.addObserver(observer);
        turnLogic.addObserver(observer);
    }
}
