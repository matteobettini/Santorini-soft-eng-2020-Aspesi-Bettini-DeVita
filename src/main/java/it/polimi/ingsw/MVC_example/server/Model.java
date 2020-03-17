package it.polimi.ingsw.MVC_example.server;

import it.polimi.ingsw.MVC_example.common.Listenable;


public class Model extends Listenable implements Cloneable{

    private int result;

    public Model(){
        result = 0;
    }


    public void sum(int a, int b){
        result = a + b;
        this.notifyListeners(result,0,"da Model");
    }


   @Override
   public Model clone(){
        Model model = new Model();
        model.result = result;
        return model;
   }

    public int getResult(){
        return result;
    }





}
