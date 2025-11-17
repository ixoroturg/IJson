package test;

import java.io.FileInputStream;
import ixoroturg.json.*;

public class BigFileTest {
  public static void test() throws Exception{
    // FileInputStream reader = new FileInputStream("./src/testFiles/BigFile");
    // String test2 = new String(reader.readAllBytes());
    // String test = "[";
    // for(int i = 0; i < 1000; i++){
    //   test+=test2+",";
    // }
    // test = test.substring(0,test.length()-1);
    // test += "]";
    

    String test = new String(new FileInputStream("./src/testFiles/canada.json").readAllBytes());
    IJson js = IJson.of(test);
    System.out.println("canada.json parse time: "+js.getParseTime());

    test = new String(new FileInputStream("./src/testFiles/twitter.json").readAllBytes());
    js = IJson.of(test);
    System.out.println("twitter.json parse time: "+js.getParseTime());

    test = new String(new FileInputStream("./src/testFiles/citm_catalog.json").readAllBytes());
    js = IJson.of(test);
    System.out.println("citm_catalog.json parse time: "+js.getParseTime());
    // System.out.println("\n\nVery big file:\n"+test);
    // Json js = IJson.of(test);
    // System.out.println("\n\nBig file (61 000 strings) parse time: "+js.getParseTime());
    // System.out.println("\nJson:\n"+js.toStringFormat());
  }
}
