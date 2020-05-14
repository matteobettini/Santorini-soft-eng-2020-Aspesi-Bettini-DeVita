package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.cards.*;
import it.polimi.ingsw.server.cards.enums.TriggerType;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.model.enums.PlayerState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MapCompilerTest {
    private static CardFactory cardFactory;
    private InternalModel model;
    private Player Matteo;
    private Player Mirko;
    private Player Andrea;

    @BeforeAll
    static void init() throws InvalidCardException {
        cardFactory = CardFactory.getInstance();
    }

    @BeforeEach
    void createModel(){
        List<String> players = new ArrayList<>();
        players.add("Andrea");
        players.add("Matteo");
        players.add("Mirko");
        model = new InternalModel(players, cardFactory);
        Andrea = model.getPlayerByNick("Andrea");
        Matteo = model.getPlayerByNick("Matteo");
        Mirko = model.getPlayerByNick("Mirko");
    }

    /*
        Test default strategy next states
     */
    @Test
    public void Test1(){
        model.compileCardStrategy();
        MapCompiler.compileMap(model.getPlayers(), cardFactory.getDefaultStrategy());

        Set<TriggerType> possibleActions;
        for(Player player : model.getPlayers()){
            for (PlayerState state : PlayerState.values()){
                switch (state){
                    case TURN_STARTED:
                        player.setPlayerState(state);
                        possibleActions = player.getPossibleActions();
                        assert possibleActions.size() == 1;
                        assert possibleActions.contains(TriggerType.MOVE);
                        break;
                    case MOVED:
                        player.setPlayerState(state);
                        possibleActions = player.getPossibleActions();
                        assert possibleActions.size() == 1;
                        assert possibleActions.contains(TriggerType.BUILD);
                        break;
                    case FIRST_BUILT:
                    case BUILT:
                        player.setPlayerState(state);
                        possibleActions = player.getPossibleActions();
                        assert possibleActions.size() == 0;
                        break;
                    default:
                        assert false;
                }
            }
        }
    }

    /*
        Test with normal cards
     */
    @Test
    public void Test2(){
        CardFile apollo = cardFactory.getCards().stream().filter(c->c.getName().equals("Apollo")).findAny().orElse(null);
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(c->c.getName().equals("Athena")).findAny().orElse(null);
        assertNotNull(apollo);
        assertNotNull(demeter);
        assertNotNull(athena);

        Andrea.setCard(apollo);
        Mirko.setCard(demeter);
        Matteo.setCard(athena);
        model.compileCardStrategy();
        MapCompiler.compileMap(model.getPlayers(), cardFactory.getDefaultStrategy());

        Set<TriggerType> possibleActions;
        for(Player player : model.getPlayers()){
            for (PlayerState state : PlayerState.values()){
                switch (state){
                    case TURN_STARTED:
                        player.setPlayerState(state);
                        possibleActions = player.getPossibleActions();
                        assert possibleActions.size() == 1;
                        assert possibleActions.contains(TriggerType.MOVE);
                        break;
                    case MOVED:
                        player.setPlayerState(state);
                        possibleActions = player.getPossibleActions();
                        assert possibleActions.size() == 1;
                        assert possibleActions.contains(TriggerType.BUILD);
                        break;
                    case FIRST_BUILT:
                    case BUILT:
                        player.setPlayerState(state);
                        possibleActions = player.getPossibleActions();
                        assert possibleActions.size() == 0;
                        break;
                    default:
                        assert false;
                }
            }
        }
    }

    /*
        Test with prometheus
     */
    @Test
    public void Test3(){
        CardFile prometheus = cardFactory.getCards().stream().filter(c->c.getName().equals("Prometheus")).findAny().orElse(null);
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(c->c.getName().equals("Athena")).findAny().orElse(null);
        assertNotNull(prometheus);
        assertNotNull(demeter);
        assertNotNull(athena);

        Andrea.setCard(prometheus);
        Mirko.setCard(demeter);
        Matteo.setCard(athena);
        model.compileCardStrategy();
        MapCompiler.compileMap(model.getPlayers(), cardFactory.getDefaultStrategy());

        Set<TriggerType> possibleActions;
        for(Player player : model.getPlayers()){
            if (!player.equals(Andrea)){
                for (PlayerState state : PlayerState.values()){
                    switch (state){
                        case TURN_STARTED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.MOVE);
                            break;
                        case MOVED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        case FIRST_BUILT:
                        case BUILT:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 0;
                            break;
                        default:
                            assert false;
                    }
                }
            }else{
                for (PlayerState state : PlayerState.values()){
                    switch (state){
                        case TURN_STARTED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 2;
                            assert possibleActions.contains(TriggerType.MOVE);
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        case MOVED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        case FIRST_BUILT:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.MOVE);
                            break;
                        case BUILT:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 0;
                            break;
                        default:
                            assert false;
                    }
                }
            }
        }
    }

    /*
        Test with opponents allow card
     */
    @Test
    public void Test4(){
        CardFile special = CardFileImplTest.getAllowOpponents(TriggerType.BUILD, PlayerState.TURN_STARTED);
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(c->c.getName().equals("Athena")).findAny().orElse(null);
        assertNotNull(special);
        assertNotNull(demeter);
        assertNotNull(athena);

        Andrea.setCard(special);
        Mirko.setCard(demeter);
        Matteo.setCard(athena);
        model.compileCardStrategy();
        MapCompiler.compileMap(model.getPlayers(), cardFactory.getDefaultStrategy());

        Set<TriggerType> possibleActions;
        for(Player player : model.getPlayers()){
            if (player.equals(Andrea)){
                for (PlayerState state : PlayerState.values()){
                    switch (state){
                        case TURN_STARTED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.MOVE);
                            break;
                        case MOVED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        case FIRST_BUILT:
                        case BUILT:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 0;
                            break;
                        default:
                            assert false;
                    }
                }
            }else{
                for (PlayerState state : PlayerState.values()){
                    switch (state){
                        case TURN_STARTED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 2;
                            assert possibleActions.contains(TriggerType.MOVE);
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        case MOVED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        case FIRST_BUILT:
                        case BUILT:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 0;
                            break;
                        default:
                            assert false;
                    }
                }
            }
        }
    }

    /*
       Test with opponents allow card, applicable to all states
    */
    @Test
    public void Test5(){
        CardFile special = CardFileImplTest.getAllowOpponents(TriggerType.BUILD);
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(c->c.getName().equals("Athena")).findAny().orElse(null);
        assertNotNull(special);
        assertNotNull(demeter);
        assertNotNull(athena);

        Andrea.setCard(special);
        Mirko.setCard(demeter);
        Matteo.setCard(athena);
        model.compileCardStrategy();
        MapCompiler.compileMap(model.getPlayers(), cardFactory.getDefaultStrategy());

        Set<TriggerType> possibleActions;
        for(Player player : model.getPlayers()){
            if (player.equals(Andrea)){
                for (PlayerState state : PlayerState.values()){
                    switch (state){
                        case TURN_STARTED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.MOVE);
                            break;
                        case MOVED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        case FIRST_BUILT:
                        case BUILT:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 0;
                            break;
                        default:
                            assert false;
                    }
                }
            }else{
                for (PlayerState state : PlayerState.values()){
                    switch (state){
                        case TURN_STARTED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 2;
                            assert possibleActions.contains(TriggerType.MOVE);
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        case FIRST_BUILT:
                        case BUILT:
                        case MOVED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        default:
                            assert false;
                    }
                }
            }
        }
    }

    /*
       Test with allow card, applicable to all state except one
    */
    @Test
    public void Test6(){
        CardFile special = CardFileImplTest.getAllowAllStatesExcluded(TriggerType.BUILD, PlayerState.TURN_STARTED);
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(c->c.getName().equals("Athena")).findAny().orElse(null);
        assertNotNull(special);
        assertNotNull(demeter);
        assertNotNull(athena);

        Andrea.setCard(special);
        Mirko.setCard(demeter);
        Matteo.setCard(athena);
        model.compileCardStrategy();
        MapCompiler.compileMap(model.getPlayers(), cardFactory.getDefaultStrategy());

        Set<TriggerType> possibleActions;
        for(Player player : model.getPlayers()){
            if (player.equals(Andrea)){
                for (PlayerState state : PlayerState.values()){
                    switch (state){
                        case TURN_STARTED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.MOVE);
                            break;
                        case FIRST_BUILT:
                        case BUILT:
                        case MOVED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        default:
                            assert false;
                    }
                }
            }else{
                for (PlayerState state : PlayerState.values()){
                    switch (state){
                        case TURN_STARTED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.MOVE);
                            break;
                        case MOVED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        case FIRST_BUILT:
                        case BUILT:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 0;
                            break;
                        default:
                            assert false;
                    }
                }
            }
        }
    }

    /*
       Test with opponents allow card, applicable to all state except one
    */
    @Test
    public void Test7(){
        CardFile special = CardFileImplTest.getAllowOpponentsAllStatesExcluded(TriggerType.BUILD, PlayerState.TURN_STARTED);
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(c->c.getName().equals("Athena")).findAny().orElse(null);
        assertNotNull(special);
        assertNotNull(demeter);
        assertNotNull(athena);

        Andrea.setCard(special);
        Mirko.setCard(demeter);
        Matteo.setCard(athena);
        model.compileCardStrategy();
        MapCompiler.compileMap(model.getPlayers(), cardFactory.getDefaultStrategy());

        Set<TriggerType> possibleActions;
        for(Player player : model.getPlayers()){
            if (!player.equals(Andrea)){
                for (PlayerState state : PlayerState.values()){
                    switch (state){
                        case TURN_STARTED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.MOVE);
                            break;
                        case FIRST_BUILT:
                        case BUILT:
                        case MOVED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        default:
                            assert false;
                    }
                }
            }else{
                for (PlayerState state : PlayerState.values()){
                    switch (state){
                        case TURN_STARTED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.MOVE);
                            break;
                        case MOVED:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 1;
                            assert possibleActions.contains(TriggerType.BUILD);
                            break;
                        case FIRST_BUILT:
                        case BUILT:
                            player.setPlayerState(state);
                            possibleActions = player.getPossibleActions();
                            assert possibleActions.size() == 0;
                            break;
                        default:
                            assert false;
                    }
                }
            }
        }
    }

    /*
       Test with not player equals, applicable to turn started
    */
    @Test
    public void Test8(){
        CardFile special = CardFileImplTest.getAllowAll(TriggerType.BUILD, PlayerState.TURN_STARTED);
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(c->c.getName().equals("Athena")).findAny().orElse(null);
        assertNotNull(special);
        assertNotNull(demeter);
        assertNotNull(athena);

        Andrea.setCard(special);
        Mirko.setCard(demeter);
        Matteo.setCard(athena);
        model.compileCardStrategy();
        MapCompiler.compileMap(model.getPlayers(), cardFactory.getDefaultStrategy());

        Set<TriggerType> possibleActions;
        for(Player player : model.getPlayers()){
            for (PlayerState state : PlayerState.values()){
                switch (state){
                    case TURN_STARTED:
                        player.setPlayerState(state);
                        possibleActions = player.getPossibleActions();
                        assert possibleActions.size() == 2;
                        assert possibleActions.contains(TriggerType.MOVE);
                        assert possibleActions.contains(TriggerType.BUILD);
                        break;
                    case MOVED:
                        player.setPlayerState(state);
                        possibleActions = player.getPossibleActions();
                        assert possibleActions.size() == 1;
                        assert possibleActions.contains(TriggerType.BUILD);
                        break;
                    case FIRST_BUILT:
                    case BUILT:
                        player.setPlayerState(state);
                        possibleActions = player.getPossibleActions();
                        assert possibleActions.size() == 0;
                        break;
                    default:
                        assert false;
                }
            }
        }
    }
}