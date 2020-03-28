package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.StatementType;
import it.polimi.ingsw.cardReader.enums.StatementVerbType;

public abstract class RuleStatement {

    public abstract StatementType getType();

    public abstract String getSubject();

    public abstract StatementVerbType getVerb();

    public abstract String getObject();
}
