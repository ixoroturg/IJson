package test;

import java.io.*;

import IJson.*;
public class main {

	public static void main(String[] args) throws IOException {
		
		int index = 0;
		Json ad = new IJson();
		ad.put("id", index+"");
		
		
		//30777 error
		//value = ad.get("id").toString();
		//value = js.get("menu").get("myWHString").toString();
		System.out.println(ad);
	}

}
