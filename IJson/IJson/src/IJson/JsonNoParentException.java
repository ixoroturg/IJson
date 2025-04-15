package IJson;

public class JsonNoParentException extends JsonException{
	public JsonNoParentException(String describtion, Json json, int index){
		super(describtion, json, index);
	}
}
