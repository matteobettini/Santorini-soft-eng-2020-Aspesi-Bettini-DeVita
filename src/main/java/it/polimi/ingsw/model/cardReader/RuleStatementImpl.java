package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;

import java.util.Objects;

class RuleStatementImpl implements RuleStatement {

    private final StatementType type;
    private final String subject;
    private final StatementVerbType verb;
    private final String object;

    public RuleStatementImpl(StatementType type, String subject, StatementVerbType verb, String object) {
        assert(type != null && subject != null && verb != null && object != null);
        this.type = type;
        this.subject = subject;
        this.verb = verb;
        this.object = object;
    }

    /**
     * Getter for this statement type
     * @return Enum value corresponding to this statement type
     */
    public StatementType getType(){
        return this.type;
    }

    /**
     * Getter for this statement subject
     * @return String containing rule subject information
     */
    public String getSubject(){
        return this.subject;
    }

    /**
     * Getter for this statement verb
     * @return Enum value representing this statement verb
     */
    public StatementVerbType getVerb(){
        return this.verb;
    }

    /**
     * Getter for this statement object
     * @return String containing rule object information
     */
    public String getObject(){
        return this.object;
    }

    /**
     * Compares two RuleStatements, using the internal state instead of the memory location
     * @param o Object to compare to
     * @return True if the two objects contains the same information
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleStatementImpl that = (RuleStatementImpl) o;
        return type == that.type &&
                subject.equals(that.subject) &&
                verb == that.verb &&
                object.equals(that.object);
    }

    /**
     * Return an hash code for this class, using the internal information
     * @return The generated hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, subject, verb, object);
    }
}
