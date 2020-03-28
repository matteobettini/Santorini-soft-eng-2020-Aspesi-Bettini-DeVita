package it.polimi.ingsw.cardReader.helpers;

import it.polimi.ingsw.cardReader.CardRule;
import it.polimi.ingsw.cardReader.RuleEffect;
import it.polimi.ingsw.cardReader.RuleStatement;
import it.polimi.ingsw.cardReader.enums.TriggerType;

import java.util.ArrayList;
import java.util.List;

public class RuleHelper {

    public static CardRule getEmptyCardRule(){
        TriggerType triggerT = TriggerType.MOVE;
        List<RuleStatement> statementsT = new ArrayList<>();

        return new CardRule(triggerT,statementsT,null);
    }
    public static List<CardRule> getRandomCardRuleList(){
        List<CardRule> res = new ArrayList<>();
        res.add(new CardRule(TriggerType.MOVE, StatementHelper.getStatementList(), EffectHelper.getRuleEffect()));
        res.add(new CardRule(TriggerType.MOVE, StatementHelper.getStatementList(), EffectHelper.getRuleEffect()));
        return res;
    }

    public static List<CardRule> getRulesWithAllTriggerTypes(){
        List<CardRule> res = new ArrayList<>();
        res.add(new CardRule(TriggerType.BUILD, StatementHelper.getStatementList(), EffectHelper.getRuleEffect()));
        res.add(new CardRule(TriggerType.MOVE, StatementHelper.getStatementList(), EffectHelper.getRuleEffect()));
        res.add(new CardRule(TriggerType.BUILD, StatementHelper.getStatementList(), EffectHelper.getRuleEffect()));
        res.add(new CardRule(TriggerType.MOVE, StatementHelper.getStatementList(), EffectHelper.getRuleEffect()));
        res.add(new CardRule(TriggerType.MOVE, StatementHelper.getStatementList(), EffectHelper.getRuleEffect()));
        return res;
    }
}
