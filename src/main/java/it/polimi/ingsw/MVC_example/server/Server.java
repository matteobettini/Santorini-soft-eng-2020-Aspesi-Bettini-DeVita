package it.polimi.ingsw.MVC_example.server;

import it.polimi.ingsw.MVC_example.common.Listenable;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Server extends Listenable {

    private int port;
    private ServerSocket serverSocket;
    private Socket client;
    private Scanner in;
    private PrintWriter out;

    public Server(int port){
        this.port = port;

    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server pronto sulla porta: " + port);
        client = serverSocket.accept();

        in = new Scanner(client.getInputStream());
        out = new PrintWriter(client.getOutputStream());

        recieve();

    }

    public void sendInfo(int c){
        out.println(c);
        out.flush();
        recieve();
    }
    public void recieve(){
        int a = in.nextInt();
        int b = in.nextInt();
        notifyListeners(new Point(a, b),0,"da Server" );
    }


}
