package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.cardReader.exceptions.CardLoadingException;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
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

    private CardFile generateDefaultStrategy(){
        List<CardRuleImpl> rules = new LinkedList<>();
        List<RuleStatementImpl> statements = new LinkedList<>();
        //MOVE ALLOW
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.STATE_EQUALS, "TURN_STARTED"));
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.MOVE_LENGTH, "1"));
        statements.add(new RuleStatementImpl(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_MORE, "1"));
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.INTERACTION_NUM, "0"));
        RuleEffectImpl effect = new RuleEffectImpl(EffectType.ALLOW, PlayerState.MOVED,null);
        rules.add(new CardRuleImpl(TriggerType.MOVE,statements,effect));
        //BUILD ALLOW
        statements = new LinkedList<>();
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.STATE_EQUALS, "MOVED"));
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.BUILD_NUM, "1"));
        statements.add(new RuleStatementImpl(StatementType.NIF,"YOU", StatementVerbType.BUILD_DOME_EXCEPT, "THIRD_FLOOR"));
        effect = new RuleEffectImpl(EffectType.ALLOW,PlayerState.BUILT,null);
        rules.add(new CardRuleImpl(TriggerType.BUILD,statements,effect));
        //MOVE WIN
        statements = new LinkedList<>();
        statements.add(new RuleStatementImpl(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE, "0"));
        statements.add(new RuleStatementImpl(StatementType.IF,"FINAL_POSITION", StatementVerbType.LEVEL_TYPE, "THIRD_FLOOR"));
        effect = new RuleEffectImpl(EffectType.WIN,PlayerState.UNKNOWN,null);
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
        }catch (NullPointerException ex){
            throw new CardLoadingException("Card's dir not found");
        }
        return result;
    }
}
