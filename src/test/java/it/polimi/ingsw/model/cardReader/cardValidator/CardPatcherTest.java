package it.polimi.ingsw.model.cardReader.cardValidator;

import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.RuleEffect;
import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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