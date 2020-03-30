package it.polimi.ingsw.model.cardReader.enums;

/**
 * Represents possible values for a rule effect.
 * Each of them has a specific compiler to get the lambda effect.
 */
public enum EffectType {
    ALLOW,
    SET_OPPONENT_POSITION,
    DENY,
    WIN
}
