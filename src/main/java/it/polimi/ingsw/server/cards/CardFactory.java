package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.cards.enums.*;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.model.enums.PlayerState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton for cards
 */
public class CardFactory {
    private static String cardsPath = "server/Cards.xml";

    private CardFile defaultCard;
    private List<CardFile> cards;

    private static CardFactory instance = null;

    private CardFactory()  {
    }

    /**
     * Gets an instance for this cards' singleton
     * @return Instance of CardFactory
     * @throws InvalidCardException If there are problems during cards parsing
     */
    public synchronized static CardFactory getInstance() throws InvalidCardException{
        if (instance == null){
            instance = new CardFactory();
            instance.defaultCard = instance.generateDefaultStrategy();
            instance.cards = instance.loadCards();
        }
        return instance;
    }

    /**
     * Gets the default game strategy
     * @return CardFile containing the default game strategy
     */
    public CardFile getDefaultStrategy() {
        return defaultCard;
    }

    /**
     * Loads all cards from config files
     * @return List containing parsed and checked cards
     */
    public List<CardFile> getCards() {
        return new LinkedList<>(cards);
    }

    /**
     * This CardFile generator must be managed carefully.
     * To permit the whole model to work correctly, every default strategy must have:
     * - Exactly 1 CardRule with trigger MOVE, effect type ALLOW, subtype STANDARD and without PLAYER_EQUALS
     * - Exactly 1 CardRule with trigger BUILD, effect type ALLOW, subtype STANDARD and without PLAYER_EQUALS
     * - At least one CardRule with effect type WIN
     * @return CardFile of the default strategy
     */
    private CardFile generateDefaultStrategy(){
        List<CardRuleImpl> rules = new LinkedList<>();
        List<RuleStatementImpl> statements = new LinkedList<>();
        //MOVE ALLOW
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.STATE_EQUALS, "TURN_STARTED"));
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.MOVE_LENGTH, "1"));
        statements.add(new RuleStatementImpl(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_MORE, "1"));
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.INTERACTION_NUM, "0"));
        RuleEffectImpl effect = new RuleEffectImpl(EffectType.ALLOW, AllowType.STANDARD, PlayerState.MOVED);
        rules.add(new CardRuleImpl(TriggerType.MOVE,statements,effect));
        //BUILD ALLOW
        statements = new LinkedList<>();
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.STATE_EQUALS, "MOVED"));
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.BUILD_NUM, "1"));
        statements.add(new RuleStatementImpl(StatementType.NIF,"YOU", StatementVerbType.BUILD_DOME_EXCEPT, "THIRD_FLOOR"));
        effect = new RuleEffectImpl(EffectType.ALLOW,AllowType.STANDARD, PlayerState.BUILT);
        rules.add(new CardRuleImpl(TriggerType.BUILD,statements,effect));
        //MOVE WIN
        statements = new LinkedList<>();
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE, "0"));
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.EXISTS_LEVEL_TYPE, "THIRD_FLOOR"));
        effect = new RuleEffectImpl(EffectType.WIN);
        rules.add(new CardRuleImpl(TriggerType.MOVE,statements,effect));
        //Generate card
        CardFile defaultCard = new CardFileImpl("Default Strategy", "None", rules);
        try{
            CardValidator.checkCardFile(defaultCard);
        } catch (InvalidCardException e) {
            assert false;
        }
        return defaultCard;
    }

    private List<CardFile> loadCards() throws InvalidCardException {
        return CardReader.readCards(defaultCard, cardsPath);
    }
}
