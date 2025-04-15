package IJson;

public class JsonArrayIndexOutOfBoundsException extends JsonException{
	public JsonArrayIndexOutOfBoundsException(String describtion, Json json, int index) {
		super(describtion, json , index);
	}	
}