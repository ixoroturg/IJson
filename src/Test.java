
import java.io.*;
import java.util.Arrays;

import ixoroturg.json.*;
public class Test {

	public static void main(String[] args) throws IOException {
    System.out.println("Hello");
		//{"station":[11336,787,10024,7861,10033,7889,6965,31],"data":[164,166,244,165]}
		
//		Json js = new IJson().parseHttpRequest("path={\"1C\":true}");
//		System.out.println(js);
//		System.exit(0);
		
//		Json js = new IJson() // Создаём
//			.put("name", "ixoroturg") // вставляем имя
//			.put("attributes", new IJson().put("age",22).put("height",173).put("addon", new IJson())) // создаём json с age и height и передаём его в attributes
//				.get("attributes")
//				.get("addon") // переходим в addon чтобы не делать это в прошлых скобках
//				.add(new int[] {1,2,3,4,5}) // добавляем массив, синтаксис - new int[] - объявление и {value1,value2} - значения
//				.add(new double[] {1.5,2.5}) // ещё массив
//				.add(new String[] {"Даже","так","можно"}) // ещё массив
//				.back() // назад в attributes
//				.put("more_mods",new IJson()) // добавляем новый объект
//					.get("more_mods")
//					.put("mode1","easy") // и свойства
//					.put("mode2","hard");
//		Json js = new IJson().parseHttpRequestForce("key1=[1,2,3]&key2={\"name\":\"ixoroturg\"}");
////		js.set
//		System.out.println(js);
		Json js = new IJson()
				.put("name", "ixoroturg") // вставляем имя
				.put("attributes", new IJson())
				.put("attributes/age",22)
				.put("attributes/height",173)
				.put("attributes/addon", new IJson())
				.add("attributes/addon",(byte)123)
				.add("attributes/addon",new double[] {1.5,2.5})
				.add("attributes/addon",new String[] {"Даже",null,"можно"})
				.put("attributes/more_mods", new IJson())
				.put("attributes/more_mods/mode1","easy")
				.put("attributes/more_mods/mode2","hard")
					.get("attributes/more_mods");
//				
//		System.out.println("1:\n"+js);
//		js = js.back(); // вернулись в attributes
////		System.out.println(js.get);
//		System.out.println("2:\n"+js);
//		js = js.back(); // вернулись к родителю
////		System.out.println("3:\n"+Arrays.toString(js.getStringArray("attributes/addon/8")));
		js.put("../../more",1)
		.put("../more2",2);
		System.out.println(js.get(".."));
		
		System.exit(0);
//		Json test1 = new IJson()
//						.put("1", new IJson().put("name2", "ixoroturg").put("name","kronos"))
//						.put("2", "test1")
//						.put("56", new IJson().add(new IJson().put("key", "1").put("value", new IJson().add(new IJson("1")).add(new IJson("true")))));
//		Json test2 = new IJson()
//				.put("1", new IJson().put("name", "ixoroturg").put("name","kronos"))
//				.put("2", "test1")
//				.put("56", new IJson()
//						.add(new IJson()
//								.put("key", "1")
//								.put("value", new IJson(JsonType.array)
//										.add(new IJson("true"))
//										.add(new IJson("1")
//												))));
//
//		System.out.println(test1);
//		System.out.println(test2);
//		int[] arr = new int[0];
		Json test = new IJson().readFrom(new FileInputStream("/home/ixoroturg/java/IServer/src/settings/start.conf"));
		System.out.println(Arrays.toString(test.getStringArray("api.files")));
		String[] A = null;
		test.clear();
		test.parseHttpRequestForce("key1=value1&key2=value2");
		test.putValue("key3","0e-1");
		test = test.get("key3");
		
//		test2.clear();
		String t2 = test.getPropertyName();
		try {
			String[] b = {null, "hello"};
			byte[] a = {1,2,3};
			System.out.println(new IJson().put("arr", (String)null));
			System.out.println(new IJson(0e-1));
//			System.out.println(test2.put("56.0.next",-5e+2));
//			System.out.println(test2.get("56.0.next").getPropertyName());
//			System.out.println(test2.put("56.0.value.0.next", new int[] {11,22,33,44,55}));
		}catch(JsonNoSuchPropertyException e) {
//			System.err.println(e.getJson());
			e.printStackTrace();
		}
		

	}

}
