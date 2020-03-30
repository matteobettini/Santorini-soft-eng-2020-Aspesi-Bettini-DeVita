package it.polimi.ingsw.cardReader.cardValidator;

import it.polimi.ingsw.cardReader.CardFile;
import it.polimi.ingsw.cardReader.CardRule;
import it.polimi.ingsw.cardReader.RuleEffect;
import it.polimi.ingsw.cardReader.RuleStatement;
import it.polimi.ingsw.cardReader.enums.EffectType;
import it.polimi.ingsw.cardReader.enums.StatementType;
import it.polimi.ingsw.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardPatcherTest {

    @Test
    void patchCard() {

    }

    private CardFile testPatchMoveOnly(){
        //Generate default strategy
        List<RuleStatement> statements = new ArrayList<>();
        statements.add(new RuleStatement(StatementType.IF, "YOU", StatementVerbType.MOVE_LENGTH, "1"));
        statements.add(new RuleStatement(StatementType.NIF, "YOU", StatementVerbType.EXISTS_DELTA_MORE, "1"));
        RuleEffect effect = new RuleEffect(EffectType.ALLOW, PlayerState.MOVE, null);
        List<CardRule> rules = new ArrayList<>();
        rules.add(new CardRule(TriggerType.MOVE, statements,effect));
        CardFile defaultCardFile = new CardFile("Default", "", rules);


        return null;
    }
}