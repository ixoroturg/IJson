package IJson;

public class JsonInvalidFormatException extends RuntimeException{
	public JsonInvalidFormatException(String describtion){
		super(describtion);
	}
}
