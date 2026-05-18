package ixoroturg.json;

public class JsonNoSuchPropertyException extends JsonException{
	public JsonNoSuchPropertyException(String message){
		super(message);
	}
	public JsonNoSuchPropertyException(String message, IJsonParseContext ctx) {
		super(message, ctx);
	}
}
