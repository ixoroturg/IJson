package IJson;

import java.util.ArrayList;
import java.util.List;

public class JsonException extends RuntimeException{
	private Json json;
	private int index;
	public JsonException(String describtion, Json json, int index) {
		super(describtion+"\n"+getDescription(json, index));
		this.json = json;
		this.index = index;
	}
	/**
	 * @return Json object which is the cause of the exception
	 */
	public Json getJson() {
		return json;
	}
	/**
	 * @return relative inner index in json string where was exception
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * @return inner json string
	 */
	public String getRAWJson() {
		return json.getRAWJson();
	}
	private static String getDescription(Json js, int index) {
		if(true)
			return "error description is not workong";
		List<String> desc = new ArrayList<>();
			for(;((IJson)js).parent != null;) {
				desc.add(js.getPropertyName());
				js = js.back();
			}
		desc = desc.reversed();
		String result = "";
		for(String str: desc) {
			result += "."+str;
		}
		result = result.substring(1);
		String answer = "at "+result+" / ";
		if(index != -1)
			index += answer.length();
		answer += js.get(result).toString() + (index >= 0 ? "\n"+" ".repeat(index)+"^" : "");
		return answer;
	}
}
