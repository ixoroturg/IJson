package IJson;

import java.io.IOException;

public class JsonReadedNothingException extends IOException {
	public JsonReadedNothingException(String describtion){
		super(describtion);
	}
}
