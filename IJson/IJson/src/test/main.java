package test;

import java.io.*;

import IJson.*;
public class main {

	public static void main(String[] args) throws IOException {
		
		int index = 0;
		//Json ad = null;// = new IJson("{\"id0\": -30456e7}");
		Json ad = new IJson("{\"station\":\"8826,6{[965,9058,9932,100,7435,8067,787,31,6870,2,10076,7043,10188,8530,9846,826,819,9125,11325,298\",\"data\":\"321,326,325,322,328,327,329,330,324,323\",\"separator\":\",\"}\n"
				+ "");
		
		try {
		;
		//ad.readFrom(new FileInputStream("/home/ixoroturg/Рабочий стол/sample.json"));
		}catch (Exception e) {
			//System.out.println(((IJson) ad).getRAWFormattedJson());
			e.printStackTrace();
		}
		
		//String input = new String(new FileInputStream("/home/ixoroturg/Рабочий стол/sample.json").readAllBytes()).trim();
		
		//System.out.println("Вход: "+input.substring(95373, 95373 + 200));
		//System.out.println(((IJson) ad).getRAWFormattedJson());
		//System.out.println(((IJson) ad).getRAWFormattedJson().substring(0, 120));
		//ad.put("id", "00.98");
		
		//System.out.println((int)'');
		//30777 error
		//value = ad.get("id").toString();
		//value = js.get("menu").get("myWHString").toString();
		//System.out.println(ad);
		//System.out.println(ad.getString("station"));
	}

}
