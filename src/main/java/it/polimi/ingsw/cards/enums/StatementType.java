package it.polimi.ingsw.cards.enums;

/**
 * Represents the possible types of statement
 * of a card.
 * Contains methods to easily get enum value from xml string value
 */
public enum StatementType {
    IF("if"),
    NIF("nif");

    private final String code;
    StatementType(String code){
        this.code = code;
    }

    /**
     * Getter for the current enum value's code
     * @return The code associated with this value
     */
    public String getCode() {
        return code;
    }

    /**
     * Helper function to reproduce Enum.valueOf using enum value's codes
     * instead of enum names
     * @param code The code of the enum value
     * @return The enum value corresponding to the code
     * @throws IllegalArgumentException If there is no enum value with that code
     */
    public static StatementType valueByContent(String code){
        for(StatementType e : StatementType.values()){
            if(e.code.equals(code))
                return e;
        }
        throw new IllegalArgumentException();
    }
}
