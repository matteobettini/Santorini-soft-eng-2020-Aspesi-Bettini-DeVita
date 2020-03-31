package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerFlag;
import it.polimi.ingsw.model.enums.PlayerState;
import it.polimi.ingsw.model.turnInfo.MoveData;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Translates a Rule Statement into a Compiled Statement
 */
public class StatementCompiler {


    /** Compiles the given statement
     * @param model the internal model is needed to incapsulate it in the lambdas
     * @param statement the statement to be compiled
     * @param owner the owner of the rule in which the statement is
     * @return the compiled statement
     */
    public static LambdaStatement compileStatement(InternalModel model, RuleStatement statement, Player owner) {

        assert (model != null && statement != null && owner != null);

       StatementVerbType verb = statement.getVerb();
       LambdaStatement result = null;
       switch (verb){
           case PLAYER_EQUALS:
               result = compilePlayerEquals(statement, owner);
               break;
           case STATE_EQUALS:
               result = compileStateEquals(statement);
               break;
           case HAS_FLAG:
               result = compileHasFlag(statement, owner);
               break;
           case MOVE_LENGTH:
               result = compileMoveLegth(model, statement);
               break;
           case EXISTS_DELTA_MORE:
               result = compileExistsDeltaMore(model, statement);
               break;
           case EXISTS_DELTA_LESS:
               result = compileExistsDeltaLess(model, statement);
               break;
           case LEVEL_TYPE:
               result = compileLevelType(model, statement);
               break;
           case INTERACTION_NUM:
               result = compileInteractionNum(model, statement);
               break;
           case POSITION_EQUALS:
               result = compilePositionEquals(model, statement);
               break;
           case BUILD_NUM:
               result = compileBuildNum(statement);
               break;
           case BUILD_DOME_EXCEPT:
               result = compileBuildDomeExcept(model, statement);
               break;
           case BUILD_DOME:
               result = compileBuildDome(model, statement);
               break;
           case BUILD_IN_SAME_SPOT:
               result = compileBuildInSameSpot(statement);
               break;
       }
       return result;
    }

