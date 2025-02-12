package IJson;

public class JsonWrongTypeException extends JsonException{
	public JsonWrongTypeException(String describtion, Json json, int index) {
		super(describtion, json, index);
	}
}
