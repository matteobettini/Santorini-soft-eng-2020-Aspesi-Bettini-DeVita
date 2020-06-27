# Prova Finale Ingegneria del Software 2020
# Scaglione Margara

## Gruppo AM09


- ###   10568949    Andrea Aspesi ([@AspesiAndrea](https://github.com/AspesiAndrea))<br>andrea1.aspesi@mail.polimi.it
- ###   10580728    Matteo Bettini ([@MatteoBettini](https://github.com/MatteoBettini))<br>matteo1.bettini@mail.polimi.it
- ###   10560693    Mirko De Vita ([@MirkoDeVita98](https://github.com/MirkoDeVita98))<br>mirko.devita@mail.polimi.it

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Persistence | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Advanced Gods | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Undo | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |

<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->

# Santorini

![Santorini Logo](https://github.com/MatteoBettini/ing-sw-2020-Aspesi-Bettini-DeVita/blob/master/logo.png)

## Setup

- In the [deliveries](https://github.com/MatteoBettini/ing-sw-2020-Aspesi-Bettini-DeVita/tree/master/deliveries) folder there are two multi-platform jar files, one to set the Server up and the other one to start the Client.
- The Server can be run with the following command:
    ```shell
    > java -jar GameServer.jar
    ```
  This command can be followed by these arguments:
  - -port: followed by the desired port number between MIN_PORT and MAX_PORT as argument;
  - -v: to activate logging on the console;
  - -log: followed by a file name, to activate logging both in the console and in the chosen file;
  - -help: to get help.
  
- The Client can be run with the following command:
    ```shell
    > java -jar GameClient.jar
    ```
  - This command sets the Client on Graphical User Interface(GUI) mode, but it can be followed by **-cli** if the Command Line Interface(CLI) is preferred.
  - The Server's IP to connect to can be specified during the execution.
  
 ## Extra
 
 Two game-modes are implemented:
 - Normal: the Server sends the possible moves/builds to the Client so that they are displayed to the Player during his/her turn.
 - Hardcore: in this mode there are no suggestions and the Player can lose if he/she does not obey to Gods' rules.
 
 ## Testing and Documentation
 
 The [model package](https://github.com/MatteoBettini/ing-sw-2020-Aspesi-Bettini-DeVita/tree/master/src/main/java/it/polimi/ingsw/server/model) is entirely tested, the tests' coverage report and the JavaDoc can be accessed [here](https://github.com/MatteoBettini/ing-sw-2020-Aspesi-Bettini-DeVita/tree/master/deliveries).
 
 ## Tools
 
 * [Draw.io](http://www.draw.io/) - UML Diagram
 * [Maven](https://maven.apache.org/) - Dependency Management
 * [IntelliJ](https://www.jetbrains.com/idea/) - IDE
 * [JavaFX](https://openjfx.io) - Graphical Framework
 
 ## License
 
 This project is developed in collaboration with [Politecnico di Milano](https://www.polimi.it) and [Cranio Creations](http://www.craniocreations.it).
 
