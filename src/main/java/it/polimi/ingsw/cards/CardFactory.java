package it.polimi.ingsw.cards;

import it.polimi.ingsw.cards.enums.*;
import it.polimi.ingsw.cards.exceptions.CardLoadingException;
import it.polimi.ingsw.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.PlayerState;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton for cards
 */
public class CardFactory {
    private static String cardPath = "src/main/resources/cards";

    private CardFile defaultCard;
    private List<CardFile> cards;

    private static CardFactory instance = null;

    private CardFactory()  {
    }

    public synchronized static CardFactory getInstance() throws CardLoadingException, InvalidCardException{
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
        RuleEffectImpl effect = new RuleEffectImpl(EffectType.ALLOW, AllowType.STANDARD, PlayerState.MOVED,null);
        rules.add(new CardRuleImpl(TriggerType.MOVE,statements,effect));
        //BUILD ALLOW
        statements = new LinkedList<>();
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.STATE_EQUALS, "MOVED"));
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.BUILD_NUM, "1"));
        statements.add(new RuleStatementImpl(StatementType.NIF,"YOU", StatementVerbType.BUILD_DOME_EXCEPT, "THIRD_FLOOR"));
        effect = new RuleEffectImpl(EffectType.ALLOW,AllowType.STANDARD, PlayerState.BUILT,null);
        rules.add(new CardRuleImpl(TriggerType.BUILD,statements,effect));
        //MOVE WIN
        statements = new LinkedList<>();
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE, "0"));
        statements.add(new RuleStatementImpl(StatementType.IF,"FINAL_POSITION", StatementVerbType.LEVEL_TYPE, "THIRD_FLOOR"));
        effect = new RuleEffectImpl(EffectType.WIN,null);
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

    private List<CardFile> loadCards() throws InvalidCardException, CardLoadingException {
        List<CardFile> result = new LinkedList<>();
        try{
            File cardDir = new File(cardPath);
            File[] files = cardDir.listFiles((File dir, String name) -> name.toLowerCase().endsWith(".xml"));
            if (files == null){
                throw new CardLoadingException("Cannot scan card's dir");
            }
            for(File card : files){
                result.add(CardReader.readCard(defaultCard,card));
            }
        }catch (NullPointerException | SecurityException ex){
            throw new CardLoadingException("Card's dir not found or not accessible");
        }
        return result;
    }
}
