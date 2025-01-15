package test;

import java.io.*;

import IJson.*;
public class main {

	public static void main(String[] args) throws IOException {
		
		String test = "12345";
		//test = test.substring(0,test.length()-1);
		System.out.println(test);
		
		Json js = new IJson();
		js.readFrom(new FileInputStream("test.txt"));
		String value = js.get("menu").get("id").toString();
		System.out.println(value);
	}

}
