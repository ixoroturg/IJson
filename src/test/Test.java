package test;
import ixoroturg.json.*;

public class Test {
  public static void main(String[] args) {
    IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX = 64;
    IJsonSetting.CHARACTERS_AFTER_ERROR_INDEX = 64;
    IJsonSetting.DECODE_UNICODE_SEQUENCE = false;
    IJsonSetting.FORMAT_INDENT_COUNT = 2;
    IJsonSetting.FORMAT_INDENT_SYMBOL = ' ';
    FileIO.test();

    System.out.println("Test complete successfully");
  }
}
