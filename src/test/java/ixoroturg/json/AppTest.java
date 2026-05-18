package ixoroturg.json;

import static org.junit.jupiter.api.Assertions.assertTrue;
import ixoroturg.json.*;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
//    System.out.println((int)'\t'+ " "+(int)'\n'+" "+(int)'\f'+" "+(int)'\r' + " "+(int)'\b');

    IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX = 16;
    IJsonSetting.CHARACTERS_AFTER_ERROR_INDEX = 16;
     IJsonSetting.DECODE_UNICODE_SEQUENCE = false;
    IJsonSetting.FORMAT_INDENT_COUNT = 2;
    IJsonSetting.FORMAT_INDENT_SYMBOL = ' ';
    // IJsonSetting.USE_FAST_NUMBER_PARSE = true;
//    IJson.Setting.De/
    // KeyCloakApiTest.test();
    // System.exit(0);
    // Json js = IJson.ofObject();
    // js.put("suka","lol");
    // js.writeTo(System.out);
    try{
      // FileIO.test();
      // Numbers.test();
      // BooleanAndNullTest.test();
      // StringTest.test();
      // GetTest.test();
      // PutAddGetTest.test();
      // BigFileTest.test();
      // Json js = IJson.of("lol");
//      js.getInnerRepresentation().
      System.out.println("\nTest complete successfully");
    } catch(Exception e){
      System.out.println("\nTest failed");
      // System.out.println(e);
      e.printStackTrace();
    }
    }
}

