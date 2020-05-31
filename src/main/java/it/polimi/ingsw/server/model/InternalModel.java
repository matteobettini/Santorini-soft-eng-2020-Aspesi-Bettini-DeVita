package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.cards.CardFactory;
import it.polimi.ingsw.server.cards.CardFile;
import it.polimi.ingsw.server.cards.CardRule;
import it.polimi.ingsw.server.cards.enums.TriggerType;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.server.model.enums.LevelType;
import it.polimi.ingsw.server.model.exceptions.PlayerLostSignal;
import it.polimi.ingsw.server.model.exceptions.PlayerWonSignal;
import it.polimi.ingsw.common.packets.InvalidPacketException;
import it.polimi.ingsw.common.packets.PacketBuild;
import it.polimi.ingsw.common.packets.PacketMove;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains all the actual instances of the Model data.
 * It is protected from the external of the package and it handles the entry points of the Model.
 */
class InternalModel {

    private final CardFactory cardFactory;
    private final Board board;
    private List<Player> players;
    private Player winner;
    private final List<Player> losers;
    private List<CompiledCardRule> allowMoveRules;
    private List<CompiledCardRule> denyMoveRules;
    private List<CompiledCardRule> winMoveRules;
    private List<CompiledCardRule> allowBuildRules;
    private List<CompiledCardRule> denyBuildRules;
    private List<CompiledCardRule> winBuildRules;
    private boolean isHardCore;

    /**
     * Create a data model instance, with the given settings
     * @param players List of players nicknames
     * @param factory Singleton instance of the card reader object
     * @param isHardCore True if hardcore mode must be enabled
     */
    InternalModel(List<String> players, CardFactory factory, boolean isHardCore){
        assert (players != null && factory != null && players.size() >= 2);
        this.players = new LinkedList<>();
        for(String p : players){
            this.players.add(new Player(p));
        }
        this.board = new Board();
        this.winner = null;
        this.losers = new LinkedList<>();
        this.cardFactory = factory;
        this.isHardCore = isHardCore;
    }

    /**
     * Create a data model instance, with the given settings in hardcore mode
     * @param players List of players nicknames
     * @param factory Singleton instance of the card reader object
     */
    InternalModel(List<String> players, CardFactory factory){
        this(players,factory,true);
    }

