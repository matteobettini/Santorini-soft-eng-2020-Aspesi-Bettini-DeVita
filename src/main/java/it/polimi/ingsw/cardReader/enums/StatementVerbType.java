package it.polimi.ingsw.cardReader.enums;

/**
 * Represents the possible types of a statement verb.
 * Each of them has a specific compiler to get the lambda rule.
 */
public enum StatementVerbType {
    PLAYER_EQUALS,
    STATE_EQUALS,
    HAS_FLAG,
    MOVE_LENGTH,
    EXISTS_DELTA_MORE,
    EXISTS_DELTA_LESS,
    LEVEL_TYPE,
    INTERACTION_NUM,
    POSITION_EQUALS,
    BUILD_NUM,
    BUILD_DOME_EXCEPT,
    BUILD_DOME,
    BUILDING_POS_EQUALS
}
