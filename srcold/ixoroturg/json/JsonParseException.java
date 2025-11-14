package ixoroturg.json;

public class JsonParseException extends JsonException{
	public JsonParseException(String describtion, Json json, int index){
		super(describtion, json, index);
	}
}
