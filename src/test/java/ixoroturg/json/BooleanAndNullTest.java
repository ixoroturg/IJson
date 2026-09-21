package ixoroturg.json;

import ixoroturg.json.*;

public class BooleanAndNullTest {
  public static void test(){

    System.out.println("\nBoolean and null test:\n");
    try{
      Json js = IJson.ofInner("{\"someValue\": true}");
      System.out.println(js);
      js = IJson.ofInner("{\"someValue\": false}");
      System.out.println(js);
      js = IJson.ofInner("{\"someValue\": null}");
      System.out.println(js);
      
      
      try{
        js = IJson.ofInner("{\"someValue\": nul}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На nul поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.ofInner("{\"someValue\": nu}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На nu поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.ofInner("{\"someValue\": nula}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На nula поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.ofInner("{\"someValue\": tru}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На tru поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.ofInner("{\"someValue\": fals}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На fals поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.ofInner("{\"someValue\": filse}");
        System.out.println(js);
      }catch( Exception e){
        System.out.println("На filse поймано:\n" + e.getMessage());
      }
      try{
        js = IJson.ofInner("{\"someValue\": trea}");
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
