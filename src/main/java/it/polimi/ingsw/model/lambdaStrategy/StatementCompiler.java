package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.cardReader.RuleStatement;
import it.polimi.ingsw.cardReader.enums.StatementType;
import it.polimi.ingsw.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerFlag;
import it.polimi.ingsw.model.enums.PlayerState;
import it.polimi.ingsw.model.exceptions.NoWorkerPresentException;
import it.polimi.ingsw.model.turnInfo.BuildData;
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


    private static LambdaStatement compileHasFlag(RuleStatement statement, Player owner) {
        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            if(moveData == null){
                if(statement.getSubject().equals("YOU")){
                    result = buildData.getPlayer().hasFlag(PlayerFlag.valueOf(statement.getObject()));
                }
                else if(statement.getSubject().equals("CARD_OWNER")){
                    result = owner.hasFlag(PlayerFlag.valueOf(statement.getObject()));
                }
            }
            else if (buildData == null){
                if(statement.getSubject().equals("YOU")){
                    result = moveData.getPlayer().hasFlag(PlayerFlag.valueOf(statement.getObject()));
                }
                else if(statement.getSubject().equals("CARD_OWNER")){
                    result = owner.hasFlag(PlayerFlag.valueOf(statement.getObject()));
                }
            }
            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileMoveLegth(InternalModel model, RuleStatement statement) {
        LambdaStatement lambdaStatement = (moveData, buildData) -> {
            boolean result = false;
            assert (buildData == null);
            List<Point> moves = moveData.getData();
            assert (!moves.isEmpty());

            if (moves.size() == Integer.parseInt(statement.getObject())) {

                Point myPosition = moveData.getWorker().getPosition();
                Point myFirstMovePosition = moves.get(0);
                if (StatementCompiler.adiacent(myPosition, myFirstMovePosition) && model.getBoard().getCell(myFirstMovePosition).getTopBuilding() != LevelType.DOME){
                    boolean correct = true;
                    for (int i = 0; i < moves.size() - 1; i++) {
                        if(!StatementCompiler.adiacent(moves.get(i), moves.get(i+1)) || model.getBoard().getCell(moves.get(i+1)).getTopBuilding() == LevelType.DOME)
                            correct = false;
                    }
                    if(correct)
                        result = true;
                }
             }

            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        };
        return  lambdaStatement;
    }

    private static LambdaStatement compileExistsDeltaMore(InternalModel model, RuleStatement statement) {
        LambdaStatement lambdaStatement = (moveData, buildData) -> {
            boolean result = false;
            assert(buildData == null);

            List<Point> moves = moveData.getData();
            if(!moves.isEmpty()){
                List<Integer> deltas = StatementCompiler.getMoveDeltas(model, moves, moveData);
                int max = deltas.stream()
                        .max(Integer::compareTo)
                        .orElse(0);
                if(max > Integer.parseInt(statement.getObject()))
                    result = true;
            }

            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        };
        return  lambdaStatement;
    }


    private static LambdaStatement compileExistsDeltaLess(InternalModel model, RuleStatement statement) {
        LambdaStatement lambdaStatement = (moveData, buildData) -> {
            boolean result = false;
            assert(buildData == null);

            List<Point> moves = moveData.getData();
            if(!moves.isEmpty()){
                List<Integer> deltas = StatementCompiler.getMoveDeltas(model, moves, moveData);
                int min = deltas.stream()
                        .min(Integer::compareTo)
                        .orElse(0);
                if(min < Integer.parseInt(statement.getObject()))
                    result = true;
            }

            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        };
        return  lambdaStatement;
    }


    private static LambdaStatement compileLevelType(InternalModel model, RuleStatement statement) {
        LambdaStatement lambdaStatement = (moveData, buildData) -> {
            boolean result = false;
            assert(buildData == null);
            List<Point> moves = moveData.getData();

            if(statement.getSubject().equals("START_POSITION")) {
                Point startPosition = moves.get(0);
                if(model.getBoard().getCell(startPosition).getTopBuilding() == LevelType.valueOf(statement.getObject()))
                    result = true;
            }
            else if(statement.getSubject().equals("FINAL_POSITION")){
                Point finalPosition = moves.get(moves.size()-1);
                if(model.getBoard().getCell(finalPosition).getTopBuilding() == LevelType.valueOf(statement.getObject()))
                    result = true;
            }

            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        };
        return  lambdaStatement;
    }


    private static LambdaStatement compileInteractionNum(InternalModel model, RuleStatement statement) {
        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(buildData == null);
            List<Point> moves = moveData.getData();

            int free_cells = 0;

            for(Point p : moves){
                try {
                    model.getBoard().getCell(p).getWorkerID();
                } catch (NoWorkerPresentException e) {
                    free_cells ++;
                }
            }

            if((moves.size() - free_cells) == Integer.parseInt(statement.getObject()))
                result = true;


            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compilePositionEquals(InternalModel model, RuleStatement statement) {
        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(buildData == null);
            List<Point> moves = moveData.getData();

            if(statement.getObject().equals("START_POSITION"))
                if(moves.get(moves.size()-1).equals(moveData.getWorker().getPosition()))
                    result = true;

            else if(statement.getObject().equals("OPPONENTS")){

                Point finalPosition = moves.get(moves.size()-1);
                List<Worker> myWorkers = moveData.getPlayer().getWorkers();
                assert(myWorkers.size() == 2);
                try{
                    String presentWID = model.getBoard().getCell(finalPosition).getWorkerID();
                    if(!presentWID.equals(myWorkers.get(0).getID()) && !presentWID.equals(myWorkers.get(1).getID()))
                        result = true;
                } catch (NoWorkerPresentException ignored){ }

            }

            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileBuildNum(RuleStatement statement) {
        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result;
            assert(moveData == null);
            Map<Point, BuildingType> builds = buildData.getData();
            if(builds.size() != Integer.parseInt(statement.getObject()))
                result = false;
            else {
                result = true;
                for (Point currPoint : builds.keySet()) {
                    if (!StatementCompiler.adiacent(currPoint, buildData.getWorker().getPosition()))
                        result = false;
                }
            }
            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }

    private static LambdaStatement compileBuildDomeExcept(InternalModel model, RuleStatement statement) {
        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(moveData == null);
            Map<Point, BuildingType> builds = buildData.getData();

            if(!builds.isEmpty()) {
                for (Point currPoint : builds.keySet()) {
                    if (builds.get(currPoint) == BuildingType.DOME)
                        if (model.getBoard().getCell(currPoint).getTopBuilding() != LevelType.valueOf(statement.getObject())) {
                            result = true;
                            break;
                        }
                }
            }

            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileBuildDome(InternalModel model, RuleStatement statement) {
        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(moveData == null);
            Map<Point, BuildingType> builds = buildData.getData();

            if(!builds.isEmpty()) {
                for (Point currPoint : builds.keySet()) {
                    if (builds.get(currPoint) == BuildingType.DOME)
                        if (model.getBoard().getCell(currPoint).getTopBuilding() == LevelType.valueOf(statement.getObject())) {
                            result = true;
                            break;
                        }
                }
            }
            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileBuildInSameSpot(RuleStatement statement) {
        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result;
            assert (moveData == null);

            Map<Point, BuildingType> builds = buildData.getData();
            if(builds.isEmpty())
                result = false;
            else {
                result = true;
                Iterator<Point> iterator = builds.keySet().iterator();
                Point p = iterator.next();
                while (iterator.hasNext()) {
                    if (!iterator.next().equals(p)) {
                        result = false;
                    }
                }
            }


            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compilePlayerEquals(RuleStatement statement, Player owner) {
        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            if(moveData == null){
                result = buildData.getPlayer().equals(owner);
            }
            else if (buildData == null){
                result = moveData.getPlayer().equals(owner);
            }

            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileStateEquals(RuleStatement statement) {

        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            if(moveData == null){
                result = buildData.getPlayer().getState() == PlayerState.valueOf(statement.getObject());
            }
            else if (buildData == null){
                result = moveData.getPlayer().getState() == PlayerState.valueOf(statement.getObject());
            }
            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }

    public static boolean adiacent(Point p1, Point p2){
        return (p2.x == p1.x && p2.y == p1.y - 1) || (p2.x == p1.x && p2.y == p1.y + 1) || (p2.x == p1.x - 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y + 1) || (p2.x == p1.x + 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y + 1);
    }

    public static List<Integer> getMoveDeltas(InternalModel model, List<Point> moves, MoveData moveData){
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