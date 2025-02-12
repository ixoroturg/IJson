package IJson;

public class JsonException extends RuntimeException{
	private Json json;
	private int index;
	public JsonException(String describtion, Json json, int index) {
		super(describtion+(index != -1 ? index : "")+"\nUse getJson() and getIndex() methods for additional information");
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
}
