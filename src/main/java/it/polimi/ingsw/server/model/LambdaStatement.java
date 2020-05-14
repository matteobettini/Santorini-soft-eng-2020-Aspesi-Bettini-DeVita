package it.polimi.ingsw.server.model;

/**
 * Thi is a lmabda function that encapsulates the logic of one statement of a certain rule.
 * It says if a certain statement is true or folse given some specific game coditions.
 * It can be both the statement of a move rule or of a build rule.
 */
@FunctionalInterface
interface LambdaStatement {

    /**
     * This method runs the statement
     * @param moveData if it is a move statement, this parameter will contain the associated data otherwise it will be null
     * @param buildData if it is a build statement, this parameter will contain the associated data otherwise it will be null
     * @return returns true if the statement is true or false if it is wrong
     */
    boolean evaluate(MoveData moveData, BuildData buildData);
}