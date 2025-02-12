package IJson;

public class JsonNoSuchPropertyException extends JsonException{
	public JsonNoSuchPropertyException(String describtion, Json json, int index) {
		super(describtion, json, index);
	}
}
