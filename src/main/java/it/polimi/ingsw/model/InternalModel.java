package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;
import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.lambdaStrategy.CompiledCardRule;
import it.polimi.ingsw.model.lambdaStrategy.RuleCompiler;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerLostSignal;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerWonSignal;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;
import it.polimi.ingsw.packets.InvalidPacketException;
import it.polimi.ingsw.packets.PacketBuild;
import it.polimi.ingsw.packets.PacketMove;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class contains all the actual instances of the Model data.
 * It is protected from the external of the package and it handles the entry points of the Model.
 */
public class InternalModel {

    private final CardFactory cardFactory;
    private final Board board;
    private final List<Player> players;
    private Player winner;
    private final List<Player> losers;
    private List<CompiledCardRule> allowMoveRules;
    private List<CompiledCardRule> denyMoveRules;
    private List<CompiledCardRule> winMoveRules;
    private List<CompiledCardRule> allowBuildRules;
    private List<CompiledCardRule> denyBuildRules;
    private List<CompiledCardRule> winBuildRules;

    public InternalModel(List<String> players, CardFactory factory){
        this.players = new ArrayList<>();
        for(String p : players){
            this.players.add(new Player(p));
        }
        this.board = new Board();
        this.losers = new ArrayList<>();
        this.cardFactory = factory;
    }

    /**
     * Getter that returns the Board used in the match.
     * @return an instance of Board.
     */
    public Board getBoard(){ return this.board; }

    /**
     * Getter that returns the List of subscribed Players to the match.
     * @return a List of instances of Players.
     */
    public List<Player> getPlayers(){ return this.players; }

    /**
     * Getter that returns the Player given his nickname.
     * @param playerNick is a String that contains the Player nickname.
     * @return the Player that has playerNick as nickname.
     */
    public Player getPlayerByNick(String playerNick){
        assert playerNick != null;
        for(Player p : this.players){
            if(p.getNickname().equals(playerNick)) return p;
        }
        return null;
    }

    public Player getWinner() { return this.winner; }

    public List<Player> getLosers() { return this.losers; }

    public void addLoser(Player loser){
        assert loser != null;
        assert !losers.contains(loser);
        losers.add(loser);
    }

    public void setWinner(Player winner){
        assert this.winner == null;
        assert winner != null;
        this.winner = winner;
    }

    /**
     * Getter that returns the Worker given its ID.
     * @param workerID is the ID of the Worker.
     * @return the Worker that has workerID as ID.
     */
    public Worker getWorkerByID(String workerID){
        if(workerID == null) return null;
        for(Player p : this.players){
            for(Worker w : p.getWorkers()){
                if(w.getID().equals(workerID)) return w;
            }
        }
        return null;
    }

    /**
     * This is a method that converts the packetMove received through the connection
     * into an instance of MoveData.
     * @param packetMove is the packet containing the data of the move action performed by
     * the Player in his turn.
     * @return a MoveData obtained by the conversion of a packetMove.
     */
    public MoveData packetMoveToMoveData(PacketMove packetMove) throws InvalidPacketException {
        assert packetMove != null;
        
        Player p = getPlayerByNick(packetMove.getPlayerNickname());
        Worker w = getWorkerByID(packetMove.getWorkerID());
        List<Point> moves = packetMove.getMove();
        
        if(moves == null || moves.isEmpty() || p == null || w == null || !p.getWorkers().contains(w)) throw new InvalidPacketException();

        for (Point move : moves) if (move == null || board.getCell(move) == null) throw new InvalidPacketException();
        
        Point myPosition = w.getPosition();
        Point myFirstMovePosition = moves.get(0);
        if (!Board.areAdjacent(myPosition, myFirstMovePosition, false) || board.getCell(myFirstMovePosition).getTopBuilding() == LevelType.DOME){
            throw new InvalidPacketException();
        }
        
        for (int i = 0; i < moves.size() - 1; i++) {
            if(!Board.areAdjacent(moves.get(i), moves.get(i+1), false) || board.getCell(moves.get(i+1)).getTopBuilding() == LevelType.DOME) {
                throw new InvalidPacketException();
            }
        }
        
        return new MoveData(p, w, moves);
    }

    /**
     * This is a method that converts the packetBuild received through the connection
     * into an instance of BuildData.
     * @param packetBuild is the packet containing the data of the build action performed by
     * the Player in his turn.
     * @return a BuildData obtained by the conversion of a packetBuild.
     */
    public BuildData packetBuildToBuildData(PacketBuild packetBuild) throws InvalidPacketException {
        assert packetBuild != null;
        
        Player p = getPlayerByNick(packetBuild.getPlayerNickname());
        Worker w = getWorkerByID(packetBuild.getWorkerID());
        Map<Point,List<BuildingType>> builds = packetBuild.getBuilds();
        List<Point> buildsOrder = packetBuild.getDataOrder();
        
        if(buildsOrder == null || builds == null || buildsOrder.isEmpty() || builds.isEmpty() || buildsOrder.size() != builds.size()  || p == null || w == null || !p.getWorkers().contains(w)) throw new InvalidPacketException();
        
        for(Point pos : builds.keySet()) {
            if (builds.get(pos) == null || builds.get(pos).isEmpty() || board.getCell(pos) == null)
                throw new InvalidPacketException();
            if (!Board.areAdjacent(pos, w.getPosition(), true))
                throw new InvalidPacketException();
        }

        for(Point point : buildsOrder)
            if(!builds.containsKey(point))
                throw new InvalidPacketException();


        return new BuildData(p, w, builds, buildsOrder);
    }

