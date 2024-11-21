
public class IJsonException extends RuntimeException{
	public static final byte NO_SUCH_ELEMENT = 1, NO_SUCH_PROPERTY = 2, INVALID_JSON_FORMAT = 3;
	
	private int intArg = -1;
	private String strArg = null;
	private byte error = 0;
	String json = null;
	public IJsonException(String discribtion, byte error, Object argument, Json json){
		super(discribtion);
		this.error = error;
		switch(argument){
		case Integer arg->{intArg = arg;}
		case String arg-> {strArg = arg;}
		default -> {}
		};
		this.json = json.toString();
	}
	public byte getError(){
		return  error;
	}
	public String getDescription(){
		return switch(error){
		case NO_SUCH_ELEMENT -> "Элемент под индексом "+intArg+" не существует";
		case NO_SUCH_PROPERTY -> "Свойства с названием "+strArg+" не существует";
		case INVALID_JSON_FORMAT -> {
			String err = null;
			for(int i = intArg; i < 5; i++){
				if(i < json.length())
					err+=json.charAt(i);
			}
			yield "Неправильный формат json: "+err;
		}
		default -> "Неизвестная ошибка";
		};
	}
	public int getIntArg(){
		return intArg;
	}
	public String getStrArg(){
		return strArg;
	}
	public String getJson(){
		return json;
	}
}
