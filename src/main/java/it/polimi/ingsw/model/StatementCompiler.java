package it.polimi.ingsw.model;

import it.polimi.ingsw.cards.RuleStatement;
import it.polimi.ingsw.cards.enums.StatementType;
import it.polimi.ingsw.cards.enums.StatementVerbType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerFlag;
import it.polimi.ingsw.model.enums.PlayerState;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Translates a Rule Statement into a Compiled Statement
 */
class StatementCompiler {


    /** Compiles the given statement
     * @param model the internal model is needed to incapsulate it in the lambdas
     * @param statement the statement to be compiled
     * @param owner the owner of the rule in which the statement is
     * @return the compiled statement
     */
    public static LambdaStatement compileStatement(InternalModel model, RuleStatement statement, Player owner) {

        assert (model != null && statement != null);

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
               result = compileMoveLength(statement);
               break;
           case EXISTS_DELTA_MORE:
               result = compileExistsDeltaMore(model, statement);
               break;
           case EXISTS_DELTA_LESS:
               result = compileExistsDeltaLess(model, statement);
               break;
           case EXISTS_LEVEL_TYPE:
               result = compileExistsLevelType(model, statement);
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
        assert (owner != null);
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
        assert (owner != null);

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

    private static LambdaStatement compileMoveLength(RuleStatement statement) {

        int object = Integer.parseInt(statement.getObject());
        boolean isNif = statement.getType() == StatementType.NIF;

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {

            boolean result = false;
            assert (buildData == null);
            List<Point> moves = moveData.getData();

            if (moves.size() == object)
                result = true;

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
                List<Integer> deltas = model.getBoard().getMoveDeltas(moves, moveData.getWorker().getPosition());
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
                List<Integer> deltas = model.getBoard().getMoveDeltas(moves, moveData.getWorker().getPosition());
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

    private static LambdaStatement compileExistsLevelType(InternalModel model, RuleStatement statement) {

        LevelType object = LevelType.valueOf(statement.getObject());
        boolean isNif = statement.getType() == StatementType.NIF;

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert (buildData == null);
            List<Point> moves = moveData.getData();

            for(Point p : moves)
                if(model.getBoard().getCell(p).getTopBuilding() == object) {
                    result = true;
                    break;
                }

            if(isNif)
                result = !result;

            return result;
        });

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

                if(presentWID != null && myWorkers.stream().noneMatch(w -> w.getID().equals(presentWID)))
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
            boolean result = false;
            assert(moveData == null);

            Map<Point,List<BuildingType>> builds = buildData.getData();

            int num_of_builds = 0;

            for(List<BuildingType> b : builds.values())
                num_of_builds += b.size();

            if(num_of_builds == object)
                result = true;

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

            for (Point currPoint : builds.keySet()) {
                List<BuildingType> buildsInThisPoint = builds.get(currPoint);
                if(buildsInThisPoint.contains(BuildingType.DOME)){
                    if (buildsInThisPoint.indexOf(BuildingType.DOME) == 0){
                        if(model.getBoard().getCell(currPoint).getTopBuilding() != object) {
                            result = true;
                            break;
                        }
                    }
                    else if(LevelType.valueOf(buildsInThisPoint.get(buildsInThisPoint.indexOf(BuildingType.DOME) - 1).name()) != object){
                        result = true;
                        break;
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


            for (Point currPoint : builds.keySet()) {
                List<BuildingType> buildsInThisPoint = builds.get(currPoint);
                if(buildsInThisPoint.contains(BuildingType.DOME)){
                    if (buildsInThisPoint.indexOf(BuildingType.DOME) == 0){
                        if(model.getBoard().getCell(currPoint).getTopBuilding() == object) {
                            result = true;
                            break;
                        }
                    }
                    else if(LevelType.valueOf(buildsInThisPoint.get(buildsInThisPoint.indexOf(BuildingType.DOME) - 1).name()) == object){
                        result = true;
                        break;
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
}