    /**
     * Compiles Card Rules, creating executable lambdas adapted to this particular
     * instance.
     */
    public void compileCardStrategy(){

        allowMoveRules = new ArrayList<>();
        denyMoveRules = new ArrayList<>();
        winMoveRules = new ArrayList<>();
        allowBuildRules = new ArrayList<>();
        denyBuildRules = new ArrayList<>();
        winBuildRules = new ArrayList<>();

        //Adding default rules
        CardFile defaultStrategy = cardFactory.getDefaultStrategy();
        for(CardRule rule : defaultStrategy.getRules()){
            compileAndAddRule(rule,null);
        }
        //Adding card rules
        for(Player player : players){
            //assert(player.getCard() != null);
            if(player.getCard() != null){
                for(CardRule rule : player.getCard().getRules()){
                    compileAndAddRule(rule,player);
                }
            }

        }


    }

    private void compileAndAddRule(CardRule rule, Player owner){
        assert (rule != null);
        CompiledCardRule compiledCardRule = RuleCompiler.compile(this, rule, owner);
        switch (rule.getEffect().getType()){
            case ALLOW:
            case SET_OPPONENT_POSITION:
                if (rule.getTrigger() == TriggerType.MOVE)
                    allowMoveRules.add(compiledCardRule);
                else
                    allowBuildRules.add(compiledCardRule);
                break;
            case DENY:
                if (rule.getTrigger() == TriggerType.MOVE)
                    denyMoveRules.add(compiledCardRule);
                else
                    denyBuildRules.add(compiledCardRule);
                break;
            case WIN:
                if (rule.getTrigger() == TriggerType.MOVE)
                    winMoveRules.add(compiledCardRule);
                else
                    winBuildRules.add(compiledCardRule);
                break;
        }
    }

    /**
     * Tries to make a move for the player. If the move is compatible with current game rules,
     * then the move effect will be applied to the model.
     * @param moveData Move data containing all information needed to process the rules
     * @return True, if the move was applied, False if the move is invalid and must be repeated
     * @throws PlayerWonSignal If the player has won the game with this move
     * @throws PlayerLostSignal If the player has lost the game with this move
     */
    public boolean makeMove(MoveData moveData) throws PlayerWonSignal, PlayerLostSignal {
        assert (allowMoveRules != null && denyMoveRules != null && winMoveRules != null && moveData != null);
        //Check if player can do this move
        CompiledCardRule fired = getAllowMoveRule(moveData);
        //If no allow rule is fired, the move is invalid
        if (fired == null) return false;

        //Check if with this move, the player has lost
        for(CompiledCardRule rule : denyMoveRules){
            rule.execute(moveData,null,false);
        }
        //Check if with this move, the player has won
        for(CompiledCardRule rule : winMoveRules){
            try{
                rule.execute(moveData,null,false);
            } catch (PlayerWonSignal e){
                fired.applyEffect(moveData,null);
                throw new PlayerWonSignal(e.getPlayer());
            }
        }
        //If the move was normal and allowed, apply its effect
        fired.applyEffect(moveData,null);

        return true;
    }

    /**
     * Tries to make a build for the player. If the build is compatible with current game rules,
     * then the build effect will be applied to the model.
     * @param buildData Build data containing all information needed to process the rules
     * @return True, if the build was applied, False if the build is invalid and must be repeated
     * @throws PlayerWonSignal If the player has won the game with this build
     * @throws PlayerLostSignal If the player has lost the game with this build
     */
    public boolean makeBuild(BuildData buildData) throws PlayerWonSignal, PlayerLostSignal{
        assert (allowBuildRules != null && denyBuildRules != null && winBuildRules != null && buildData != null);
        //Check if player can do this move
        CompiledCardRule fired = getAllowBuildRule(buildData);
        //If no allow rule is fired, the move is invalid
        if (fired == null) return false;

        //Check if with this build, the player has lost
        for(CompiledCardRule rule : denyBuildRules){
            rule.execute(null,buildData,false);
        }
        //Check if with this build, the player has won
        for(CompiledCardRule rule : winBuildRules){
            try {
                rule.execute(null, buildData, false);
            } catch (PlayerWonSignal e){
                fired.applyEffect(null, buildData);
                throw new PlayerWonSignal(e.getPlayer());
            }
        }
        //If the build was normal and allowed, apply its effect
        fired.applyEffect(null,buildData);

        return true;
    }

