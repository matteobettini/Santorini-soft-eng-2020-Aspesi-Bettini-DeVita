package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.cardReader.RuleStatement;
import it.polimi.ingsw.cardReader.enums.StatementType;
import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerState;
import it.polimi.ingsw.model.exceptions.NoWorkerPresentException;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        // TODO implement here
        return null;
    }


    private static LambdaStatement compileHasFlag(InternalModel model, RuleStatement statement, Player owner) {
        // TODO implement here
        return null;
    }


    private static LambdaStatement compileMoveLegth(InternalModel model, RuleStatement statement, Player owner) {
        // TODO implement here
        return null;
    }

    private static LambdaStatement compileExistsDeltaMore(InternalModel model, RuleStatement statement, Player owner) {
        // TODO implement here
        return null;
    }


    private static LambdaStatement compileExistsDeltaLess(InternalModel model, RuleStatement statement, Player owner) {
        // TODO implement here
        return null;
    }


    private static LambdaStatement compileLevelType(InternalModel model, RuleStatement statement, Player owner) {
        // TODO implement here
        return null;
    }


    private static LambdaStatement compileInteractionNum(InternalModel model, RuleStatement statement) {
        //TODO
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


    private static LambdaStatement compilePositionEquals(RuleStatement statement) {
        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(buildData == null);
            List<Point> moves = moveData.getData();
            if(moves.get(moves.size()-1).equals(moveData.getWorker().getPosition()))
                result = true;

            if(statement.getType() == StatementType.NIF)
                result = !result;

            return result;
        });
        return  lambdaStatement;
    }


    private static LambdaStatement compileBuildNum(RuleStatement statement) {
        LambdaStatement lambdaStatement = ((moveData, buildData) -> {
            boolean result = false;
            assert(moveData == null);
            Map<Point, BuildingType> builds = buildData.getData();
            if(builds.size() != Integer.parseInt(statement.getObject()))
                return false;

            result = true;
            for (Point currPoint : builds.keySet()) {
                if(!adiacent(currPoint, buildData.getWorker().getPosition()))
                    result = false;
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
            if(builds.isEmpty())
                return false;

            for (Point currPoint : builds.keySet()) {
                if (builds.get(currPoint) == BuildingType.DOME)
                    if (model.getBoard().getCell(currPoint).getTopBuilding() != LevelType.valueOf(statement.getObject())) {
                        result = true;

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
            if(builds.isEmpty())
                return false;

            for (Point currPoint : builds.keySet()) {
                if (builds.get(currPoint) == BuildingType.DOME)
                    if (model.getBoard().getCell(currPoint).getTopBuilding() == LevelType.valueOf(statement.getObject())) {
                        result = true;
                        break;
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
            boolean result = false;
            if(moveData == null){
                Map<Point, BuildingType> builds = buildData.getData();
                if(builds.isEmpty())
                    return false;
                Iterator<Point> iterator = builds.keySet().iterator();
                Point p = iterator.next();
                while(iterator.hasNext())
                    if(!iterator.next().equals(p))
                        return false;
                result = true;
            }
            else if (buildData == null){
                return false;
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

    private static boolean adiacent(Point p1, Point p2){
        return (p2.x == p1.x && p2.y == p1.y - 1) || (p2.x == p1.x && p2.y == p1.y + 1) || (p2.x == p1.x - 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y + 1) || (p2.x == p1.x + 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y + 1);
    }

}