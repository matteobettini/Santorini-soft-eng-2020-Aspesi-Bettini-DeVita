package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;
import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.*;
import java.util.List;

public class ConcreteExposedModel implements ObservableModel {

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
    public void addPacketCardsFromServerObserver(Observer<PacketCardsFromServer> observer) {
        setupManager.addPacketCardsFromServerObserver(observer);
    }

    @Override
    public void addPacketDoActionObserver(Observer<PacketDoAction> observer) {
        setupManager.addPacketDoActionObserver(observer);
        turnLogic.addPacketDoActionObserver(observer);
    }

    @Override
    public void addPacketPossibleBuildsObserver(Observer<PacketPossibleBuilds> observer) {
        turnLogic.addPacketPossibleBuildsObserver(observer);
    }

    @Override
    public void addPacketPossibleMovesObserver(Observer<PacketPossibleMoves> observer) {
        turnLogic.addPacketPossibleMovesObserver(observer);
    }

    @Override
    public void addPacketSetupObserver(Observer<PacketSetup> observer) {
        setupManager.addPacketSetupObserver(observer);
    }

    @Override
    public void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> observer) {
        setupManager.addPacketUpdateBoardObserver(observer);
        turnLogic.addPacketUpdateBoardObserver(observer);
    }
}
