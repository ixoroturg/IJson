package test;

import ixoroturg.json.*;
import java.io.FileInputStream;
import java.io.FileReader;

public class StringTest {
  public static void test(){
    System.out.println("\nString test:\n");
    try{
      IJsonSetting.ESCAPE_CONTROL_CHARACTERS = false;
      IJsonSetting.DECODE_UNICODE_SEQUENCE = false;
      Json js = IJson.of(new FileReader("./src/testFiles/StringTest"));
      System.out.println("Without escape:\n");
      System.out.println("Inline:\n");
      System.out.println(js);
      System.out.println("\nFormat:\n");
      System.out.println(js.toStringFormat());

      IJsonSetting.ESCAPE_CONTROL_CHARACTERS = true;
      IJsonSetting.DECODE_UNICODE_SEQUENCE = true;
      System.out.println("\nWith escape:\n");
      js = IJson.of(new FileInputStream("./src/testFiles/StringTest"));
      System.out.println("Inline:\n");
      System.out.println(js);
      System.out.println("\nFormat:\n");
      System.out.println(js.toStringFormat());

      js = IJson.of("{\"control_chars\": \"formfeed: \\u000c, newLine: \\u000a, Tab: \\u0009, CR: \\u000D\"}");
      System.out.println("Управляющие последовательности:\n");
      System.out.println(js.toStringFormat());
  
    } catch(Exception e){
      TestException t = new TestException("String test failed");
      t.initCause(e);
      throw t;
    }
  }
}
