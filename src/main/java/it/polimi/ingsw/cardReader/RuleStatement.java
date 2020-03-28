package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.StatementType;
import it.polimi.ingsw.cardReader.enums.StatementVerbType;

public abstract class RuleStatement {

    /**
     * Getter for this statement type
     * @return Enum value corresponding to this statement type
     */
    public abstract StatementType getType();

    /**
     * Getter for this statement subject
     * @return String containing rule subject information
     */
    public abstract String getSubject();

    /**
     * Getter for this statement verb
     * @return Enum value representing this statement verb
     */
    public abstract StatementVerbType getVerb();

    /**
     * Getter for this statement object
     * @return String containing rule object information
     */
    public abstract String getObject();
}
