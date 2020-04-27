package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.enums.PlayerState;

import java.util.*;
import java.util.stream.Collectors;

class MapCompiler {

    public static void compileMap(List<Player> players, CardFile defaultStrategy){
        Map<Player, List<CardRule>> allows = new HashMap<>();
        //Add default
        for(Player player : players){
            allows.put(player, defaultStrategy.getRules().stream().filter(r->r.getEffect().getType() == EffectType.ALLOW).collect(Collectors.toList()));
        }
        //Adding card specific
        for(Player player: players){
            CardFile hisCard = player.getCard();
            if (hisCard == null) continue; //Skip if has not card
            for(CardRule rule : hisCard.getRules()){
                if (rule.getEffect().getType() == EffectType.ALLOW){
                    if (ownsRule(rule.getStatements())){
                        allows.get(player).add(rule);
                    }else if (othersOwnRule(rule.getStatements())){
                        for (Player player1 : players){
                            if (!player1.equals(player)){
                                allows.get(player1).add(rule);
                            }
                        }
                    }else{
                        for (Player player1 : players){
                            allows.get(player1).add(rule);
                        }
                    }
                }
            }
        }
        //Process data for each player
        for(Player player:players){
            processPlayerRules(player,allows.get(player));
        }
    }

    private static boolean ownsRule(List<RuleStatement> statements){
        for(RuleStatement stm : statements){
            if (stm.getType() == StatementType.IF && stm.getVerb() == StatementVerbType.PLAYER_EQUALS){
                assert (stm.getObject().equals("CARD_OWNER"));
                return true;
            }
        }
        return false;
    }
    private static boolean othersOwnRule(List<RuleStatement> statements){
        for(RuleStatement stm : statements){
            if (stm.getType() == StatementType.NIF && stm.getVerb() == StatementVerbType.PLAYER_EQUALS){
                assert (stm.getObject().equals("CARD_OWNER"));
                return true;
            }
        }
        return false;
    }

    private static void processPlayerRules(Player player, List<CardRule> allows){
        Map<PlayerState, Set<TriggerType>> association = new HashMap<>();
        for(PlayerState state : PlayerState.values()) {
            association.put(state, new HashSet<>());
            for(CardRule rule : allows){
                if (stateAllowed(state,rule.getStatements())){
                    association.get(state).add(rule.getTrigger());
                }
            }
        }
        player.addActionData(association);
    }

    private static boolean stateAllowed(PlayerState state, List<RuleStatement> statements){
        boolean stateEqualsFound = false;
        for(RuleStatement stm : statements){
            if (stm.getVerb() == StatementVerbType.STATE_EQUALS) {
                stateEqualsFound = true;
                if (stm.getType() == StatementType.IF) {
                    if (stm.getObject().equals(state.name()))
                        return true;
                }else{
                    if (!stm.getObject().equals(state.name()))
                        return true;
                }
            }
        }
        return !stateEqualsFound;
    }
}
