package IJson;

public class JsonInvalidFormatException extends JsonException{
	public JsonInvalidFormatException(String describtion, Json json, int index){
		super(describtion, json, index);
	}
}
