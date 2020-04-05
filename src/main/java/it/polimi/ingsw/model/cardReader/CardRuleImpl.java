package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.TriggerType;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

class CardRuleImpl implements CardRule{

    private final TriggerType trigger;
    private final List<RuleStatementImpl> statements;
    private final RuleEffectImpl effect;

    public CardRuleImpl(TriggerType trigger, List<RuleStatementImpl> statements, RuleEffectImpl effect) {
        assert (trigger != null && statements != null && effect != null);
        this.trigger = trigger;
        this.statements = statements;
        this.effect = effect;
    }

    /**
     * Getter for the trigger of this rule
     * @return Enum value corresponding to the trigger of this rule
     */
    public TriggerType getTrigger(){
        return this.trigger;
    }

    /**
     * Getter for this card rule statements
     * @return List of all the statements of the card
     */
    public List<RuleStatement> getStatements(){
        return new LinkedList<>(this.statements);
    }
    public List<RuleStatementImpl> getStatementsInternal(){
        return new LinkedList<>(this.statements);
    }

    /**
     * Setter for adding a statement to this rule.
     * Useful for patching runtime the rule
     * @param stm Statement to add to this rule
     */
    public void addStatement(RuleStatement stm){
        assert (stm != null);
        statements.add(new RuleStatementImpl(stm.getType(), stm.getSubject(), stm.getVerb(), stm.getObject()));
    }

    /**
     * Getter for the effect of this rule
     * @return Object RuleEffect containing effect data of this rule
     */
    public RuleEffect getEffect(){
        return this.effect;
    }
    public RuleEffectImpl getEffectInternal() {
        return this.effect;
    }

    /**
     * Compares two CardRules, using the internal state instead of the memory location
     * @param o Object to compare to
     * @return True if the two objects contains the same information
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardRuleImpl cardRule = (CardRuleImpl) o;
        return trigger == cardRule.trigger &&
                statements.equals(cardRule.statements) &&
                effect.equals(cardRule.effect);
    }

    /**
     * Return an hash code for this class, using the internal information
     * @return The generated hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(trigger, statements, effect);
    }
}