    /**
     * Getter for strategy mode
     * @return True if hardcore is enabled, False otherwise
     */
    public boolean isHardCore(){
        return this.isHardCore;
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
    public List<Player> getPlayers(){ return new LinkedList<>(this.players); }

    /**
     * Getter that returns the Player given his nickname.
     * @param playerNick is a String that contains the Player nickname.
     * @return the Player that has playerNick as nickname.
     */
    public Player getPlayerByNick(String playerNick){
        if (playerNick == null) return null;
        for(Player p : this.players){
            if(p.getNickname().equals(playerNick)) return p;
        }
        return null;
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
     * Gets the winner of the match
     * @return Player instance of the winner. Null if there is no winner yet.
     */
    public Player getWinner() { return this.winner; }

    /**
     * Gets the losers of the match
     * @return List of Player instances of the losers. Null if there are no losers.
     */
    public List<Player> getLosers() { return new LinkedList<>(this.losers); }

    /**
     * Removes player and its workers from the board.
     * Adds the player to the losers list
     * @param loser Player instance of the loser
     */
    public void addLoser(Player loser){
        assert loser != null;
        assert !losers.contains(loser);
        assert players.contains(loser);
        losers.add(loser);
        //Clear loser workers
        for (Worker worker : loser.getWorkers()){
            board.getCell(worker.getPosition()).removeWorker();
            worker.removeFromBoard();
        }
        //Rebuild rules excluding losers' God Powers
        compileCardStrategy();
    }

    /**
     * Sets a player as the winner of the match
     * @param winner Player instance of the winner
     */
    public void setWinner(Player winner){
        assert this.winner == null;
        assert winner != null;
        this.winner = winner;
    }

    /**
     * Reorders match players, from the given starting player.
     * The original list is shifted left to make the starting player the first of the list.
     * @param player Player instance to make the first.
     */
    public void setStartPlayer(Player player){
        assert (player != null);
        assert (players.contains(player));
        int indexOfPlayer = players.indexOf(player);
        List<Player> tmpList = new LinkedList<>();
        for(int i=indexOfPlayer;i<players.size();i++){
            tmpList.add(players.get(i));
        }
        for(int i=0;i<indexOfPlayer;i++){
            tmpList.add(players.get(i));
        }
        players = tmpList;
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
        
        if(buildsOrder == null || builds == null || buildsOrder.isEmpty() || builds.isEmpty()  || p == null || w == null || !p.getWorkers().contains(w)) throw new InvalidPacketException();

        int numBuilds = 0; //Number of buildings in this packet
        for(Point pos : builds.keySet()) {
            if (builds.get(pos) == null || builds.get(pos).isEmpty() || board.getCell(pos) == null)
                throw new InvalidPacketException();
            if (!Board.areAdjacent(pos, w.getPosition(), true))
                throw new InvalidPacketException();
            numBuilds += builds.get(pos).size();
        }

        //Check order points
        for(Point point : buildsOrder)
            if(!builds.containsKey(point))
                throw new InvalidPacketException();

        //Check order points size -> one point per building
        if (buildsOrder.size() != numBuilds)
            throw new InvalidPacketException();

        return new BuildData(p, w, builds, buildsOrder);
    }

    /**
     * Compiles Card Rules, creating executable lambdas adapted to this particular
     * model instance.
     */
    public void compileCardStrategy(){
        allowMoveRules = new ArrayList<>();
        denyMoveRules = new ArrayList<>();
        winMoveRules = new ArrayList<>();
        allowBuildRules = new ArrayList<>();
        denyBuildRules = new ArrayList<>();
        winBuildRules = new ArrayList<>();

        //Get players still in game
        List<Player> stillInGame = players.stream().filter(p->!losers.contains(p)).collect(Collectors.toList());

        //Adding default rules
        CardFile defaultStrategy = cardFactory.getDefaultStrategy();
        for(CardRule rule : defaultStrategy.getRules()){
            compileAndAddRule(rule,null);
        }
        //Adding card rules
        for(Player player : stillInGame){
            //assert(player.getCard() != null);
            if(player.getCard() != null){
                for(CardRule rule : player.getCard().getRules()){
                    compileAndAddRule(rule,player);
                }
            }

        }
        //Compile state info for players
        MapCompiler.compileMap(stillInGame,cardFactory.getDefaultStrategy());
    }

    private void compileAndAddRule(CardRule rule, Player owner){
        assert (rule != null);
        CompiledCardRule compiledCardRule = RuleCompiler.compile(this, rule, owner);
        switch (rule.getEffect().getType()){
            case ALLOW:
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
            default:
                assert false;
        }
    }

    /**
     * Tries to make a move for the player. If the move is compatible with current game rules,
     * then the move effect will be applied to the model.
     * @param moveData Move data containing all information needed to process the rules
     * @return True, if the move was applied, False if the move is invalid and must be repeated
     * @throws PlayerWonSignal If the player has won the game with this move
     * @throws PlayerLostSignal If the player has lost the game with this move (and mode is hardcore)
     */
    public boolean makeMove(MoveData moveData) throws PlayerWonSignal, PlayerLostSignal {
        assert (allowMoveRules != null && denyMoveRules != null && winMoveRules != null && moveData != null);
        //Check if player can do this move
        CompiledCardRule fired = getAllowMoveRule(moveData);
        //If no allow rule is fired, the move is invalid
        if (fired == null) return false;

        //If is hardcore, you can lose by violating gods rules
        if (isHardCore){
            //Check if with this move, the player has lost
            for(CompiledCardRule rule : denyMoveRules){
                rule.execute(moveData,null,false);
            }
        }

        try{
            //Check if with this move, the player has won
            for(CompiledCardRule rule : winMoveRules){
                rule.execute(moveData,null,false);
            }
        } finally {
            //If the move was allowed, apply its effect
            fired.applyEffect(moveData,null);
        }

        return true;
    }

    /**
     * Tries to make a build for the player. If the build is compatible with current game rules,
     * then the build effect will be applied to the model.
     * @param buildData Build data containing all information needed to process the rules
     * @return True, if the build was applied, False if the build is invalid and must be repeated
     * @throws PlayerWonSignal If the player has won the game with this build
     * @throws PlayerLostSignal If the player has lost the game with this build (and mode is hardcore)
     */
    public boolean makeBuild(BuildData buildData) throws PlayerWonSignal, PlayerLostSignal{
        assert (allowBuildRules != null && denyBuildRules != null && winBuildRules != null && buildData != null);
        //Check if player can do this move
        CompiledCardRule fired = getAllowBuildRule(buildData);
        //If no allow rule is fired, the move is invalid
        if (fired == null) return false;

        //If is hardcore, you can lose by violating gods rules
        if (isHardCore){
            //Check if with this build, the player has lost
            for(CompiledCardRule rule : denyBuildRules){
                rule.execute(null,buildData,false);
            }
        }

        try{
            //Check if with this build, the player has won
            for(CompiledCardRule rule : winBuildRules){
                rule.execute(null, buildData, false);
            }
        } finally {
            //If the build was allowed, apply its effect
            fired.applyEffect(null,buildData);
        }

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
     * Tests if at least one of the workers owned by the specified player can move at least once, in any direction.
     * This will be checked according to his specific rules (abilities), not just using default movements.
     * @param player Player instance
     * @return True, if exists one possible move for at least one of his worker, False otherwise
     */
    public boolean canMove(Player player){
        assert player != null;
        for(Worker worker : player.getWorkers()){
            if (canMove(player,worker))
                return true;
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
     * Tests if at least one of the workers owned by the specified player can build at least once, in any direction.
     * This will be checked according to his specific rules (abilities), not just using default builds rules.
     * @param player Player instance
     * @return True, if exists one possible build for at least one of his workers worker, False otherwise
     */
    public boolean canBuild(Player player){
        assert player != null;
        for (Worker worker : player.getWorkers()){
            if (canBuild(player,worker))
                return true;
        }
        return false;
    }

    /**
     * Gets all possible points where the specified worker can move at the next opportunity it has.
     * @param player Player instance
     * @param worker Worker instance (must be owned by the supplied player)
     * @return Set of points, representing all possible cells reachable for the worker, with a single movement.
     */
    public Set<Point> getPossibleMoves(Player player, Worker worker){
        assert (player != null && worker != null);
        List<Point> moves = new LinkedList<>();
        return getPossibleMoves(new MoveData(player,worker,moves));
    }

    /**
     * Gets all possible points accessible for the worker (based on a started move path), with a single movement.
     * Already visited points will be excluded from results.
     * @param moveData Data of the path from where starting the check.
     * @return List of visitable points for the player's worker
     */
    public Set<Point> getPossibleMoves(MoveData moveData){
        assert (moveData != null);
        Set<Point> result = new HashSet<>();

        if (!moveData.getData().isEmpty() && hasWonWithMove(moveData))
            return result; //If already won, no sense to check further

        List<Point> moves = new ArrayList<>(moveData.getData());

        Point startPoint;
        if (moves.isEmpty())
            startPoint = moveData.getWorker().getPosition(); //Use worker position if empty
        else
            startPoint = moves.get(moves.size()-1); //Take last point in the packet, if not empty

        Player player = moveData.getPlayer();
        Worker worker = moveData.getWorker();

        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                if (i!=0 || j!=0){ //Excluding start position i==0 && j==0
                    Point destPoint = new Point(startPoint.x + i, startPoint.y + j);
                    //We should grant all condition of packetMove to MoveData converter, to ensure not to create malformed data
                    // - points inside board
                    // - not move over domes
                    Cell destCell = board.getCell(destPoint);
                    if (destCell != null){ //Inside board
                        if (destCell.getTopBuilding() != LevelType.DOME){ //No dome
                            if (!moves.contains(destPoint)) { //Exclude already visited points
                                moves.add(destPoint); //Add the test point
                                MoveData moveTry = new MoveData(player,worker,moves);
                                if (getAllowMoveRule(moveTry) != null) //If this move is allowed
                                    result.add(destPoint);
                                moves.remove(moves.size()-1); //Remove the test point
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Function returns the points where you can build and also which building you can build where
     * @param player Player who wants to build
     * @param worker Worker used by the player
     * @return Map where every point (representing cells where the worker can build) contains a list of building types allowed
     */
    public Map<Point, List<BuildingType>> getPossibleBuilds(Player player, Worker worker){
        assert (player != null && worker != null);

        List<Point> buildOrder = new LinkedList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();

        return getPossibleBuilds(new BuildData(player,worker,builds,buildOrder));
    }

    /**
     * Function returns the points where you can build and also which building you can build where
     * @param buildData Data of the history from where starting the check.
     * @return Map where every point (representing cells where the worker can build) contains a list of building types allowed
     */
    public Map<Point, List<BuildingType>> getPossibleBuilds(BuildData buildData){
        assert (buildData != null);
        Map<Point, List<BuildingType>> result = new HashMap<>();

        if (!buildData.getData().isEmpty() && hasWonWithBuild(buildData))
            return result; //If already won, no sense to check further

        //Clone data from the original data packet
        List<Point> buildOrder = new ArrayList<>(buildData.getDataOrder());
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        for(Point point : buildData.getData().keySet()){
            builds.put(point, new ArrayList<>(buildData.getData().get(point)));
        }
        //Init useful vars for optimizing cycle execution
        List<BuildingType> possibleBuildings = new LinkedList<>();
        Player player = buildData.getPlayer();
        Worker worker = buildData.getWorker();
        Point startPosition = worker.getPosition();

        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                Point destPoint = new Point(startPosition.x + i, startPosition.y + j);
                Cell destCell = board.getCell(destPoint);
                //We should grant all condition of packetBuild to BuildData converter, to ensure not to create malformed data
                // - points inside board
                // - not build over domes
                // - one point of order for building
                if (destCell != null){ //Inside board
                    BuildingType nextBuildable;
                    if (builds.containsKey(destPoint)){ //If I have already at least one building for this point
                        List<BuildingType> destBuildings = builds.get(destPoint); //Get these buildings
                        nextBuildable = destCell.getNextBuildable(destBuildings); //Get next buildable
                        if (nextBuildable == null)
                            continue; //If there isn't, just skip this point

                        destBuildings.add(nextBuildable); //Add new building to these
                        buildOrder.add(destPoint); //Also add one point for this new building
                        possibleBuildings.clear(); //Clear possible buildings for this point
                        //Try make a normal build
                        BuildData buildTry = new BuildData(player,worker,builds,buildOrder);
                        if (getAllowBuildRule(buildTry) != null){ //If permitted
                            possibleBuildings.add(nextBuildable); //Add to permitted of this point
                        }
                        destBuildings.remove(destBuildings.size()-1); //Remove test building for next try
                        if (nextBuildable != BuildingType.DOME){ //Explicitly check for dome, only case permitted everywhere
                            destBuildings.add(BuildingType.DOME);
                            //Same building point, so not changing currTry
                            //Try dome building
                            buildTry = new BuildData(player,worker,builds,buildOrder);
                            if (getAllowBuildRule(buildTry) != null){
                                possibleBuildings.add(BuildingType.DOME);
                            }
                            destBuildings.remove(destBuildings.size()-1); //Remove for next try
                        }
                        buildOrder.remove(buildOrder.size()-1); //Remove current try point order
                        //If at least one succeeded, add the point
                        if (possibleBuildings.size() > 0)
                            result.put(destPoint, new LinkedList<>(possibleBuildings));

                    } else { //There aren't buildings for this point
                        nextBuildable = destCell.getNextBuildable();
                        if (nextBuildable == null) //Skip if cannot build here
                            continue;
                        List<BuildingType> destBuildings = new LinkedList<>();
                        destBuildings.add(nextBuildable);
                        builds.put(destPoint, destBuildings);
                        buildOrder.add(destPoint); //Want to add one building
                        possibleBuildings.clear();
                        //Normal build
                        BuildData buildTry = new BuildData(player,worker,builds,buildOrder);
                        if (getAllowBuildRule(buildTry) != null){
                            possibleBuildings.add(nextBuildable);
                        }
                        if (nextBuildable != BuildingType.DOME){ //Explicitly check for dome, only case permitted everywhere
                            destBuildings.clear();
                            destBuildings.add(BuildingType.DOME);
                            //Try dome building
                            buildTry = new BuildData(player,worker,builds,buildOrder);
                            if (getAllowBuildRule(buildTry) != null){
                                possibleBuildings.add(BuildingType.DOME);
                            }
                        }
                        //If at least one succeeded, add the point
                        if (possibleBuildings.size() > 0)
                            result.put(destPoint, new LinkedList<>(possibleBuildings));

                        builds.remove(destPoint); //Remove always added point
                        buildOrder.remove(buildOrder.size()-1);
                    }
                }
            }
        }
        return result;
    }

    private CompiledCardRule getAllowMoveRule(MoveData moveData){
        assert (moveData != null && allowMoveRules != null && denyMoveRules != null);
        CompiledCardRule fired = null;
        try{
            for(CompiledCardRule rule : allowMoveRules){
                if (rule.execute(moveData, null, true)){
                    fired = rule;
                    break;
                }
            }
            if (!isHardCore && fired != null){
                try{
                    for(CompiledCardRule rule : denyMoveRules){
                        rule.execute(moveData, null, false);
                    }
                } catch (PlayerLostSignal ex) {
                    return null;
                }
            }
        } catch (PlayerWonSignal | PlayerLostSignal ignored) {
            assert false;
        }
        return fired;
    }

    private CompiledCardRule getAllowBuildRule(BuildData buildData){
        assert (buildData != null && allowBuildRules != null && denyBuildRules != null);
        CompiledCardRule fired = null;
        try{
            for(CompiledCardRule rule : allowBuildRules){
                if (rule.execute(null, buildData, true)){
                    fired = rule;
                    break;
                }
            }
            if (!isHardCore && fired != null){
                try{
                    for(CompiledCardRule rule : denyBuildRules){
                        rule.execute(null, buildData, false);
                    }
                } catch (PlayerLostSignal ex) {
                    return null;
                }
            }
        } catch (PlayerWonSignal | PlayerLostSignal ignored) {
            assert false;
        }
        return fired;
    }

    private boolean hasWonWithMove(MoveData moveData){
        if (getAllowMoveRule(moveData) == null) return false; //Search the allow for the move
        try{
            //Check if a win rule fires
            for(CompiledCardRule rule : winMoveRules){
                rule.execute(moveData,null,false);
            }
        } catch (PlayerWonSignal playerWonSignal) {
            return true;
        } catch (PlayerLostSignal ignored) { }
        return false;
    }

    private boolean hasWonWithBuild(BuildData buildData){
        if (getAllowBuildRule(buildData) == null) return false; //Search the allow for the build
        try{
            //Check if a win rule fires
            for(CompiledCardRule rule : winBuildRules){
                rule.execute(null,buildData,false);
            }
        } catch (PlayerWonSignal playerWonSignal) {
            return true;
        } catch (PlayerLostSignal ignored) { }
        return false;
    }
}
