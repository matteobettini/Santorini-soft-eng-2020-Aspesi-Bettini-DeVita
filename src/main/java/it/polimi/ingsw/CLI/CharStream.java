package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

import java.io.PrintStream;
import java.util.List;

public class CharStream {

    private final int height;
    private final int width;
    private final char[][] content;
    private final String[][]colors;

    private static final String ANSI_RESET  = "\u001B[0m";

    public CharStream(int width, int height){
        this.height = height;
        this.width = width;
        this.content = new char[height][width];
        this.colors = new String[height][width];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public char getChar(int x, int y){
        if (x < 0 || x >= width || y < 0 || y >= height) return 0;
        return content[y][x];
    }

    public void addChar(char content,int x, int y, ForeColor foreColor, BackColor backColor){
        if(x < 0 || x >= width || y < 0 || y >= height) return;
        this.content[y][x] = content;
        addColor(x, y , foreColor, backColor);
    }

    public boolean addString(int x, int y, String str, ForeColor foreColor, BackColor backColor){
        if (x < 0 || x + str.length() >= width || y < 0 || y  >= height) return false;
        for(int x1 = 0 ; x1 <str.length(); x1++){
            content[y][x + x1] = str.charAt(x1);
            if (foreColor == null && backColor == null) colors[y][x + x1] = null;
            else colors[y][x + x1] = (foreColor != null ? foreColor.getCode() : "") + (backColor!= null ? backColor.getCode() : "");
        }
        return true;
    }
    public boolean addString(int x, int y, String str){
        return addString(x,y,str,null,null);
    }


    public void addColor(int x, int y, ForeColor foreColor, BackColor backColor){
        if (x<0 || x >= width || y<0 || y>=height) return;
        if (foreColor == null && backColor == null) colors[y][x] = null;
        else colors[y][x] = (foreColor != null ? foreColor.getCode() : "") + (backColor!= null ? backColor.getCode() : "");
    }

    public void setMessage(String message, int x, int y, ForeColor foreColor, BackColor backColorWord, BackColor backColor){
        int space = 0;
        for(int i = 0; i < message.length(); ++i){
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
    }


    public void reset(){
        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height; ++j){
                this.content[j][i] = '\0';
                this.colors[j][i] = BackColor.ANSI_BG_BLACK.getCode();
            }
        }
    }

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