package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.buildings.BuildingFactory;
import it.polimi.ingsw.model.enums.BuildingType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphicalCell implements CharFigure{
    private final CharStream stream;
    private final List<CharFigure> buildings;
    private GraphicalWorker worker;
    private final Point position;
    private final int RATEOX;
    private final int RATEOY;

    public GraphicalCell(Point position, CharStream stream, int RATEOX, int RATEOY){
        this.stream = stream;
        this.buildings = new ArrayList<>();
        this.position = position;
        this.RATEOX = RATEOX;
        this.RATEOY= RATEOY;
    }

    public void setWorker(String workerID) {

        MatchData matchData = MatchData.getInstance();

        String playerName;
        int workerNumber;

        for(String player : matchData.getIds().keySet()){
            if(matchData.getIds().get(player).contains(workerID)){
                playerName = player;
                workerNumber = matchData.getIds().get(player).indexOf(workerID);
                Color color = matchData.getPlayersColor().get(playerName);
                if(playerName != null) this.worker = new GraphicalWorker(stream, color,RATEOX / 4, RATEOY / 4, workerNumber + 1, playerName);
            }
        }
    }

    public GraphicalWorker getWorker(){
        return this.worker;
    }

    public void removeWorker(){
        this.worker = null;
    }

    public Point getPosition() {
        return position;
    }

    public void addBuilding(BuildingType buildingType){
        buildings.add(BuildingFactory.getBuilding(stream, buildingType, RATEOX, RATEOY));
    }

    @Override
    public void draw() {
        draw(0,0);
    }

    @Override
    public void draw(int relX, int relY) {
        for(CharFigure building : buildings){
            building.draw(relX, relY);
        }
        if(worker != null) worker.draw(relX, relY);
    }
}
