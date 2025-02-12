package IJson;

public class JsonIllegalTypeException extends JsonException{
	public JsonIllegalTypeException(String describtion, Json json, int index) {
		super(describtion, json , index);
	}	
}
