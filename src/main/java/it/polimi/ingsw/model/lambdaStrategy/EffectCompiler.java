package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.cardReader.RuleEffect;
import it.polimi.ingsw.cardReader.enums.EffectType;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerState;
import it.polimi.ingsw.model.exceptions.DomeException;
import it.polimi.ingsw.model.exceptions.NoWorkerPresentException;
import it.polimi.ingsw.model.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerLostSignal;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerWonSignal;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Translates Rule Effects into Compiled Effects
 */
public class EffectCompiler {

    /** Compiles the given effect
     * @param model the internal model is needed to incapsulate it in the lambdas
     * @param effect the effect to compile
     * @param owner the owner of the rule in which the effect is
     * @return the compiled effect
     */
    public static LambdaEffect compileEffect(InternalModel model, RuleEffect effect) {

        EffectType effectType = effect.getType();
        LambdaEffect compiledEffect = null;

        switch (effectType){
            case ALLOW:
                compiledEffect = compileAllowEffect(model, effect);
                break;
            case SET_OPPONENT_POSITION:
                compiledEffect = compileSetOpponentPositionEffect(model, effect);
                break;
            case DENY:
                compiledEffect = ((moveData, buildData, simulate) -> {
                    if(moveData == null)
                        throw new PlayerLostSignal(buildData.getPlayer());
                    else
                        throw new PlayerLostSignal(moveData.getPlayer());
                });
                break;
            case WIN:
                compiledEffect = ((moveData, buildData, simulate) -> {
                    if (moveData == null)
                        throw new PlayerWonSignal(buildData.getPlayer());
                    else
                        throw new PlayerWonSignal(moveData.getPlayer());
                });
                break;
        }
        return compiledEffect;
    }


    private static LambdaEffect compileAllowEffect(InternalModel model, RuleEffect effect) {


        PlayerState nextPlayerState = effect.getNextState();

        LambdaEffect lambdaEffect = (moveData, buildData, simulate) -> {

            if(buildData == null) {

                // All the player moves
                List<Point> moves = moveData.getData();
                Point startPosition = moveData.getWorker().getPosition();
                Point finalPosition = moves.get(moves.size() - 1);
                Cell startPositionCell = model.getBoard().getCell(startPosition);
                Cell finalPositionCell = model.getBoard().getCell(finalPosition);
                Worker myWoker = moveData.getWorker();


                try {
                    // Where i want to go should be without workers (should be already tested)
                    finalPositionCell.getWorkerID();
                    return false;
                } catch (NoWorkerPresentException e) {
                    // It is without workers

                    // If i want to go on a dome it fails (should be already tested)
                    if(finalPositionCell.getTopBuilding() == LevelType.DOME)
                        return false;

                    // If we are not in a simulation
                    if(!simulate){
                        try {
                            // Set my worker in final cell
                            finalPositionCell.setWorker(myWoker.getID());
                        } catch (WorkerAlreadyPresentException | DomeException ignored) { }
                        try {
                            // remove my worker from previous position
                            startPositionCell.removeWorker();
                        } catch (NoWorkerPresentException ignored) {
                            System.err.println("There is no one in the cell my move is starting from , i am the allow effect on move of worker "+ moveData.getWorker().getID());
                            return false;
                        }
                        // Set my new worker's position
                        myWoker.setPosition(finalPosition);

                        // Set the new player state
                        moveData.getPlayer().setPlayerState(nextPlayerState);
                    }
                    return true;
                }
            }

            else if(moveData == null){


                Map<Point, List<BuildingType>> builds = buildData.getData();
                Iterator<Point> buildingPos = builds.keySet().iterator();
                List<BuildingType> allBuildingsIWantToBuild = new ArrayList<>();

                // CHeck i can build the chosen buildings in the chosen cells
                while(buildingPos.hasNext()){
                    Point whereIWantToBuild = buildingPos.next();
                    List<BuildingType> whatIWantToBuildHere = builds.get(whereIWantToBuild);
                    allBuildingsIWantToBuild.addAll(whatIWantToBuildHere);
                    if(!model.getBoard().getCell(whereIWantToBuild).canBuild(whatIWantToBuildHere))
                        return false;
                }

                long numOfFirstFloorsIWantToUse = allBuildingsIWantToBuild.stream()
                        .filter((buildingType -> buildingType == BuildingType.FIRST_FLOOR))
                        .count();
                long numOfSecondFloorsIWantToUse = allBuildingsIWantToBuild.stream()
                        .filter((buildingType -> buildingType == BuildingType.SECOND_FLOOR))
                        .count();
                long numOfThirdFloorsIWantToUse = allBuildingsIWantToBuild.stream()
                        .filter((buildingType -> buildingType == BuildingType.THIRD_FLOOR))
                        .count();
                long numOfDomesIWantToUse = allBuildingsIWantToBuild.stream()
                        .filter((buildingType -> buildingType == BuildingType.DOME))
                        .count();

                if(numOfFirstFloorsIWantToUse > model.getBoard().availableBuildings(BuildingType.FIRST_FLOOR))
                    return false;
                if(numOfSecondFloorsIWantToUse > model.getBoard().availableBuildings(BuildingType.SECOND_FLOOR))
                    return false;
                if(numOfThirdFloorsIWantToUse > model.getBoard().availableBuildings(BuildingType.THIRD_FLOOR))
                    return false;
                if(numOfDomesIWantToUse > model.getBoard().availableBuildings(BuildingType.DOME))
                    return false;


                if(!simulate){
                    buildingPos = builds.keySet().iterator();
                    while(buildingPos.hasNext()){
                        Point whereIWantToBuild = buildingPos.next();
                        List<BuildingType> whatIWantToBuildHere = builds.get(whereIWantToBuild);
                        for(BuildingType b : whatIWantToBuildHere)
                            if(!model.getBoard().getCell(whereIWantToBuild).addBuilding(b)) {
                                System.err.println("L'effetto allow build del worker " + buildData.getWorker().getID() + "nell applicazione dell'effetto ha trovato cose diverse da quelle che ha checkato nell'effetto");
                                return false;
                            }
                    }
                    Board board = model.getBoard();
                    for( int i=0; i < numOfFirstFloorsIWantToUse; i++)
                        board.useBuilding(BuildingType.FIRST_FLOOR);
                    for( int i=0; i < numOfSecondFloorsIWantToUse; i++)
                        board.useBuilding(BuildingType.SECOND_FLOOR);
                    for( int i=0; i < numOfThirdFloorsIWantToUse; i++)
                        board.useBuilding(BuildingType.THIRD_FLOOR);
                    for( int i=0; i < numOfDomesIWantToUse; i++)
                        board.useBuilding(BuildingType.DOME);

                    // Set next player state
                    buildData.getPlayer().setPlayerState(nextPlayerState);
                }

                return true;

            }
            return false;
        };
        return  lambdaEffect;
    }