    /**
     * Tests if the worker of the supplied player can move at least once, in any direction.
     * This will be checked according to his specific rules (abilities), not just using default movements.
     * @param player Player instance
     * @param worker Worker instance (must be owned by the supplied player)
     * @return True, if exists one possible move for the specified worker, False otherwise
     */
    public boolean canMove(Player player, Worker worker){
        assert (player != null && worker != null);
        List<Point> currTry = new LinkedList<>();
        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                if (i!=0 || j!=0){ //Excluding current worker position i==0 && j==0
                    Point destPoint = new Point(worker.getPosition().x + i, worker.getPosition().y + j);
                    Cell destCell = board.getCell(destPoint);
                    if (destCell != null){ //Inside board
                        if (destCell.getTopBuilding() != LevelType.DOME){ //No dome
                            currTry.clear();
                            currTry.add(destPoint);
                            MoveData moveTry = new MoveData(player,worker,currTry);
                            if (getAllowMoveRule(moveTry) != null) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Tests if the worker of the supplied player can build at least once, in any direction.
     * This will be checked according to his specific rules (abilities), not just using default builds rules.
     * @param player Player instance
     * @param worker Worker instance (must be owned by the supplied player)
     * @return True, if exists one possible build for the specified worker, False otherwise
     */
    public boolean canBuild(Player player, Worker worker){
        assert (player != null && worker != null);
        List<Point> currTry = new LinkedList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildData = new LinkedList<>();

        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                Point destPoint = new Point(worker.getPosition().x + i, worker.getPosition().y + j);
                Cell destCell = board.getCell(destPoint);
                if (destCell != null){ //Inside board
                    BuildingType nextBuildable = destCell.getNextBuildable();
                    if (nextBuildable != null){ //Next building available
                        currTry.clear();
                        builds.clear();
                        buildData.clear();
                        currTry.add(destPoint);
                        buildData.add(nextBuildable);
                        builds.put(destPoint, buildData);
                        BuildData buildTry = new BuildData(player,worker,builds,currTry);
                        if (getAllowBuildRule(buildTry) != null) return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets all possible points where the specified worker can move at the next opportunity it has.
     * Very similar to canMove, used for strategy testing purposes at the moment.
     * @param player Player instance
     * @param worker Worker instance (must be owned by the supplied player)
     * @return Set of points, representing all possible cells reachable for the worker, with a single movement.
     */
    public Set<Point> getPossibleMoves(Player player, Worker worker){
        assert (player != null && worker != null);
        Set<Point> result = new HashSet<>();
        List<Point> currTry = new LinkedList<>();
        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                if (i!=0 || j!=0){ //Excluding current worker position i==0 && j==0
                    Point destPoint = new Point(worker.getPosition().x + i, worker.getPosition().y + j);
                    Cell destCell = board.getCell(destPoint);
                    if (destCell != null){ //Inside board
                        if (destCell.getTopBuilding() != LevelType.DOME){ //No dome
                            currTry.clear();
                            currTry.add(destPoint);
                            MoveData moveTry = new MoveData(player,worker,currTry);
                            if (getAllowMoveRule(moveTry) != null) result.add(destPoint);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets all possible points where the specified worker can build at the next opportunity it has.
     * Very similar to canBuild, used for strategy testing purposes at the moment.
     * @param player Player instance
     * @param worker Worker instance (must be owned by the supplied player)
     * @return Set of points, representing all possible cells where the worker can build.
     */
    public Set<Point> getPossibleBuilds(Player player, Worker worker){
        assert (player != null && worker != null);
        Set<Point> result = new HashSet<>();
        List<Point> currTry = new LinkedList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildData = new LinkedList<>();

        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                Point destPoint = new Point(worker.getPosition().x + i, worker.getPosition().y + j);
                Cell destCell = board.getCell(destPoint);
                if (destCell != null){ //Inside board
                    BuildingType nextBuildable = destCell.getNextBuildable();
                    if(nextBuildable != null){ //Next building available
                        currTry.clear();
                        builds.clear();
                        buildData.clear();
                        currTry.add(destPoint);
                        buildData.add(nextBuildable);
                        builds.put(destPoint, buildData);
                        BuildData buildTry = new BuildData(player,worker,builds,currTry);
                        if (getAllowBuildRule(buildTry) != null) result.add(destPoint);
                    }
                }
            }
        }
        return result;
    }

    private CompiledCardRule getAllowMoveRule(MoveData moveData){
        assert (moveData != null && allowMoveRules != null);
        CompiledCardRule fired = null;
        try{
            for(CompiledCardRule rule : allowMoveRules){
                if (rule.execute(moveData, null, true)){
                    fired = rule;
                    break;
                }
            }
        } catch (PlayerWonSignal | PlayerLostSignal ignored) {
            assert false;
        }
        return fired;
    }

    private CompiledCardRule getAllowBuildRule(BuildData buildData){
        assert (buildData != null && allowBuildRules != null);
        CompiledCardRule fired = null;
        try{
            for(CompiledCardRule rule : allowBuildRules){
                if (rule.execute(null, buildData, true)){
                    fired = rule;
                    break;
                }
            }
        } catch (PlayerWonSignal | PlayerLostSignal ignored) {
            assert false;
        }
        return fired;
    }
}
