package test;

import java.io.*;
import java.util.Arrays;

import IJson.*;
public class main {

	public static void main(String[] args) throws IOException {
		//{"station":[11336,787,10024,7861,10033,7889,6965,31],"data":[164,166,244,165]}

//		Json test1 = new IJson()
//						.put("1", new IJson().put("name2", "ixoroturg").put("name","kronos"))
//						.put("2", "test1")
//						.put("56", new IJson().add(new IJson().put("key", "1").put("value", new IJson().add(new IJson("1")).add(new IJson("true")))));
		Json test2 = new IJson()
				.put("1", new IJson().put("name", "ixoroturg").put("name","kronos"))
				.put("2", "test1")
				.put("56", new IJson()
						.add(new IJson()
								.put("key", "1")
								.put("value", new IJson(JsonType.array)
										.add(new IJson("true"))
										.add(new IJson("1")
												))));
//
//		System.out.println(test1);
//		System.out.println(test2);
		int[] arr = new int[0];
		Json test = new IJson(arr);
//		
//		var c = test.getDoubleStream().map(Math::sqrt).peek(System.out::println).reduce(0, (d1,d2) -> {return (d1+d2)/2;});
//		System.out.println(c);
//		System.out.println(test2);
//		System.out.println("ответ");
		try {
//			int check = test2.getInt("56/0/key");
//			System.out.println(check);
//			Json res = test2.get("56.0.value.0");
			Json[] arr2 = new Json[]{null, null,null,new IJson().put("null", "not null"), null };
//			arr2[3] = new IJson().put("null", "not null");
//			System.out.println(test2.get("56.0").put("lol","ass"));
			System.out.println(test2.put("56.0.lol", new String[] {"A","B","C"}));
			System.out.println(test2.add("56.0.lol", "D"));
			System.out.println(test2.add("56.0.value", arr2));
//			System.out.println(test2.get("56.0.value.5"));
			System.out.println(test2.put("56.0.value.5.next", new int[] {11,22,33,44,55}));
		}catch(JsonNoSuchPropertyException e) {
			System.err.println(e.getJson());
		}
		

	}

}