    private static LambdaEffect compileSetOpponentPositionEffect(InternalModel model, RuleEffect effect) {

        PlayerState nextPlayerState = effect.getNextState();
        LambdaEffect lambdaEffect = null;

        if (effect.getData().equals("PUSH_STRAIGHT")){
            lambdaEffect = ((moveData, buildData, simulate) -> {

                assert(buildData == null);

                List<Point> moves = moveData.getData();
                Point finalPosition = moves.get(moves.size()-1);
                Cell whereIWannaGo = model.getBoard().getCell(finalPosition);
                Point mySecondToLastPosition;
                if(moves.size() > 1)
                    mySecondToLastPosition = moves.get(moves.size()-2);
                else
                    mySecondToLastPosition = moveData.getWorker().getPosition();

                int deltaX = finalPosition.x - mySecondToLastPosition.x;
                int deltaY = finalPosition.y - mySecondToLastPosition.y;

                Cell whereHeHasToGo = model.getBoard().getCell(new Point(finalPosition.x-deltaX,finalPosition.y-deltaY));

                if(whereHeHasToGo == null || whereHeHasToGo.getTopBuilding() == LevelType.DOME)
                    return false;

                try {
                    whereHeHasToGo.getWorkerID();
                    return false;
                } catch (NoWorkerPresentException e) {
                    try {
                        whereIWannaGo.getWorkerID();
                    } catch (NoWorkerPresentException ex) {
                        System.err.println("There is no one in the cell i want to switch my worker with , i am the set opp pos effect of worker "+ moveData.getWorker().getID());
                        return false;
                    }
                    if (!simulate) {
                        String hisWorkerName = null;
                        try {
                            hisWorkerName = whereIWannaGo.getWorkerID();
                        } catch (NoWorkerPresentException ignored) { }

                        Worker hisWorker = model.getWorkerByID(hisWorkerName);

                        try {
                            whereHeHasToGo.setWorker(hisWorkerName);
                        } catch (WorkerAlreadyPresentException | DomeException ignored) { }
                        hisWorker.setPosition(whereHeHasToGo.getPosition());
                        try {
                            whereIWannaGo.removeWorker();
                        } catch (NoWorkerPresentException ignored) { }
                        try {
                            model.getBoard().getCell(moveData.getWorker().getPosition()).removeWorker();
                        } catch (NoWorkerPresentException ignored) { }
                        try {
                            whereIWannaGo.setWorker(moveData.getWorker().getID());
                        } catch (WorkerAlreadyPresentException | DomeException ignored) { }
                        moveData.getWorker().setPosition(finalPosition);
                        moveData.getPlayer().setPlayerState(nextPlayerState);
                    }
                    return true;
                }
            });
        }
        else if(effect.getData().equals("SWAP")){
             lambdaEffect = ((moveData, buildData, simulate) -> {

                 assert(buildData == null);

                 List<Point> moves = moveData.getData();


                 Point finalPosition = moves.get(moves.size()-1);
                 Cell whereIWannaGoCell = model.getBoard().getCell(finalPosition);
                 Point mySecondToLastPosition;
                 if(moves.size() > 1)
                     mySecondToLastPosition = moves.get(moves.size()-2);
                 else
                     mySecondToLastPosition = moveData.getWorker().getPosition();
                 Cell mySecondToLastCell = model.getBoard().getCell(mySecondToLastPosition);

                 if (!simulate) {
                     Worker hisWorker = null;
                     try {
                         mySecondToLastCell.removeWorker();
                     } catch (NoWorkerPresentException ignored) { }
                     try {
                         hisWorker = model.getWorkerByID(whereIWannaGoCell.getWorkerID());
                     } catch (NoWorkerPresentException ignored) { }
                     try {
                         whereIWannaGoCell.removeWorker();
                     } catch (NoWorkerPresentException ignored) { }
                     try {
                         whereIWannaGoCell.setWorker(moveData.getWorker().getID());
                     } catch (WorkerAlreadyPresentException | DomeException ignored) { }
                     try {
                         assert (hisWorker != null);
                         mySecondToLastCell.setWorker(hisWorker.getID());
                     } catch (WorkerAlreadyPresentException | DomeException ignored) { }
                     moveData.getWorker().setPosition(finalPosition);
                     hisWorker.setPosition(mySecondToLastPosition);
                     moveData.getPlayer().setPlayerState(nextPlayerState);
                 }

                 return true;
            });
        }
        return lambdaEffect;
    }

}