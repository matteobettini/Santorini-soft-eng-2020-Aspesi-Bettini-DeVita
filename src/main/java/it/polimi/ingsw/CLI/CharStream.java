package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.colors.BackColor;
import it.polimi.ingsw.CLI.colors.ForeColor;

import java.io.PrintStream;
import java.util.List;

public class CharStream {

    private final int height;
    private final int width;
    private final char[][] content;
    private final String[][]colors;

    private static final String ANSI_RESET  = "\u001B[0m";

    /**
     * This constructor initializes the stream's width and height to the given ones.
     * The content is saved in a height * width char matrix and the colors in a height * width String matrix.
     * @param width is the width of the stream.
     * @param height is the height of the stream.
     */
    public CharStream(int width, int height){
        this.height = height;
        this.width = width;
        this.content = new char[height][width];
        this.colors = new String[height][width];
    }

    /**
     * This method returns the width of the stream.
     * @return an integer.
     */
    public int getWidth() {
        return width;
    }

    /**
     * This method returns the height of the stream.
     * @return an integer.
     */
    public int getHeight() {
        return height;
    }

    /**
     * This method returns the char saved on the stream's position X, Y,
     * @return an char.
     */
    public char getChar(int x, int y){
        if (x < 0 || x >= width || y < 0 || y >= height) return 0;
        return content[y][x];
    }

    /**
     * This method add the given char and its background color to the stream's position X, Y,
     * @param content is the char to add.
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param backColor is the color of the background.
     */
    public void addChar(char content,int x, int y,  BackColor backColor){
        addChar(content, x , y , null, backColor);
    }

    /**
     * This method add the given char and its color to the stream's position X, Y,
     * @param content is the char to add.
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param foreColor is the color of the content.
     */
    public void addChar(char content,int x, int y,  ForeColor foreColor){
        addChar(content, x , y , foreColor, null);
    }

    /**
     * This method add the given char to the stream's position X, Y,
     * @param content is the char to add.
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     */
    public void addChar(char content,int x, int y){
        addChar(content, x , y , null, null);
    }


    /**
     * This method add the given char, its color and background color to the stream's position X, Y,
     * @param content is the char to add.
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param foreColor is the color of the content.
     * @param backColor is the color of the background.
     */
    public void addChar(char content,int x, int y, ForeColor foreColor, BackColor backColor){
        if(x < 0 || x >= width || y < 0 || y >= height) return;
        this.content[y][x] = content;
        addColor(x, y , foreColor, backColor);
    }

    /**
     * This method add the given String, its color and background color starting from the stream's position X, Y,
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param str is the String to add.
     * @param foreColor is the color of the content.
     * @param backColor is the color of the background.
     */
    public boolean addString(int x, int y, String str, ForeColor foreColor, BackColor backColor){
        if (x < 0 || x + str.length() >= width || y < 0 || y  >= height) return false;
        for(int x1 = 0 ; x1 <str.length(); x1++){
            content[y][x + x1] = str.charAt(x1);
            addColor(x + x1, y, foreColor, backColor);
        }
        return true;
    }

    /**
     * This method add the given String and its color starting from the stream's position X, Y,
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param str is the String to add.
     * @param foreColor is the color of the content.
     */
    public boolean addString(int x, int y, String str, ForeColor foreColor){
        return addString(x, y, str, foreColor, null);
    }

    /**
     * This method add the given String and its background color starting from the stream's position X, Y,
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param str is the String to add.
     * @param backColor is the color of the background.
     */
    public boolean addString(int x, int y, String str, BackColor backColor){
        return addString(x, y, str, null, backColor);
    }

    /**
     * This method add the given String starting from the stream's position X, Y,
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param str is the String to add.
     */
    public boolean addString(int x, int y, String str){
        return addString(x,y,str,null,null);
    }


    /**
     * This method set the content color and the background color in the stream's position X, Y,
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param foreColor is the content color to set.
     * @param backColor  is the background color to set.
     */
    public void addColor(int x, int y, ForeColor foreColor, BackColor backColor){
        if (x<0 || x >= width || y<0 || y>=height) return;
        if (foreColor == null && backColor == null) colors[y][x] = null;
        else colors[y][x] = (foreColor != null ? foreColor.getCode() : "") + (backColor!= null ? backColor.getCode() : "");
    }

    /**
     * This method set the background color in the stream's position X, Y,
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param backColor  is the background color to set.
     */
    public void addColor(int x, int y, BackColor backColor){
        addColor(x, y, null, backColor);
    }

    /**
     * This method set the content color in the stream's position X, Y,
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param foreColor is the content color to set.
     */
    public void addColor(int x, int y, ForeColor foreColor){
        addColor(x, y, foreColor, null);
    }

    /**
     * This method set a graphical message on the stream starting from position X, Y.
     * @param message is the graphical message.
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param foreColor is the color of the message's boarders.
     * @param backColorWord is the color of the internal background.
     * @param backColor is the color of the external background
     */
    public void setMessage(String message, int x, int y, ForeColor foreColor, BackColor backColorWord, BackColor backColor){
        int space = 0;
        for(int i = 0; i < message.length(); ++i){

            if(message.charAt(i) != ' '){
                List<String> charRows = GraphicalLetter.getLetter(message.charAt(i));

                int count = 0;
                int maxLenghtRow = 0;
                for(String row : charRows){
                    addString(x + space, y + count, row, foreColor, backColor);
                    if(row.length() > maxLenghtRow) maxLenghtRow = row.length();
                    count++;
                }
                List<String> lettCol = GraphicalLetter.getColorLetter(message.charAt(i));
                count = 0;
                for(String lett : lettCol){
                    for(int j = 0; j < lett.length(); ++j){
                        if(lett.charAt(j) == '1') addColor(x + space + j, y + count, foreColor, backColorWord);
                    }
                    ++count;
                }
                space += maxLenghtRow + 1;
            }
            else space += 3;

        }
    }

    /**
     * This method set a graphical message on the stream starting from position X, Y.
     * @param message is the graphical message.
     * @param x is the coordinate X.
     * @param y is the coordinate Y.
     * @param foreColor is the color of the message's boarders.
     * @param backColorWord is the color of the internal background.
     */
    public void setMessage(String message, int x, int y, ForeColor foreColor, BackColor backColorWord){
        setMessage(message,x, y, foreColor,  backColorWord, null);
    }


    /**
     * This method resets the content ot the stream and its colors.
     */
    public void reset(){
        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height; ++j){
                this.content[j][i] = '\0';
                this.colors[j][i] = BackColor.ANSI_BG_BLACK.getCode();
            }
        }
    }


    /**
     * This method prints the content ot the stream and its colors.
     */
    public void print(PrintStream stream){
        for(int row = 0;row < height; row++){
            String color = null;
            for(int col = 0;col < width; col++){
                if (colors[row][col] != null){
                    if (!colors[row][col].equals(color)){
                        color = colors[row][col];
                        stream.print(ANSI_RESET);
                        stream.print(color);
                    }
                }else{
                    color = null;
                    stream.print(ANSI_RESET);
                }
                if(content[row][col] != '\0') stream.print(content[row][col]);
                else stream.print(" ");
            }
            stream.print(ANSI_RESET);
            stream.println();
        }
        stream.println();
    }


}