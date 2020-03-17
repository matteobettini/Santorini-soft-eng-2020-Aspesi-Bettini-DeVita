package it.polimi.ingsw.MVC_example.client;

import it.polimi.ingsw.MVC_example.common.Listenable;
import it.polimi.ingsw.MVC_example.common.MoveAcquirer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client extends Listenable implements MoveAcquirer {

    private Socket socket;
    private Scanner in;
    private PrintWriter out;


    public void startCLient(String address,int port ){
        try {
            this.socket = new Socket(address, port);
            System.out.println("Client: connessione stabilita a server: " + address + " " + port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());


        } catch (IOException e) {
            System.err.println("Client: connessione non avvenuta!");
        }
    }

    public void send(int a,int b){
        out.println(a);
        out.println(b);
        out.flush();
        receive();
    }

    public void receive(){
        try{
            String msg = in.nextLine();
            this.notifyListeners(Integer.parseInt(msg),0,"client");
        } catch(NoSuchElementException e){
                e.printStackTrace();
        }
    }
}
