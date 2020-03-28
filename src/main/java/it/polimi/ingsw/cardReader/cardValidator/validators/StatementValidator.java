package it.polimi.ingsw.cardReader.cardValidator.validators;

import it.polimi.ingsw.cardReader.RuleStatement;
import it.polimi.ingsw.cardReader.cardValidator.exceptions.InvalidStatementObjectException;
import it.polimi.ingsw.cardReader.cardValidator.exceptions.InvalidStatementSubjectException;

/**
 * This class offers a method to validate card statement's semantic
 */
public abstract class StatementValidator {
    /**
     * This method is used to validate RuleStatement semantic
     * @param stm RuleStatement to be validated
     * @throws InvalidStatementSubjectException If problems are found during the scan of statement's subject data
     * @throws InvalidStatementObjectException If problems are found during the scan of statement's object data
     */
    public static void checkRuleStatement(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void playerEqualsValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void stateEqualsValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void hasFlagValidate(RuleStatement stm)  throws InvalidStatementSubjectException, InvalidStatementObjectException{
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void moveLengthValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        // TODO implement here
    }

    /**
     * @param stm
     * @return
     */
    private static void existsDeltaMoreValidate(RuleStatement stm)  throws InvalidStatementSubjectException, InvalidStatementObjectException{
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void existsDeltaLessValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void levelTypeValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        // TODO implement here
    }

    /**
     * @param stm
     * @return
     */
    private static void interactionNumValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void positionEqualsValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void buildNumValidate(RuleStatement stm)  throws InvalidStatementSubjectException, InvalidStatementObjectException{
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void buildDomeExceptValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void buildDomeValidate(RuleStatement stm)  throws InvalidStatementSubjectException, InvalidStatementObjectException{
        // TODO implement here
    }

    /**
     * @param stm
     */
    private static void buildingPosEqualsValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        // TODO implement here
    }
}