    private static LambdaStatement compilePlayerEquals(RuleStatement statement, Player owner) {

        boolean isNif = statement.getType() == StatementType.NIF;

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            if(moveData == null){
                result = buildData.getPlayer().equals(owner);
            }
            else if (buildData == null){
                result = moveData.getPlayer().equals(owner);
            }

            if(isNif)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileStateEquals(RuleStatement statement) {

        boolean isNif = statement.getType() == StatementType.NIF;
        PlayerState object = PlayerState.valueOf(statement.getObject());

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            if(moveData == null){
                result = (buildData.getPlayer().getState() == object);
            }
            else if (buildData == null){
                result = (moveData.getPlayer().getState() == object);
            }
            if(isNif)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }

    private static LambdaStatement compileHasFlag(RuleStatement statement, Player owner) {

        PlayerFlag objectFlag = PlayerFlag.valueOf(statement.getObject());
        boolean isNif = statement.getType() == StatementType.NIF;
        LambdaStatement lambdaStatement = null;

        if(statement.getSubject().equals("YOU")){
            lambdaStatement = ((moveData, buildData) -> {
                boolean result = false;
                if(moveData == null){
                    result = buildData.getPlayer().hasFlag(objectFlag);
                }
                else if (buildData == null){
                    result = moveData.getPlayer().hasFlag(objectFlag);
                }

                if(isNif)
                    result = !result;

                return result;
            });
        }
        else if(statement.getSubject().equals("CARD_OWNER")){
            lambdaStatement = ((moveData, buildData) -> {

                boolean result;
                assert (buildData == null || moveData == null);

                result = owner.hasFlag(objectFlag);

                if(isNif)
                    result = !result;

                return result;
            });
        }


        return  lambdaStatement;
    }


    private static LambdaStatement compileMoveLegth(InternalModel model, RuleStatement statement) {

        int object = Integer.parseInt(statement.getObject());
        boolean isNif = statement.getType() == StatementType.NIF;

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {

            boolean result = false;
            assert (buildData == null);
            List<Point> moves = moveData.getData();
            assert (!moves.isEmpty());

            if (moves.size() == object) {
                Point myPosition = moveData.getWorker().getPosition();
                Point myFirstMovePosition = moves.get(0);
                if (StatementCompiler.adiacent(myPosition, myFirstMovePosition) && model.getBoard().getCell(myFirstMovePosition).getTopBuilding() != LevelType.DOME){
                    boolean correct = true;
                    for (int i = 0; i < moves.size() - 1; i++) {
                        if(!StatementCompiler.adiacent(moves.get(i), moves.get(i+1)) || model.getBoard().getCell(moves.get(i+1)).getTopBuilding() == LevelType.DOME) {
                            correct = false;
                            break;
                        }
                    }
                    if(correct)
                        result = true;
                }
            }

            if(isNif)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }

    private static LambdaStatement compileExistsDeltaMore(InternalModel model, RuleStatement statement) {

        int object = Integer.parseInt(statement.getObject());
        boolean isNif = statement.getType() == StatementType.NIF;


        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(buildData == null);

            List<Point> moves = moveData.getData();
            if(!moves.isEmpty()){
                List<Integer> deltas = StatementCompiler.getMoveDeltas(model, moves, moveData);
                int max = deltas.stream()
                        .max(Integer::compareTo)
                        .orElse(0);
                if(max > object)
                    result = true;
            }

            if(isNif)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileExistsDeltaLess(InternalModel model, RuleStatement statement) {
        int object = Integer.parseInt(statement.getObject());
        boolean isNif = statement.getType() == StatementType.NIF;

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(buildData == null);

            List<Point> moves = moveData.getData();
            if(!moves.isEmpty()){
                List<Integer> deltas = StatementCompiler.getMoveDeltas(model, moves, moveData);
                int min = deltas.stream()
                        .min(Integer::compareTo)
                        .orElse(0);
                if(min < object)
                    result = true;
            }

            if(isNif)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileLevelType(InternalModel model, RuleStatement statement) {

        LevelType object = LevelType.valueOf(statement.getObject());
        boolean isNif = statement.getType() == StatementType.NIF;
        LambdaStatement lambdaStatement = null;

        if(statement.getSubject().equals("START_POSITION")) {
            lambdaStatement = ((moveData, buildData) -> {
                boolean result = false;
                assert(buildData == null);
                Point startPosition = moveData.getWorker().getPosition();
                if(model.getBoard().getCell(startPosition).getTopBuilding() == object)
                    result = true;

                if(isNif)
                    result = !result;

                return result;

            });
        }
        else if(statement.getSubject().equals("FINAL_POSITION")){
            lambdaStatement = ((moveData, buildData) -> {
                boolean result = false;
                assert(buildData == null);
                List<Point> moves = moveData.getData();
                assert (!moves.isEmpty());

                Point finalPosition = moves.get(moves.size()-1);
                if(model.getBoard().getCell(finalPosition).getTopBuilding() == object)
                    result = true;

                if(isNif)
                    result = !result;

                return result;

            });
        }
        return lambdaStatement;
    }


    private static LambdaStatement compileInteractionNum(InternalModel model, RuleStatement statement) {

        boolean isNif = statement.getType() == StatementType.NIF;
        int object = Integer.parseInt(statement.getObject());

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(buildData == null);
            List<Point> moves = moveData.getData();

            int occupied = 0;

            for(Point p : moves){
                if(model.getBoard().getCell(p).getWorkerID() != null)
                    occupied ++;
            }

            if(occupied == object)
                result = true;

            if(isNif)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compilePositionEquals(InternalModel model, RuleStatement statement) {

        boolean isNif = statement.getType() == StatementType.NIF;
        LambdaStatement lambdaStatement = null;

        if(statement.getObject().equals("START_POSITION")){
            lambdaStatement = ((moveData, buildData) -> {
                boolean result = false;
                assert(buildData == null);
                List<Point> moves = moveData.getData();
                assert (!moves.isEmpty());

                if(moves.get(moves.size()-1).equals(moveData.getWorker().getPosition()))
                    result = true;

                if(isNif)
                    result = !result;

                return result;
            });
        }
        else if(statement.getObject().equals("OPPONENTS")){
            lambdaStatement = ((moveData, buildData) -> {
                boolean result = false;
                assert(buildData == null);
                List<Point> moves = moveData.getData();
                assert (!moves.isEmpty());

                Point finalPosition = moves.get(moves.size()-1);
                List<Worker> myWorkers = moveData.getPlayer().getWorkers();


                String presentWID = model.getBoard().getCell(finalPosition).getWorkerID();
                if(presentWID != null && !presentWID.equals(myWorkers.get(0).getID()) && !presentWID.equals(myWorkers.get(1).getID()))
                    result = true;


                if(isNif)
                    result = !result;

                return result;
            });
        }

        return  lambdaStatement;
    }


    private static LambdaStatement compileBuildNum(RuleStatement statement) {

        boolean isNif = statement.getType() == StatementType.NIF;
        int object = Integer.parseInt(statement.getObject());

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result;
            assert(moveData == null);

            Map<Point,List<BuildingType>> builds = buildData.getData();

            int num_of_builds = 0;

            for(List<BuildingType> b : builds.values())
                num_of_builds += b.size();

            if(num_of_builds != object)
                result = false;
            else {
                result = true;
                for (Point currPoint : builds.keySet()) {
                    if (!StatementCompiler.adiacent(currPoint, buildData.getWorker().getPosition()))
                        result = false;
                }
            }
            if(isNif)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }

    private static LambdaStatement compileBuildDomeExcept(InternalModel model, RuleStatement statement) {

        boolean isNif = statement.getType() == StatementType.NIF;
        LevelType object = LevelType.valueOf(statement.getObject());

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(moveData == null);
            Map<Point,List<BuildingType>> builds = buildData.getData();

            if(!builds.isEmpty()) {
                for (Point currPoint : builds.keySet()) {
                    if (builds.get(currPoint).size() == 1 && builds.get(currPoint).get(0) == BuildingType.DOME) {
                        if (model.getBoard().getCell(currPoint).getTopBuilding() != object) {
                            result = true;
                            break;
                        }
                    }
                    else {
                        List<BuildingType> buildsInThisPoint = builds.get(currPoint);
                        if(buildsInThisPoint.contains(BuildingType.DOME)){
                            if(model.getBoard().getCell(currPoint).canBuild(buildsInThisPoint)){
                                if(LevelType.valueOf(buildsInThisPoint.get(buildsInThisPoint.indexOf(BuildingType.DOME)-1).name()) != object)
                                    result = true;
                                    break;
                            }
                        }
                    }
                }
            }

            if(isNif)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileBuildDome(InternalModel model, RuleStatement statement) {

        boolean isNif = statement.getType() == StatementType.NIF;
        LevelType object = LevelType.valueOf(statement.getObject());

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(moveData == null);
            Map<Point,List<BuildingType>> builds = buildData.getData();

            if(!builds.isEmpty()) {
                for (Point currPoint : builds.keySet()) {
                    if (builds.get(currPoint).size() == 1 && builds.get(currPoint).get(0) == BuildingType.DOME) {
                        if (model.getBoard().getCell(currPoint).getTopBuilding() == object) {
                            result = true;
                            break;
                        }
                    }
                    else {
                        List<BuildingType> buildsInThisPoint = builds.get(currPoint);
                        if(buildsInThisPoint.contains(BuildingType.DOME)){
                            if(model.getBoard().getCell(currPoint).canBuild(buildsInThisPoint)){
                                if(LevelType.valueOf(buildsInThisPoint.get(buildsInThisPoint.indexOf(BuildingType.DOME)-1).name()) == object)
                                    result = true;
                                break;
                            }
                        }
                    }
                }
            }
            if(isNif)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileBuildInSameSpot(RuleStatement statement) {

        boolean isNif = statement.getType() == StatementType.NIF;

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert (moveData == null);

            Map<Point,List<BuildingType>> builds = buildData.getData();

            if(builds.size() == 1)
                result = true;

            if(isNif)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }




    public static boolean adiacent(Point p1, Point p2){

        assert (p1 != null && p2 != null);

        return (p2.x == p1.x && p2.y == p1.y - 1) || (p2.x == p1.x && p2.y == p1.y + 1) || (p2.x == p1.x - 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y + 1) || (p2.x == p1.x + 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y + 1);
    }

    public static List<Integer> getMoveDeltas(InternalModel model, List<Point> moves, MoveData moveData){

        assert (model != null && moves != null && moves.size()>0 && moveData != null);

        List<Integer> result = new ArrayList<>();

        Map<LevelType,Integer> levelHeights = new HashMap<>();
        levelHeights.put(LevelType.GROUND, 0);
        levelHeights.put(LevelType.FIRST_FLOOR, 1);
        levelHeights.put(LevelType.SECOND_FLOOR, 2);
        levelHeights.put(LevelType.THIRD_FLOOR, 3);
        levelHeights.put(LevelType.DOME, 4);

        result.add(levelHeights.get(model.getBoard().getCell(moves.get(0)).getTopBuilding()) - levelHeights.get(model.getBoard().getCell(moveData.getWorker().getPosition()).getTopBuilding()));


        for(int i = 0; i< moves.size()-1; i++){
            int differenceInHeight = levelHeights.get(model.getBoard().getCell(moves.get(i+1)).getTopBuilding()) - levelHeights.get(model.getBoard().getCell(moves.get(i)).getTopBuilding());
            result.add(differenceInHeight);
        }

        return result;
    }

}