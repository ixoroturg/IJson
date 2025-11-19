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
      
      System.out.println("Total parse time: "+js.getParseTime());

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
      
      IJsonSetting.ESCAPE_CONTROL_CHARACTERS = false;
      System.out.println("\nУправляющие последовательности недопустимы:\n");
      testControl();

      IJsonSetting.ESCAPE_CONTROL_CHARACTERS = true;
      System.out.println("\nУправляющие последовательности допустимы:\n");
      testControl();

      IJsonSetting.FORMAT_DIRECT_WRITE_CONTROL_CHARACTER = false;
      js = IJson.of("{\"control_chars\": \"formfeed: \\u000c, newLine: \\u000a, Tab: \\u0009, CR: \\u000D\", \"another\": \"one tow three four five\"}");
      System.out.println("\nЭкранированные символы: "+js.toStringFormat());

      IJsonSetting.FORMAT_DIRECT_WRITE_CONTROL_CHARACTER = true;
      js = IJson.of("{\"control_chars\": \"formfeed: \\u000c, newLine: \\u000a, Tab: \\u0009, CR: \\u000D\", \"another\": \"one tow three four five\"}");
      System.out.println("\nНеэкранированные символы: "+js.toStringFormat());

      IJsonSetting.NULL_STRING_AS_NULL_VALUE = false;
      js = IJson.of("{\"Null string\": \"null\"}");
      System.out.println("\nNull string: "+js.toStringFormat());

      IJsonSetting.NULL_STRING_AS_NULL_VALUE = true;
      js = IJson.of("{\"Null string\": null}");
      System.out.println("\nNull value: "+js.toStringFormat());

      try{
        js = IJson.of("{\"Forbidden\": \"\\\\\\ \"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("\nНа \\\\\\ выдало:\n"+e.getMessage());
      }
      try{
        js = IJson.of("{\"Forbidden\": \"\\\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На \\ выдало:\n"+e.getMessage());
      }
      try{
        js = IJson.of("{\"Forbidden\": \"\\q\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На \\q выдало:\n"+e.getMessage());
      }
      try{
        js = IJson.of("{\"Forbidden\": \"\\p\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На \\p выдало:\n"+e.getMessage());
      }
      try{
        js = IJson.of("{\"Forbidden\": \"\\ something\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На \\t выдало:\n"+e.getMessage());
      }
      try{
        js = IJson.of("{\"Forbidden\": \"\\\" somememem \\\" \\\"\\\"\\\" \\\\\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На много херни выдало:\n"+e.getMessage());
      }
      try{
        js = IJson.of("{\"Forbidden\":\n\n\n\n\n \"\\\" somememem \" \"\"\" \\\\\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На некорректную херню выдало:\n"+e.getMessage());
      }

    } catch(Exception e){
      TestException t = new TestException("String test failed");
      t.initCause(e);
      throw t;
    }
  }
  private static void testControl(){
      Json js;
      try{
        js = IJson.of("{\"Forbidden\": \"\t\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На \\t выдало:\n"+e.getMessage());
      }
      try{
        js = IJson.of("{\"Forbidden\": \"\n\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На \\n выдало:\n"+e.getMessage());
      }
      try{
        js = IJson.of("{\"Forbidden\": \"\r\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На \\r выдало:\n"+e.getMessage());
      }
      try{
        js = IJson.of("{\"Forbidden\": \"\f\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На \\f выдало:\n"+e.getMessage());
      }
      try{
        js = IJson.of("{\"Forbidden\": \"\b\"}");
        System.out.println(js);
      }catch (Exception e){
        System.out.println("На \\b выдало:\n"+e.getMessage());
      }

  }
}
