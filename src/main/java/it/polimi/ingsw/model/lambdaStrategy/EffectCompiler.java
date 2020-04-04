package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.model.cardReader.RuleEffect;
import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerState;
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
     * @return the compiled effect
     */
    public static LambdaEffect compileEffect(InternalModel model, RuleEffect effect) {

        assert (model != null && effect != null);

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
                    if (!simulate) {
                        if (moveData == null)
                            throw new PlayerLostSignal(buildData.getPlayer());
                        else
                            throw new PlayerLostSignal(moveData.getPlayer());
                    }
                    return true;
                });
                break;
            case WIN:
                compiledEffect = ((moveData, buildData, simulate) -> {
                    if (!simulate) {
                        if (moveData == null)
                            throw new PlayerWonSignal(buildData.getPlayer());
                        else
                            throw new PlayerWonSignal(moveData.getPlayer());
                    }
                    return true;
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
                Worker myWorker = moveData.getWorker();



                // Where i want to go should be without workers and domes (should be already tested)
                assert(!finalPositionCell.isOccupied());

                // Check i am where i want to start the move
                assert(startPositionCell.getWorkerID().equals(myWorker.getID()));


                // If we are not in a simulation
                if(!simulate){

                    // Set my worker in final cell
                    finalPositionCell.setWorker(myWorker.getID());

                    // remove my worker from previous position
                    if(!startPositionCell.removeWorker())
                        System.err.println("There is no one in the cell my move is starting from , i am the allow effect on move of worker "+ moveData.getWorker().getID());

                    // Set my new worker's position
                    myWorker.setPosition(finalPosition);

                    // Set the new player state
                    moveData.getPlayer().setPlayerState(nextPlayerState);

                }
                return true;
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

                Point startPosition = moveData.getWorker().getPosition();
                Cell startPositionCell = model.getBoard().getCell(startPosition);
                List<Point> moves = moveData.getData();
                Point finalPosition = moves.get(moves.size()-1);
                Cell finalPositionCell = model.getBoard().getCell(finalPosition);
                Point mySecondToLastPosition;
                Worker myWorker = moveData.getWorker();
                Worker hisWorker = model.getWorkerByID(finalPositionCell.getWorkerID());
                if(moves.size() > 1)
                    mySecondToLastPosition = moves.get(moves.size()-2);
                else
                    mySecondToLastPosition = startPosition;

                // Check i am where i want to start the move
                assert(startPositionCell.getWorkerID().equals(myWorker.getID()));

                // Check in my final pos there is not a dome
                assert (finalPositionCell.getTopBuilding() != LevelType.DOME);

                // Check there is someone in my final position and it is not me
                if(hisWorker == null || hisWorker.getID().equals(moveData.getPlayer().getWorkers().get(0).getID()) || hisWorker.getID().equals(moveData.getPlayer().getWorkers().get(1).getID())) {
                    System.err.println("There is no one in the cell i want to push with my worker or he is one of mine, i am the set opp pos push effect of worker " + moveData.getWorker().getID());
                    return false;
                }

                int deltaX = finalPosition.x - mySecondToLastPosition.x;
                int deltaY = finalPosition.y - mySecondToLastPosition.y;

                Cell whereHeHasToGo = model.getBoard().getCell(new Point(finalPosition.x-deltaX,finalPosition.y-deltaY));

                if(whereHeHasToGo == null || whereHeHasToGo.isOccupied())
                    return false;

                if (!simulate) {

                    whereHeHasToGo.setWorker(hisWorker.getID());

                    hisWorker.setPosition(whereHeHasToGo.getPosition());

                    finalPositionCell.removeWorker();

                    finalPositionCell.setWorker(myWorker.getID());

                    startPositionCell.removeWorker();

                    myWorker.setPosition(finalPosition);

                    moveData.getPlayer().setPlayerState(nextPlayerState);
                }

                return true;

            });
        }
        else if(effect.getData().equals("SWAP")){
             lambdaEffect = ((moveData, buildData, simulate) -> {

                 assert(buildData == null);

                 List<Point> moves = moveData.getData();

                 Point startPosition = moveData.getWorker().getPosition();
                 Cell startPositionCell = model.getBoard().getCell(startPosition);
                 Point finalPosition = moves.get(moves.size()-1);
                 Cell finalPositionCell = model.getBoard().getCell(finalPosition);
                 Point mySecondToLastPosition;
                 if(moves.size() > 1)
                     mySecondToLastPosition = moves.get(moves.size()-2);
                 else
                     mySecondToLastPosition = startPosition;
                 Cell mySecondToLastCell = model.getBoard().getCell(mySecondToLastPosition);
                 Worker myWorker = moveData.getWorker();
                 Worker hisWorker = model.getWorkerByID(finalPositionCell.getWorkerID());

                 // Check i am where i want to start the move
                 assert(startPositionCell.getWorkerID().equals(myWorker.getID()));

                 // My second to last cell is empty
                 assert ((mySecondToLastCell.getWorkerID() == null || mySecondToLastCell.getWorkerID().equals(myWorker.getID())) && mySecondToLastCell.getTopBuilding() != LevelType.DOME);

                 // Check in my final pos there is not a dome
                 assert (finalPositionCell.getTopBuilding() != LevelType.DOME);

                 // Check there is someone in my final position and it is not me
                 if(hisWorker == null || hisWorker.getID().equals(moveData.getPlayer().getWorkers().get(0).getID()) || hisWorker.getID().equals(moveData.getPlayer().getWorkers().get(1).getID())) {
                     System.err.println("There is no one in the cell i want to push with my worker or he is one of mine, i am the set opp pos swap effect of worker " + moveData.getWorker().getID());
                     return false;
                 }


                 if (!simulate) {

                     startPositionCell.removeWorker();

                     finalPositionCell.removeWorker();

                     finalPositionCell.setWorker(myWorker.getID());

                     mySecondToLastCell.setWorker(hisWorker.getID());

                     myWorker.setPosition(finalPosition);

                     hisWorker.setPosition(mySecondToLastPosition);

                     moveData.getPlayer().setPlayerState(nextPlayerState);
                 }

                 return true;
            });
        }
        return lambdaEffect;
    }

}