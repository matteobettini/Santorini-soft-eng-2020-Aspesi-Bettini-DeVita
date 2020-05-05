package it.polimi.ingsw.CLI;

import java.util.ArrayList;
import java.util.List;

public class GraphicalLetter {
    public static List<String> getLetter(char letter){
        List<String> letterRows = new ArrayList<>();
        switch (letter){
            case 'A':
                letterRows.add("    _    ");
                letterRows.add("   / \\   ");
                letterRows.add("  / _ \\  ");
                letterRows.add(" / ___ \\ ");
                letterRows.add("/_/   \\_\\");
                break;
            case 'G':
                letterRows.add("  ____ ");
                letterRows.add(" / ___|");
                letterRows.add("| |  _ ");
                letterRows.add("| |_| |");
                letterRows.add(" \\____|");
                break;
            case 'M':
                letterRows.add(" __  __ ");
                letterRows.add("|  \\/  |");
                letterRows.add("| |\\/| |");
                letterRows.add("| |  | |");
                letterRows.add("|_|  |_|");
                break;
            case 'E':
                letterRows.add(" _____ ");
                letterRows.add("| ____|");
                letterRows.add("|  _|  ");
                letterRows.add("| |___ ");
                letterRows.add("|_____|");
                break;
            case 'O':
                letterRows.add("  ___");
                letterRows.add(" / _ \\ ");
                letterRows.add("| | | |");
                letterRows.add("| |_| |");
                letterRows.add(" \\___/ ");
                break;
            case 'V':
                letterRows.add("__     __");
                letterRows.add("\\ \\   / /");
                letterRows.add(" \\ \\ / / ");
                letterRows.add("  \\ V /  ");
                letterRows.add("   \\_/ ");
                break;
            case 'R':
                letterRows.add(" ____  ");
                letterRows.add("|  _ \\ ");
                letterRows.add("| |_) |");
                letterRows.add("|  _ < ");
                letterRows.add("|_| \\_\\");
                break;
            case '!':
                letterRows.add(" _ ");
                letterRows.add("| |");
                letterRows.add("| |");
                letterRows.add("|_|");
                letterRows.add("(_)");
                break;
            case 'Y':
                letterRows.add("__   __");
                letterRows.add("\\ \\ / /");
                letterRows.add(" \\ V / ");
                letterRows.add("  | |  ");
                letterRows.add("  |_| ");
                break;
            case 'U':
                letterRows.add(" _   _ ");
                letterRows.add("| | | |");
                letterRows.add("| | | |");
                letterRows.add("| |_| |");
                letterRows.add(" \\___/");
                break;
            case 'L':
                letterRows.add(" _     ");
                letterRows.add("| |    ");
                letterRows.add("| |    ");
                letterRows.add("| |___ ");
                letterRows.add("|_____|");
                break;
            case 'I':
                letterRows.add(" ___ ");
                letterRows.add("|_ _|");
                letterRows.add(" | | ");
                letterRows.add(" | | ");
                letterRows.add("|___|");
                break;
            case 'W':
                letterRows.add("__        __");
                letterRows.add("\\ \\      / /");
                letterRows.add(" \\ \\ /\\ / / ");
                letterRows.add("  \\ V  V /  ");
                letterRows.add("   \\_/\\_/");
                break;
            case 'N':
                letterRows.add(" _   _ ");
                letterRows.add("| \\ | |");
                letterRows.add("|  \\| |");
                letterRows.add("| |\\  |");
                letterRows.add("|_| \\_|");
                break;
            case 'S':
                letterRows.add(" ____  ");
                letterRows.add("/ ___| ");
                letterRows.add("\\___ \\ ");
                letterRows.add(" ___) |");
                letterRows.add("|____/ ");
                break;
            case 'T':
                letterRows.add(" _____ ");
                letterRows.add("|_   _|");
                letterRows.add("  | |  ");
                letterRows.add("  | |  ");
                letterRows.add("  |_|  ");
                break;
            case 'C':
                letterRows.add("  ____ ");
                letterRows.add(" / ___|");
                letterRows.add("| |    ");
                letterRows.add("| |___ ");
                letterRows.add(" \\____|");
                break;
            case 'D':
                letterRows.add(" ____  ");
                letterRows.add("|  _ \\ ");
                letterRows.add("| | | |");
                letterRows.add("| |_| |");
                letterRows.add("|____/");
                break;
            case 'H':
                letterRows.add(" _   _ ");
                letterRows.add("| | | |");
                letterRows.add("| |_| |");
                letterRows.add("|  _  |");
                letterRows.add("|_| |_|");
                break;

            default:
                letterRows.add("\0");
        }
        return letterRows;
    }

