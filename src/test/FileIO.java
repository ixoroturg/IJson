package test;

import java.io.*;
import ixoroturg.json.*;

public class FileIO{
  public static void test(){
    try{
      Json js = IJson.of(new FileInputStream("./src/test/files/simplyFile"));
      System.out.println("Inline test:\n"+js);
      System.out.println("\nFormat test:\n" + js.toStringFormat());

      js.parse(new FileReader("./src/test/files/simplyFile2"));
      System.out.println("Inline test:\n"+js);
      System.out.println("\nFormat test:\n" + js.toStringFormat());

      IJsonSetting.DECODE_UNICODE_SEQUENCE = true;
      js.parse(new FileReader("./src/test/files/unicode"));
      System.out.println("Inline test:\n"+js);
      System.out.println("\nFormat test:\n" + js.toStringFormat());

      IJsonSetting.DECODE_UNICODE_SEQUENCE = false;
      js.parse(new FileReader("./src/test/files/unicode"));
      System.out.println("Inline test:\n"+js);
      System.out.println("\nFormat test:\n" + js.toStringFormat());
    } catch (Exception e){
      e.printStackTrace();
    }
  }
}
