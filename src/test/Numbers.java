package test;

import ixoroturg.json.*;

public class Numbers {
  public static void test(){
    System.out.println("\nNumbers test:");
    try {
      IJson js;

      IJsonSetting.SHOW_INNER_DOUBLE_VALUE = true;
      System.out.println("\nНастоящее значение:\n");
      innerTest();
      IJsonSetting.SHOW_INNER_DOUBLE_VALUE = false;
      System.out.println("\nСохранённое значение:\n");
      innerTest();

      try{
        js = IJson.of("{ \"Number\": -}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа - поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": }");
        System.out.println(js);
      } catch(JsonParseException e){
        System.out.println("\nНа пустоту поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": 012}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа 012 поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": 1e}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа 1e поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": 3e+}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа 3e+ поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": 5e-}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа 5e- поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": e-1}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа e-1 поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": e}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа e поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": e+1}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа e+1 поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": e1}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа e1 поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": -02}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа -02 поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": -0.321e}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа -0.321e поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": .}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа . поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": .5}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа .5 поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": 1.}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа 1. поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": 1.1.1}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа 1.1.1 поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": 5-}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа 5- поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": 5+}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа 5+ поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{ \"Number\": 5e.1}");
        System.out.println(js);
      } catch(JsonInvalidNumberException e){
        System.out.println("\nНа 5+ поймано:\n" + e.getMessage());
      }

    } catch (Exception e) {
      TestException t = new TestException("Numbers failed");
      t.initCause(e);
      throw t;
    }
  }

  private static void innerTest() throws Exception{
      Json js = IJson.of("{ \"Number\": 12345}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 0}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 1}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 0e10}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 1e10}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 0e+5}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 0e-5}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 1e+5}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 1e-5}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 1.3e+5}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 0.5}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 2.0}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 10E10}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 1.0e+0}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": -0.0}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 1.7976931348623157e+308}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 4.9e-324}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": -0e+5}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": -0}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": -0.1}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": -0.3e+5}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": -0.321}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": -123.45e-78}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 2147483647}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": -2147483648}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 12345678901234567890}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 0.000000000000000001}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 1e+308}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 1e-308}");
      System.out.println(js);
      js = IJson.of("{ \"Number\": 0e0}");
  }
}
