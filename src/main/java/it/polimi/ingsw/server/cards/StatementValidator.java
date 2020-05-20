package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.cards.exceptions.InvalidStatementObjectException;
import it.polimi.ingsw.server.cards.exceptions.InvalidStatementSubjectException;
import it.polimi.ingsw.server.model.enums.LevelType;
import it.polimi.ingsw.server.model.enums.PlayerFlag;
import it.polimi.ingsw.server.model.enums.PlayerState;

/**
 * This class offers a method to validate card statement's semantic
 */
class StatementValidator {
    /**
     * This method is used to validate RuleStatement semantic
     * @param stm RuleStatement to be validated
     * @throws InvalidStatementSubjectException If problems are found during the scan of statement's subject data
     * @throws InvalidStatementObjectException If problems are found during the scan of statement's object data
     */
    public static void checkRuleStatement(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getVerb()){
            case STATE_EQUALS:
                stateEqualsValidate(stm);
                break;
            case PLAYER_EQUALS:
                playerEqualsValidate(stm);
                break;
            case POSITION_EQUALS:
                positionEqualsValidate(stm);
                break;
            case MOVE_LENGTH:
                moveLengthValidate(stm);
                break;
            case EXISTS_LEVEL_TYPE:
                existsLevelTypeValidate(stm);
                break;
            case BUILD_IN_SAME_SPOT:
                buildInSameSpotValidate(stm);
                break;
            case BUILD_NUM:
                buildNumValidate(stm);
                break;
            case BUILD_DOME_EXCEPT:
                buildDomeExceptValidate(stm);
                break;
            case EXISTS_DELTA_MORE:
                existsDeltaMoreValidate(stm);
                break;
            case HAS_FLAG:
                hasFlagValidate(stm);
                break;
            case BUILD_DOME:
                buildDomeValidate(stm);
                break;
            case INTERACTION_NUM:
                interactionNumValidate(stm);
                break;
            case EXISTS_DELTA_LESS:
                existsDeltaLessValidate(stm);
                break;
            case IS_NEAR:
                isNearValidate(stm);
                break;
            case ONLY_COMPLETE_TOWERS_NEAR:
                onlyCompleteTowersNearValidate(stm);
                break;
            case LAST_BUILD_ON:
                lastBuildOnValidate(stm);
                break;
            case IS_THE_HIGHEST:
                isTheHighestValidate(stm);
                break;
            default:
                assert false;
        }
    }

    /**
     * Check PLAYER_EQUALS
     * @param stm PLAYER_EQUALS statement
     */
    private static void playerEqualsValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[PLAYER_EQUALS] Subject '" + stm.getSubject() + "' not supported" );
        }
        switch (stm.getObject()){
            case "CARD_OWNER":
                break;
            default:
                throw new InvalidStatementObjectException("[PLAYER_EQUALS] Object '" + stm.getObject() + "' not supported" );
        }
    }

    /**
     * Check STATE_EQUALS
     * @param stm STATE_EQUALS statement
     */
    private static void stateEqualsValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[STATE_EQUALS] Subject '" + stm.getSubject() + "' not supported" );
        }
        try{
            PlayerState state = PlayerState.valueOf(stm.getObject());
        }catch (IllegalArgumentException ex){
            throw new InvalidStatementObjectException("[STATE_EQUALS] Object '" + stm.getObject() + "' is not a valid PlayerState");
        }
    }

    /**
     * Check HAS_FLAG
     * @param stm HAS_FLAG statement
     */
    private static void hasFlagValidate(RuleStatement stm)  throws InvalidStatementSubjectException, InvalidStatementObjectException{
        switch (stm.getSubject()){
            case "YOU":
            case "CARD_OWNER":
                break;
            default:
                throw new InvalidStatementSubjectException("[HAS_FLAG] Subject '" + stm.getSubject() + "' not supported" );
        }
        try{
            PlayerFlag flag = PlayerFlag.valueOf(stm.getObject());
        }catch (IllegalArgumentException ex){
            throw new InvalidStatementObjectException("[HAS_FLAG] Object '" + stm.getObject() + "' is not a valid PlayerFlag");
        }
    }

    /**
     * Check MOVE_LENGTH
     * @param stm MOVE_LENGTH statement
     */
    private static void moveLengthValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[MOVE_LENGTH] Subject '" + stm.getSubject() + "' not supported");
        }
        try{
            int val = Integer.parseInt(stm.getObject());
            if (val <= 0){
                throw new InvalidStatementObjectException("[MOVE_LENGTH] Object '" + stm.getObject() + "' is not > 0");
            }
        }catch (NumberFormatException ex){
            throw new InvalidStatementObjectException("[MOVE_LENGTH] Object '" + stm.getObject() + "' is not a valid number");
        }
    }

    /**
     * Check EXISTS_DELTA_MORE
     * @param stm EXISTS_DELTA_MORE statement
     */
    private static void existsDeltaMoreValidate(RuleStatement stm)  throws InvalidStatementSubjectException, InvalidStatementObjectException{
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[EXISTS_DELTA_MORE] Subject '" + stm.getSubject() + "' not supported");
        }
        try{
            Integer val = Integer.parseInt(stm.getObject());
        }catch (NumberFormatException ex){
            throw new InvalidStatementObjectException("[EXISTS_DELTA_MORE] Object '" + stm.getObject() + "' is not a valid number");
        }
    }

    /**
     * Check EXISTS_DELTA_LESS
     * @param stm EXISTS_DELTA_LESS statement
     */
    private static void existsDeltaLessValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[EXISTS_DELTA_LESS] Subject '" + stm.getSubject() + "' not supported");
        }
        try{
            Integer val = Integer.parseInt(stm.getObject());
        }catch (NumberFormatException ex){
            throw new InvalidStatementObjectException("[EXISTS_DELTA_LESS] Object '" + stm.getObject() + "' is not a valid number");
        }
    }

    /**
     * Check EXISTS_LEVEL_TYPE
     * @param stm EXISTS_LEVEL_TYPE statement
     */
    private static void existsLevelTypeValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[EXISTS_LEVEL_TYPE] Subject '" + stm.getSubject() + "' not supported" );
        }
        try{
            LevelType level = LevelType.valueOf(stm.getObject());
        }catch (IllegalArgumentException ex){
            throw new InvalidStatementObjectException("[EXISTS_LEVEL_TYPE] Object '" + stm.getObject() + "' is not a valid LevelType");
        }
    }

    /**
     * Check INTERACTION_NUM
     * @param stm INTERACTION_NUM statement
     */
    private static void interactionNumValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[INTERACTION_NUM] Subject '" + stm.getSubject() + "' not supported");
        }
        try{
            int val = Integer.parseInt(stm.getObject());
            if (val < 0){
                throw new InvalidStatementObjectException("[INTERACTION_NUM] Object '" + stm.getObject() + "' is not >= 0");
            }
        }catch (NumberFormatException ex){
            throw new InvalidStatementObjectException("[INTERACTION_NUM] Object '" + stm.getObject() + "' is not a valid number");
        }
    }

    /**
     * Check POSITION_EQUALS
     * @param stm POSITION_EQUALS statement
     */
    private static void positionEqualsValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "FINAL_POSITION":
                break;
            default:
                throw new InvalidStatementSubjectException("[POSITION_EQUALS] Subject '" + stm.getSubject() + "' not supported" );
        }
        switch (stm.getObject()){
            case "START_POSITION":
            case "OPPONENTS":
                break;
            default:
                throw new InvalidStatementObjectException("[POSITION_EQUALS] Object '" + stm.getObject() + "' not supported" );
        }
    }

    /**
     * Check BUILD_NUM
     * @param stm BUILD_NUM statement
     */
    private static void buildNumValidate(RuleStatement stm)  throws InvalidStatementSubjectException, InvalidStatementObjectException{
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[BUILD_NUM] Subject '" + stm.getSubject() + "' not supported");
        }
        try{
            int val = Integer.parseInt(stm.getObject());
            if (val <= 0){
                throw new InvalidStatementObjectException("[BUILD_NUM] Object '" + stm.getObject() + "' is not > 0");
            }
        }catch (NumberFormatException ex){
            throw new InvalidStatementObjectException("[BUILD_NUM] Object '" + stm.getObject() + "' is not a valid number");
        }
    }

    /**
     * Check BUILD_DOME_EXCEPT
     * @param stm BUILD_DOME_EXCEPT statement
     */
    private static void buildDomeExceptValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[BUILD_DOME_EXCEPT] Subject '" + stm.getSubject() + "' not supported" );
        }
        try{
            LevelType level = LevelType.valueOf(stm.getObject());
            if (level == LevelType.DOME)
                throw new InvalidStatementObjectException("[BUILD_DOME_EXCEPT] Object 'DOME' not supported");
        }catch (IllegalArgumentException ex){
            throw new InvalidStatementObjectException("[BUILD_DOME_EXCEPT] Object '" + stm.getObject() + "' is not a valid LevelType");
        }
    }

    /**
     * Check BUILD_DOME
     * @param stm BUILD_DOME statement
     */
    private static void buildDomeValidate(RuleStatement stm)  throws InvalidStatementSubjectException, InvalidStatementObjectException{
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[BUILD_DOME] Subject '" + stm.getSubject() + "' not supported" );
        }
        try{
            LevelType level = LevelType.valueOf(stm.getObject());
            if (level == LevelType.DOME)
                throw new InvalidStatementObjectException("[BUILD_DOME] Object 'DOME' not supported");
        }catch (IllegalArgumentException ex){
            throw new InvalidStatementObjectException("[BUILD_DOME] Object '" + stm.getObject() + "' is not a valid LevelType");
        }
    }

    /**
     * Check BUILD_IN_SAME_SPOT
     * @param stm BUILD_IN_SAME_SPOT statement
     */
    private static void buildInSameSpotValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[BUILD_IN_SAME_SPOT] Subject '" + stm.getSubject() + "' not supported" );
        }
        switch (stm.getObject()){
            case "ALL":
                break;
            default:
                throw new InvalidStatementObjectException("[BUILD_IN_SAME_SPOT] Object '" + stm.getObject() + "' not supported" );
        }
    }

    /**
     * Check IS_NEAR
     * @param stm IS_NEAR statement
     */
    private static void isNearValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "START_POSITION":
            case "FINAL_POSITION":
            case "ONE_BUILD_POSITION":
                break;
            default:
                throw new InvalidStatementSubjectException("[IS_NEAR] Subject '" + stm.getSubject() + "' not supported" );
        }
        switch (stm.getObject()){
            case "CARD_OWNER":
                break;
            default:
                throw new InvalidStatementObjectException("[IS_NEAR] Object '" + stm.getObject() + "' not supported" );
        }
    }

    /**
     * Check ONLY_COMPLETE_TOWERS_NEAR
     * @param stm  ONLY_COMPLETE_TOWERS_NEAR statement
     */
    private static void onlyCompleteTowersNearValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[ONLY_COMPLETE_TOWERS_NEAR] Subject '" + stm.getSubject() + "' not supported" );
        }
        switch (stm.getObject()){
            case "CARD_OWNER":
                break;
            default:
                throw new InvalidStatementObjectException("[ONLY_COMPLETE_TOWERS_NEAR] Object '" + stm.getObject() + "' not supported" );
        }
    }

    /**
     * Check LAST_BUILD_ON
     * @param stm  LAST_BUILD_ON statement
     */
    private static void lastBuildOnValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "YOU":
                break;
            default:
                throw new InvalidStatementSubjectException("[LAST_BUILD_ON] Subject '" + stm.getSubject() + "' not supported" );
        }
        switch (stm.getObject()){
            case "PERIMETER":
                break;
            default:
                throw new InvalidStatementObjectException("[LAST_BUILD_ON] Object '" + stm.getObject() + "' not supported" );
        }
    }

    /**
     * Check IS_THE_HIGHEST
     * @param stm  IS_THE_HIGHEST statement
     */
    private static void isTheHighestValidate(RuleStatement stm) throws InvalidStatementSubjectException, InvalidStatementObjectException {
        switch (stm.getSubject()){
            case "CHOSEN_WORKER":
                break;
            default:
                throw new InvalidStatementSubjectException("[IS_THE_HIGHEST] Subject '" + stm.getSubject() + "' not supported" );
        }
        switch (stm.getObject()){
            case "YOUR_WORKERS":
                break;
            default:
                throw new InvalidStatementObjectException("[IS_THE_HIGHEST] Object '" + stm.getObject() + "' not supported" );
        }
    }


}
