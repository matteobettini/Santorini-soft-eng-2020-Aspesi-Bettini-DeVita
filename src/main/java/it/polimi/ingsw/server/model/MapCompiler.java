package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.cards.CardFile;
import it.polimi.ingsw.server.cards.CardRule;
import it.polimi.ingsw.server.cards.RuleStatement;
import it.polimi.ingsw.server.cards.enums.EffectType;
import it.polimi.ingsw.server.cards.enums.StatementType;
import it.polimi.ingsw.server.cards.enums.StatementVerbType;
import it.polimi.ingsw.server.cards.enums.TriggerType;
import it.polimi.ingsw.server.model.enums.PlayerState;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Used to determine player possible action from a state, based on current in-game cards.
 * Default ones are always guaranteed
 */
class MapCompiler {

    /**
     * Compiles possible actions for players
     * @param players List of in-game players
     * @param defaultStrategy Default strategy
     */
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

    /**
     * Search if the player owns this rule based on statements
     * @param statements List of card statements
     * @return True if this player is affected by this rule
     */
    private static boolean ownsRule(List<RuleStatement> statements){
        for(RuleStatement stm : statements){
            if (stm.getType() == StatementType.IF && stm.getVerb() == StatementVerbType.PLAYER_EQUALS){
                assert (stm.getObject().equals("CARD_OWNER"));
                return true;
            }
        }
        return false;
    }

    /**
     * Search if the player do not owns this rule based on statements
     * @param statements List of card statements
     * @return True if others are affected by this rule
     */
    private static boolean othersOwnRule(List<RuleStatement> statements){
        for(RuleStatement stm : statements){
            if (stm.getType() == StatementType.NIF && stm.getVerb() == StatementVerbType.PLAYER_EQUALS){
                assert (stm.getObject().equals("CARD_OWNER"));
                return true;
            }
        }
        return false;
    }

    /**
     * Create player possible action association using rules that affects him
     * @param player Player
     * @param allows List of rule that affects this player
     */
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

    /**
     * Gets if this rule can be used when player is in the supplied state
     * @param state Starting state
     * @param statements List of statements of this rule
     * @return True if the rule can be used from starting state, False otherwise
     */
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
