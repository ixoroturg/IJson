import java.io.*;
import java.util.Iterator;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		
		//System.out.print((int)' '+" "+(int)'	'+" "+(int)'\n'+" "+(int)'\r');
		Json js = new IJson(new FileInputStream("example.txt"));
		try{
			System.out.println(js);
			Json result = js.get().get("glossary").get("GlossDiv").get("GlossList").get("GlossEntry").get("GlossDef").get("GlossSeeAlso");
			//result = (IJson) result.get("GlossDiv");
			//result = result.get(0);
			
			for(Json j : result){
				System.out.println(j);
			}
			//result = result.get(2).get("name");
			System.out.println(result);
			/*Iterator<Json> iter = js.iterator();
			while(iter.hasNext()){
				System.out.println(iter);
			}*/
		} catch(IJsonException e){
			e.printStackTrace();
		}
	}

}