    public static List<String> getColorLetter(char letter){
        List<String> letterRows = new ArrayList<>();
        switch (letter){
            case 'A':
                letterRows.add("000000000");
                letterRows.add("000111000");
                letterRows.add("001111100");
                letterRows.add("011111110");
                letterRows.add("111000111");
                break;
            case 'G':
                letterRows.add("0000000");
                letterRows.add("0111111");
                letterRows.add("1110000");
                letterRows.add("1110011");
                letterRows.add("0111111");
                break;
            case 'M':
                letterRows.add("00000000");
                letterRows.add("11111111");
                letterRows.add("11111111");
                letterRows.add("11100111");
                letterRows.add("11100111");
                break;
            case 'E':
                letterRows.add("0000000");
                letterRows.add("1111111");
                letterRows.add("1111100");
                letterRows.add("1110000");
                letterRows.add("1111111");
                break;
            case 'O':
                letterRows.add("0000000");
                letterRows.add("0111110");
                letterRows.add("1110111");
                letterRows.add("1110111");
                letterRows.add("0111110");
                break;
            case 'V':
                letterRows.add("000000000");
                letterRows.add("111000111");
                letterRows.add("011101110");
                letterRows.add("001111100");
                letterRows.add("000111000");
                break;
            case 'R':
                letterRows.add("0000000");
                letterRows.add("1111110");
                letterRows.add("1110111");
                letterRows.add("1111110");
                letterRows.add("1110111");
                break;
            case '!':
                letterRows.add("000");
                letterRows.add("111");
                letterRows.add("111");
                letterRows.add("111");
                letterRows.add("111");
                break;
            case 'Y':
                letterRows.add("0000000");
                letterRows.add("1110111");
                letterRows.add("0111110");
                letterRows.add("0011100");
                letterRows.add("0011100");
                break;
            case 'U':
                letterRows.add("0000000");
                letterRows.add("1110111");
                letterRows.add("1110111");
                letterRows.add("1110111");
                letterRows.add("011111");
                break;
            case 'I':
                letterRows.add("00000");
                letterRows.add("11111");
                letterRows.add("01110");
                letterRows.add("01110");
                letterRows.add("11111");
                break;
            case 'W':
                letterRows.add("000000000000");
                letterRows.add("111000000111");
                letterRows.add("011101101110");
                letterRows.add("0011111111");
                letterRows.add("000111111");
                break;
            case 'N':
                letterRows.add("0000000");
                letterRows.add("1110111");
                letterRows.add("1111111");
                letterRows.add("1111111");
                letterRows.add("1110111");
                break;
            case 'S':
                letterRows.add("0000000");
                letterRows.add("1111110");
                letterRows.add("1111110");
                letterRows.add("0000111");
                letterRows.add("1111110");
                break;
            case 'T':
                letterRows.add("0000000");
                letterRows.add("1111111");
                letterRows.add("0011100");
                letterRows.add("0011100");
                letterRows.add("0011100");
                break;
            case 'C':
                letterRows.add("00000000");
                letterRows.add("0111111");
                letterRows.add("11100000");
                letterRows.add("11100000");
                letterRows.add("0111111");
                break;
            case 'D':
                letterRows.add("00000000");
                letterRows.add("1111110");
                letterRows.add("1110111");
                letterRows.add("1110111");
                letterRows.add("1111110");
                break;
            case 'H':
                letterRows.add("0000000");
                letterRows.add("1110111");
                letterRows.add("1110111");
                letterRows.add("1111111");
                letterRows.add("1110111");
                break;
            default:
                letterRows.add("\0");
        }
        return letterRows;
    }
}