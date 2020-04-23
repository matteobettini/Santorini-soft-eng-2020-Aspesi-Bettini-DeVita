package it.polimi.ingsw;

import it.polimi.ingsw.packets.ConnectionMessages;
import it.polimi.ingsw.packets.PacketCardsFromServer;
import it.polimi.ingsw.packets.PacketMatchStarted;
import it.polimi.ingsw.packets.PacketStartPlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SampleClient {

    private Socket socket;

    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Scanner input;


    public static void main(String[] args) {

        new SampleClient().startClient("127.0.0.1", 4567);


    }


    public void startClient(String address, int port) {

        try {
            this.socket = new Socket(address, port);
            System.out.println("Client: connessione stabilita a server: " + address + " " + port);
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
            input = new Scanner(System.in);

            while (true) {
                Object packetFromServer = is.readObject();
                if (packetFromServer instanceof ConnectionMessages) {
                    ConnectionMessages messageFromServer = (ConnectionMessages) packetFromServer;
                    if (messageFromServer == ConnectionMessages.INSERT_NICKNAME) {
                        System.out.println(messageFromServer.getMessage());
                        String name = input.nextLine();
                        sendString(name);
                    } else if (messageFromServer == ConnectionMessages.INSERT_NUMBER_OF_PLAYERS) {
                        System.out.println(messageFromServer.getMessage());
                        int numOfPlayers = input.nextInt();
                        System.out.println("Chosen num of players is: " + numOfPlayers);
                        sendInt(numOfPlayers);
                    } else if (messageFromServer == ConnectionMessages.IS_IT_HARDCORE) {
                        System.out.println(messageFromServer.getMessage());
                        boolean hardcore = input.nextBoolean();
                        sendBool(hardcore);
                    } else if (messageFromServer == ConnectionMessages.INVALID_NICKNAME) {
                        System.out.println(messageFromServer.getMessage());
                        String name = input.nextLine();
                        sendString(name);
                    } else if (messageFromServer == ConnectionMessages.CONNECTION_CLOSED) {
                        System.out.println(messageFromServer.getMessage());
                    } else if (messageFromServer == ConnectionMessages.MATCH_ENDED) {
                        System.out.println(messageFromServer.getMessage());
                    }
                } else if(packetFromServer instanceof PacketMatchStarted){
                    PacketMatchStarted packetMatchStarted = (PacketMatchStarted) packetFromServer;
                    System.out.println("\nMatch started!!!\nPlayers: " + packetMatchStarted.getPlayers() +"\nHardcore: " + packetMatchStarted.isHardcore());
                } else if(packetFromServer instanceof PacketCardsFromServer){
                    PacketCardsFromServer packetCardsFromServer = (PacketCardsFromServer) packetFromServer;
                    System.out.println("\nYou are the challenger!\nHere are all the cards: " + packetCardsFromServer.getAvailableCards() + "\nChoose: " + packetCardsFromServer.getNumberToChoose());
                }
            }



        } catch (IOException | ClassNotFoundException e) {
            closeRoutine();
        }


    }
    public void sendString(String s){
        try {
            os.writeUTF(s);
            os.flush();
        }catch (IOException e){
            System.err.println("Errror when sending from server, message: " + e.getMessage());
            closeRoutine();
        }
    }


    public void sendInt(int n){
        try {
            os.writeInt(n);
            os.flush();
        }catch (IOException e){
            System.err.println("Errror when sending from server, message: " + e.getMessage());
            closeRoutine();
        }
    }

    public void sendBool(boolean b){
        try {
            os.writeBoolean(b);
            os.flush();
        }catch (IOException e){
            System.err.println("Errror when sending from server, message: " + e.getMessage());
            closeRoutine();
        }
    }

    public void send(Object packet){
        try {
            os.writeObject(packet);
            os.flush();
        }catch (IOException e){
            System.err.println("Errror when sending from server, message: " + e.getMessage());
            closeRoutine();
        }
    }

    public void closeRoutine(){

        try {
            is.close();
        }catch (IOException ignored){}
        try {
            os.close();
        }catch (IOException ignored){}
        try{
            socket.close();
        }catch (IOException ignored){ }

        System.out.println("Socket is closed totally");
    }






}

