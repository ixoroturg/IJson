package test;

import java.io.*;

import IJson.*;
public class main {

	public static void main(String[] args) throws IOException {
		
		int index = 0;
		//Json ad = null;// = new IJson("{\"id0\": -30456e7}");
		String js = "{\"key\":{1,\"data\":[[\"Астр{а}хань,ГМО\",5,4,7,4,11,8,12,6,4,7,7,5]]}";
		//System.out.println(js);
		Json test = null;
		try {
		test = new IJson(js);
		}catch(JsonException e) {
			System.out.println(e.getJson().getRAWJson().substring(e.getIndex()));
			e.printStackTrace();
		}
		//test.add("dsa");
		test.put("test","1.543e7");
		test.put("test2", false);
		test.put("test3", true);
		test = test.get("data").add(false).back().back();
		//test = test.back();
		//test.put("test2", null);
		System.out.println(test);
		//System.out.println(test.getBoolean("test"));
	}

}
