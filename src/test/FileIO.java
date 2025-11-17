package test;

import java.io.*;
import ixoroturg.json.*;

public class FileIO{
  public static void test() {
    System.out.println("Minimum work test:");
    try{
      Json js = IJson.of(new FileInputStream("./src/testFiles/simplyFile"));
      System.out.println("Inline test:\n"+js);
      System.out.println("\nFormat test:\n" + js.toStringFormat());

      js.parse(new FileReader("./src/testFiles/simplyFile2"));
      System.out.println("Inline test:\n"+js);
      System.out.println("\nFormat test:\n" + js.toStringFormat());

      IJsonSetting.DECODE_UNICODE_SEQUENCE = true;
      js.parse(new FileReader("./src/testFiles/unicode"));
      System.out.println("Inline test:\n"+js);
      System.out.println("\nFormat test:\n" + js.toStringFormat());

      IJsonSetting.DECODE_UNICODE_SEQUENCE = false;
      js.parse(new FileReader("./src/testFiles/unicode"));
      System.out.println("Inline test:\n"+js);
      System.out.println("\nFormat test:\n" + js.toStringFormat());
    } catch (Exception e){
      e.printStackTrace();
      TestException testExp = new TestException("Test failed");
      testExp.initCause(e);
      throw testExp;
    }
  }
}
