package test;
import ixoroturg.json.*;

public class Test {
  public static void main(String[] args) {

    System.out.println((int)'\t'+ " "+(int)'\n'+" "+(int)'\f'+" "+(int)'\r' + " "+(int)'\b');

    IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX = 32;
    IJsonSetting.CHARACTERS_AFTER_ERROR_INDEX = 32;
    // IJsonSetting.DECODE_UNICODE_SEQUENCE = false;
    IJsonSetting.FORMAT_INDENT_COUNT = 2;
    IJsonSetting.FORMAT_INDENT_SYMBOL = ' ';
    try{
    FileIO.test();
    Numbers.test();
    BooleanAndNullTest.test();
    StringTest.test();
    } catch(TestException e){
      System.out.println("Test failed");
      System.out.println(e);
      e.printStackTrace();
    }
    System.out.println("\nTest complete successfully");
  }
}
