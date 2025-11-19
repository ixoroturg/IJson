package test;
import ixoroturg.json.*;

public class Test {
  public static void main(String[] args) throws Exception {

//    System.out.println((int)'\t'+ " "+(int)'\n'+" "+(int)'\f'+" "+(int)'\r' + " "+(int)'\b');

    IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX = 16;
    IJsonSetting.CHARACTERS_AFTER_ERROR_INDEX = 16;
     IJsonSetting.DECODE_UNICODE_SEQUENCE = false;
    IJsonSetting.FORMAT_INDENT_COUNT = 2;
    IJsonSetting.FORMAT_INDENT_SYMBOL = ' ';
    IJsonSetting.USE_FAST_NUMBER_PARSE = true;
//    IJson.Setting.De/
    // KeyCloakApiTest.test();
    // System.exit(0);
    try{
      FileIO.test();
      Numbers.test();
      BooleanAndNullTest.test();
      StringTest.test();
      BigFileTest.test();
    } catch(TestException e){
      System.out.println("Test failed");
      System.out.println(e);
      e.printStackTrace();
    }
    System.out.println("\nTest complete successfully");
  }
}
