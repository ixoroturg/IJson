package ixoroturg.json;

import java.io.*;

import ixoroturg.json.*;
// import com.fasterxml.jackson.*;
import com.fasterxml.jackson.databind.*;
//import org.json4s.jackson.Json;

public class BigFileTest {
  public static void test() throws Exception{



		//  Json res = IJson.of(new FileInputStream("./src/testFiles/twitter.json"));
		// System.out.println("Ответ от IJson: "+res);
		// if(true)
		// 	return;
		// System.exit(0);


	  
    // FileInputStream reader = new FileInputStream("./src/testFiles/BigFile");
    // String test2 = new String(reader.readAllBytes());
    // String test = "[";
    // for(int i = 0; i < 1000; i++){
    //   test+=test2+",";
    // }
    // test = test.substring(0,test.length()-1);
    // test += "]";
	  
	IJson js;
    var in = new FileInputStream("./src/testFiles/canada.json");
    String canada = new String(in.readAllBytes());
    in.close();
//  IJson js = IJson.of(canada);
//  System.out.println("canada.json parse time: "+js.getParseTime());
    
    in = new FileInputStream("./src/testFiles/twitter.json");
    String twitter = new String(in.readAllBytes());
    in.close();
    
    
    in = new FileInputStream("./src/testFiles/citm_catalog.json");
    String citm = new String(in.readAllBytes());
    in.close();
//    js = IJson.of(citm);
//    System.out.println("citm_catalog.json parse time: "+js.getParseTime());
    
//    System.out.println("\n\nJackson:\n");
//    long start = System.currentTimeMillis();
//    Object result = mapper.readValue(twitter, Object.class);
//    long end = System.currentTimeMillis() - start;
//    System.out.println("jackson: "+end);
    
//     ObjectMapper mapper = new ObjectMapper();
//
//  // Прогрев

    String test = canada;
    long my = 0;
    long my2 = 0;
    long my3 = 0;
    long my4 = 0;
    long jack = 0;
    long jack2 = 0;
    int count = 1000;
    long start = 0;
    long fullTest = 0;
    String[] files = {canada, twitter, citm};

    ObjectMapper mapper = new ObjectMapper();
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
      IJsonSetting.BUFFER_SIZE = 4096;
      for(int i = 0; i < count; i++){
        in = new FileInputStream(path);
        start = System.currentTimeMillis();
        js = IJson.of(in);
        my += System.currentTimeMillis() - start;
        in.close();
      }
      
      IJsonSetting.BUFFER_SIZE = 8192;
      for(int i = 0; i < count; i++){
        in = new FileInputStream(path);
        start = System.currentTimeMillis();
        js = IJson.of(in);
        my2 += System.currentTimeMillis() - start;
        in.close();
      }  

      IJsonSetting.BUFFER_SIZE = 16384;
      for(int i = 0; i < count; i++){
        in = new FileInputStream(path);
        start = System.currentTimeMillis();
        js = IJson.of(in);
        my3 += System.currentTimeMillis() - start;
        in.close();
      }

      IJsonSetting.BUFFER_SIZE = 32768;
      for(int i = 0; i < count; i++){
        in = new FileInputStream(path);
        start = System.currentTimeMillis();
        js = IJson.of(in);
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

		
		// System.out.println("Ответ от IJson: "+IJson.of(new FileInputStream("./src/testFiles/twitter.json")));
	  
      System.out.println("\n"+j+" parse time:");
      System.out.println("jackson: "+jack/count);
      System.out.println("IJson with 1KiB buffer: "+my/count);
      System.out.println("IJson with 32KiB buffer: "+my2/count);
      System.out.println("IJson with 64KiB buffer: "+my3/count);
      System.out.println("IJson with 16MiB buffer: "+my4/count);
      System.out.println("jackson 2: "+jack2/count);
    }
    fullTest = System.currentTimeMillis() - fullTest;
    System.out.println("\nWhole test time: "+fullTest);

	// for(int i = 0; i < 10; i++){
	// 	 // System.out.println("TEST:");
	// 	 Json res = IJson.of(new FileInputStream("./src/testFiles/twitter.json"));
	// 	 // System.out.println("END:");
	// 	 String str = null;
	// 	 try{
	// 		str = res.toString();
	// 	}catch(Exception e){
	// 		System.err.println("BigTest ERROR:\n"+e.getMessage());
	// 		// e.printStackTrace();
	// 	}
	// }
    
//    System.out.println(result);
//    System.out.println(js.toStringFormat());
  }
}
