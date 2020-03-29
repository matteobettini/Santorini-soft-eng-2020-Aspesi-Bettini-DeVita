package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.cardReader.RuleEffect;
import it.polimi.ingsw.cardReader.enums.EffectType;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
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
import java.util.stream.Collectors;

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
    public static LambdaEffect compileEffect(InternalModel model, RuleEffect effect, Player owner) {

        EffectType effectType = effect.getType();
        LambdaEffect compiledEffect = null;
        switch (effectType){
            case ALLOW:
                compiledEffect = compileAllowEffect(model, effect);
                break;
            case SET_OPPONENT_POSITION:
                compiledEffect = compileSetOpponentPositionEffect(model, effect, owner);
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

                List<Point> moves = moveData.getData();

                try {
                    model.getBoard().getCell(moves.get(moves.size() - 1)).getWorkerID();
                    return false;
                } catch (NoWorkerPresentException e) {
                    if(model.getBoard().getCell(moves.get(moves.size() - 1)).getTopBuilding() == LevelType.DOME)
                        return false;
                    if(!simulate){
                        try {
                            model.getBoard().getCell(moves.get(moves.size()-1)).setWorker(moveData.getWorker().getID());
                        } catch (WorkerAlreadyPresentException | DomeException ignored) { }
                        try {
                            model.getBoard().getCell(moveData.getWorker().getPosition()).removeWorker();
                        } catch (NoWorkerPresentException ignored) {
                            System.err.println("There is no one in the cell my move is starting from , i am the allow effect on move of worker "+ moveData.getWorker().getID());
                            return false;
                        }
                        moveData.getWorker().setPosition(moves.get(moves.size()-1));
                        moveData.getPlayer().setPlayerState(nextPlayerState);
                    }
                    return true;
                }
            }

            else if(moveData == null){


                Map<Point, List<BuildingType>> builds = buildData.getData();
                Iterator<Point> buildingPos = builds.keySet().iterator();
                List<BuildingType> allBuildingsIWantToBuild = new ArrayList<>();

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
                long numOfDomesFloorsIWantToUse = allBuildingsIWantToBuild.stream()
                        .filter((buildingType -> buildingType == BuildingType.DOME))
                        .count();

                if(numOfFirstFloorsIWantToUse > model.getBoard().availableBuildings(BuildingType.FIRST_FLOOR))
                    return false;
                if(numOfSecondFloorsIWantToUse > model.getBoard().availableBuildings(BuildingType.SECOND_FLOOR))
                    return false;
                if(numOfThirdFloorsIWantToUse > model.getBoard().availableBuildings(BuildingType.THIRD_FLOOR))
                    return false;
                if(numOfDomesFloorsIWantToUse > model.getBoard().availableBuildings(BuildingType.DOME))
                    return false;


                if(!simulate){
                    buildingPos = builds.keySet().iterator();
                    while(buildingPos.hasNext()){
                        Point whereIWantToBuild = buildingPos.next();
                        List<BuildingType> whatIWantToBuildHere = builds.get(whereIWantToBuild);
                        for(BuildingType b : whatIWantToBuildHere)
                            if(!model.getBoard().getCell(whereIWantToBuild).addBuilding(b)) {
                                System.err.println("L'effetto allow build del worker " + buildData.getWorker().getID() + "nell applicazione ha trovato cose diverse da quelle che ha checkato");
                                return false;
                            }
                    }
                    buildData.getPlayer().setPlayerState(nextPlayerState);
                }

                return true;

            }
            return true;
        };
        return  lambdaEffect;
    }



    private static LambdaEffect compileSetOpponentPositionEffect(InternalModel model, RuleEffect effect, Player owner) {

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