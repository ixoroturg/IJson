package ixoroturg.json;

import java.io.*;
import java.io.StringReader;

import ixoroturg.json.*;
// import com.fasterxml.jackson.*;
import com.fasterxml.jackson.databind.*;
//import org.json4s.jackson.Json;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class BigFileTest {
	// @Test
  public void test() throws Exception{
    // FileInputStream reader = new FileInputStream("./src/testFiles/BigFile");
    // String test2 = new String(reader.readAllBytes());
    // String test = "[";
    // for(int i = 0; i < 1000; i++){
    //   test+=test2+",";
    // }
    // test = test.substring(0,test.length()-1);
    // test += "]";
	  
	Json js;
	JsonNode jackFire;
    ObjectMapper mapper = new ObjectMapper();
	int fire = 100;
    var in = new FileInputStream("./src/testFiles/canada.json");
	for(int i = 0; i < fire; i++){
		in = new FileInputStream("./src/testFiles/canada.json");
		js = Json.of(in);
		in.close();
		in = new FileInputStream("./src/testFiles/canada.json");
		jackFire = mapper.readTree(in);
	}
	in.close();
    // String canada = new String(in.readAllBytes());
    // in.close();
//  Json js = Json.of(canada);
//  System.out.println("canada.json parse time: "+js.getParseTime());
    
    in = new FileInputStream("./src/testFiles/twitter.json");
	for(int i = 0; i < fire; i++){
		in = new FileInputStream("./src/testFiles/twitter.json");
		js = Json.of(in);
		in.close();
		in = new FileInputStream("./src/testFiles/twitter.json");
		jackFire = mapper.readTree(in);
	}
	in.close();
    // String twitter = new String(in.readAllBytes());
    // in.close();
    
    
    in = new FileInputStream("./src/testFiles/citm_catalog.json");
	for(int i = 0; i < fire; i++){
		in = new FileInputStream("./src/testFiles/citm_catalog.json");
		js = Json.of(in);
		in.close();
		in = new FileInputStream("./src/testFiles/citm_catalog.json");
		jackFire = mapper.readTree(in);
	}
	in.close();
    // String citm = new String(in.readAllBytes());
    // in.close();

	// for(int i = 0; i < fire; i++){
	// 	js = Json.of(in);
	// 	jackFire = mapper.readTree(in);
	// }
	


	
   // js = Json.of(twitter);
   // js.toStringFormat();
   // if(true){
   //  return;
   // }
   // System.out.println(js.toStringFormat());
//    System.out.println("citm_catalog.json parse time: "+js.getParseTime());
    
//    System.out.println("\n\nJackson:\n");
//    long start = System.currentTimeMillis();
//    Object result = mapper.readValue(twitter, Object.class);
//    long end = System.currentTimeMillis() - start;
//    System.out.println("jackson: "+end);
    
//     ObjectMapper mapper = new ObjectMapper();
//
//  // Прогрев
	System.out.println("Прогрев завершён");
    // String test = canada;
    long my = 0;
    long my2 = 0;
    long my3 = 0;
    long my4 = 0;
    long jack = 0;
    long jack2 = 0;
    int count = 1000;
    long start = 0;
    long fullTest = 0;
    String[] files = {"canada","twitter","citm_catalog"};

    System.out.println("0 = canada, 1 = twitter, 2 = citm_catalog");

    fullTest = System.currentTimeMillis();
    for(int j = 0; j < files.length; j++){

        String path = "./src/testFiles/";
        switch(j){
          case 0 -> {path += "canada";}
          case 1 -> {path += "twitter";}
          case 2 -> {path += "citm_catalog";}
        }
        path += ".json";


      for(int i = 0; i < count; i++){
        in = new FileInputStream(path);
        start = System.currentTimeMillis();
        JsonNode result = mapper.readTree(in);
        jack += System.currentTimeMillis() - start;
        in.close();
      }
      // JsonSetting.BUFFER_SIZE = 4096;
      for(int i = 0; i < count; i++){
        in = new FileInputStream(path);
        start = System.currentTimeMillis();
        js = Json.of(in);
        my += System.currentTimeMillis() - start;
        in.close();
      }
      
      // JsonSetting.BUFFER_SIZE = 8192;
      for(int i = 0; i < count; i++){
        in = new FileInputStream(path);
        start = System.currentTimeMillis();
        js = Json.of(in);
        my2 += System.currentTimeMillis() - start;
        in.close();
      }  

      // JsonSetting.BUFFER_SIZE = 16384;
      for(int i = 0; i < count; i++){
        in = new FileInputStream(path);
        start = System.currentTimeMillis();
        js = Json.of(in);
        my3 += System.currentTimeMillis() - start;
        in.close();
      }

      // JsonSetting.BUFFER_SIZE = 32768;
      for(int i = 0; i < count; i++){
        in = new FileInputStream(path);
        start = System.currentTimeMillis();
        js = Json.of(in);
        my4 += System.currentTimeMillis() - start;
        in.close();
      }
      for(int i = 0; i < count; i++){
        in = new FileInputStream(path);
        start = System.currentTimeMillis();
        JsonNode result = mapper.readTree(in);
        jack2 += System.currentTimeMillis() - start;
        in.close();
      }
      System.out.println("\n"+j+" parse time:");
      System.out.println("jackson: "+jack/count);
      System.out.println("Json with 4KiB buffer: "+my/count);
      System.out.println("Json with 8KiB buffer: "+my2/count);
      System.out.println("Json with 16KiB buffer: "+my3/count);
      System.out.println("Json with 32KiB buffer: "+my4/count);
      System.out.println("jackson 2: "+jack2/count);
    }
    fullTest = System.currentTimeMillis() - fullTest;
    System.out.println("\nWhole test time: "+fullTest);
    
    
//    System.out.println(result);
//    System.out.println(js.toStringFormat());
  }
}
