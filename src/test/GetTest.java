package test;

//import ixoroturg.json.IJson;
import ixoroturg.json.*;

public class GetTest {
  public static void test(){
    IJsonSetting.KEY_DELIMETER = '.';
    IJsonSetting.PARENT_CHARACTER = 'p';
    IJsonSetting.USE_ARRAY_SYNTAX = true;
    IJsonSetting.SHOW_INNER_DOUBLE_VALUE = true;
    
    String str = """
    		{
    			"Number1": 12345,
    			"Number2": 3321.4542,
    			"Number3": 32.432e10,
    			"obj": {
    				"title": "some title",
    				"pass": true,
    				"access" : false,
    				"rules": null,
    				"array": [
    					12,
    					432,
    					"third element",
    					{
    						"author": "ixoroturg",
    						"date": 132532527,
    						"arr2": [
    							"one",
    							"two",
    							"three"
    						]
    					}
    				],
    				"array2": [
    					1,2,3,4,5
    				]
    			}
    		}
    		""";
    String[] keys = {	
    		"Number1", "Number2", "Number3", "obj", "obj.title", "obj.pass", "obj.access", "obj.rules",
    		"obj.array", "obj.array[0]", "obj.array[1]", "obj.array[2]", "obj.array[3]",
    		"obj.array[3].author", "obj.array[3].date", "obj.array[3].arr2", "obj.array[3].arr2[0]",
    		"obj.array[3].arr2[1]", "obj.array[3].arr2[2]", "obj.array2", "obj.array2[0]", "obj.array2[1]",
    		"obj.array2[2]", "obj.array2[3]", "obj.array2[4]", "obj.title.\\p.array[3].\\p.[2]"
    	};
    int i = 0;
    try {
    	Json js = IJson.of(str);
    	System.out.println("\nGet test:\n" + js.toStringFormat());
    	
    	
    	for(i = 0; i < keys.length; i++) {
    		if(i == 7) {
    			try {
    				js = js.get(keys[i]);
    			}catch (JsonNoSuchPropertyException e) {
    				js = js.back(0);
    				System.out.println("\nnull значения обрабатываются как несуществующие, потому что иначе вы бы могли сделать has(propertyName), потом get(propertyName).someMethod() и получить NullPointerException");
    				System.out.println("На rules, который null, поймано:\n"+e.getMessage()+"\n");
    			}
    			js = js.back(0);
    			continue;
    		}
    		js = js.get(keys[i]);
    		System.out.println(keys[i]+" = "+js.toStringFormat());
    		js = js.back(0);
    	}
    	System.out.println("\nПолучение элементов массива по номеру (obj.array):");
    	js = js.get("obj.array");
    	for(i = 0; i < js.size(); i++) {
    		js = js.get(i);
    		System.out.println(i +" = " + js.toStringFormat());
    		js = js.back();
    	}
    	js = js.get(3).get("author");
    	System.out.println("\nТест прохода по родителям:\nТекущий json: "+js.toStringFormat());
    	for(i = 0; i < 3; i++) {
    		System.out.println(i+ ": Поднялись до: "+ js.back().toStringFormat());
    	}
    	System.out.println("Теперь в title: "+js.go("title").toStringFormat());
    	System.out.println("\nТеперь через IJsonSetting.PARENT_CHARACTER = 'p':");
    	String test1 = "\\p.array[3].arr2[2]";
      System.out.println("Сейчас: "+ js.toStringFormat());
    	System.out.println(test1 + " = " + js.go(test1).toStringFormat());
    	test1 = "\\p.\\p.\\p.[2]";
    	System.out.println(test1 + " = " + js.get(test1).toStringFormat());
    	
    	js.back(0);
    	String[] invalid = {
    		"not exists key", "obj.array[number poor]", "obj.array[132.456]", "obj.array[-1]", "obj.array[23e]",
    		"obj.array[52e-2", "obj.array[-3e0.5]", "obj.title.\\pl", "obj.title.\\p.array[2].\\p[2]"
    	};
    	System.out.println("\nНевалидный get с самого верха от js.back(0):");
    	for(i = 0; i < invalid.length; i++) {
    		try {
    			js.get(invalid[i]);
    		}catch(Exception e) {
    			System.out.println("На "+invalid[i]+" получено:\n"+e.getMessage());    		}
    	}
    	System.out.println("\njson не изменился: "+js);
    	try {
    		js.back();
    	}catch(JsonNoParentException e) {
    		System.out.println("\nНет родителя: "+e.getMessage());
    	}
    	try {
    		js.back(123);
    	}catch(JsonNoParentException e) {
    		System.out.println("Нет родителя 2: "+e.getMessage());
    	}
    	js.back(0);
    	
    }catch(Exception e) {
    	System.out.println("Ошибка на: "+i+" = "+keys[i]);
    	e.printStackTrace();
      TestException t = new TestException("Ошибка в GetTest");
      t.initCause(e);
      throw t;
    }
    
  }
}
