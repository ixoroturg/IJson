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
//		int[] arr = new int[0];
		Json test = new IJson();
		String[] A = null;
		test.clear();
		test.parseHttpRequestForce("key1=value1&key2=value2");
		test.putValue("key3","0e-1");
		test = test.get("key3");
		test2.clear();
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
