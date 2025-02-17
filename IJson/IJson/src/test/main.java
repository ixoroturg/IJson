package test;

import java.io.*;

import IJson.*;
public class main {

	public static void main(String[] args) throws IOException {
		
		Json test = new IJson()
				.put("field1", new IJson())
					.get("field1")
					.add(new IJson())
						.get(0)
						.put("id", 123)
						.put("name", "ixoroturg")
					.back().back();
		System.out.println(test);
		//System.out.println(test.getBoolean("test"));
	}

}
