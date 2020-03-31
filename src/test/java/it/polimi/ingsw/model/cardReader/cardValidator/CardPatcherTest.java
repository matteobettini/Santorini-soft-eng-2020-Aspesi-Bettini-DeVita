package it.polimi.ingsw.model.cardReader.cardValidator;

import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.RuleEffect;
import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class CardPatcherTest {

    @Test
    void patchCard() {

    }

    private CardFile testPatchMoveOnly(){



        return null;
    }


    public static CardFile getDefaultStrategyExample(){
        //Generate default MOVE ALLOW strategy
        List<RuleStatement> statements = new ArrayList<>();
        statements.add(new RuleStatement(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "TURN_STARTED"));
        statements.add(new RuleStatement(StatementType.IF, "YOU", StatementVerbType.MOVE_LENGTH, "1"));
        statements.add(new RuleStatement(StatementType.NIF, "YOU", StatementVerbType.EXISTS_DELTA_MORE, "1"));
        RuleEffect effect = new RuleEffect(EffectType.ALLOW, PlayerState.MOVED, null);
        List<CardRule> rules = new ArrayList<>();
        rules.add(new CardRule(TriggerType.MOVE, statements,effect));
        //Generate default BUILD ALLOW strategy
        statements = new ArrayList<>();
        statements.add(new RuleStatement(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "MOVED"));
        statements.add(new RuleStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_NUM, "1"));
        effect = new RuleEffect(EffectType.ALLOW, PlayerState.BUILT, null);
        rules.add(new CardRule(TriggerType.BUILD, statements,effect));

        CardFile defaultStrategy = new CardFile("Default", "", rules);
        try{
            CardValidator.checkCardFile(defaultStrategy);
        } catch (InvalidCardException e) {
            assert false;
        }
        return defaultStrategy;
    }
}