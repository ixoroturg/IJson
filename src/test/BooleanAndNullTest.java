package test;

import ixoroturg.json.*;

public class BooleanAndNullTest {
  public static void test(){

    System.out.println("\nBoolean and null test:\n");
    try{
      Json js = IJson.of("{\"someValue\": true}");
      System.out.println(js);
      js = IJson.of("{\"someValue\": false}");
      System.out.println(js);
      js = IJson.of("{\"someValue\": null}");
      System.out.println(js);
      
      
      try{
        js = IJson.of("{\"someValue\": nul}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На nul поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{\"someValue\": nu}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На nu поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{\"someValue\": nula}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На nula поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{\"someValue\": tru}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На tru поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{\"someValue\": fals}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На fals поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{\"someValue\": filse}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На filse поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.of("{\"someValue\": trea}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На trea поймано:\n" + e.getMessage());
      }
    }catch(Exception e){
      TestException t = new TestException("Boolean and null failed");
      t.initCause(e);
      throw t;
    }
  }
}
