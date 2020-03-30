package it.polimi.ingsw.model.cardReader.enums;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatementTypeTest {
    /**
     * Test code not null
     */
    @Test
    void testCodeNotNull(){
        for(StatementType statementType : StatementType.values()){
            assertNotNull(statementType.getCode());
        }
    }
    /**
     * Test code duplicates
     */
    @Test
    void testCodeDuplicates(){
        List<String> codes = new ArrayList<>();
        for(StatementType statementType : StatementType.values()){
            if (codes.contains(statementType.getCode())){
                assert false;
            }else{
                codes.add(statementType.getCode());
            }
        }
    }

    /**
     * Test getter by value. Enum returned must have
     * the same code as requested
     */
    @Test
    void testValueByContent(){
        //Getting all codes
        List<String> codes = new ArrayList<>();
        for(StatementType statementType : StatementType.values()){
            codes.add(statementType.getCode());
        }
        //Test all codes
        for(String code : codes){
            StatementType output = StatementType.valueByContent(code);
            assertEquals(output.getCode(), code);
        }
    }

    /**
     * Test exception if code not present is provided to valueByContent
     */
    @Test
    void testValueByContentException(){
        try{
            StatementType output = StatementType.valueByContent("NOTPRESENT");
            assert false;
        }catch (IllegalArgumentException ex){
            assert true;
        }
    }
}