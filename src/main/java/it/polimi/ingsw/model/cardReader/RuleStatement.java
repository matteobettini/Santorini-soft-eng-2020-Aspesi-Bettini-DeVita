package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;

public interface RuleStatement {
    /**
     * Getter for this statement type
     * @return Enum value corresponding to this statement type
     */
    StatementType getType();
    /**
     * Getter for this statement subject
     * @return String containing rule subject information
     */
    String getSubject();
    /**
     * Getter for this statement verb
     * @return Enum value representing this statement verb
     */
    StatementVerbType getVerb();
    /**
     * Getter for this statement object
     * @return String containing rule object information
     */
    String getObject();
}
