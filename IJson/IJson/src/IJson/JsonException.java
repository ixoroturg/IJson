package IJson;

public class JsonException extends RuntimeException{
	public JsonException(String describtion) {
		super(describtion +"\nNote: Json delete all whitespaces before parsing. To show where is error check getRAWFormattedJson().substring(<showed position>) on this Json");
	}
}
