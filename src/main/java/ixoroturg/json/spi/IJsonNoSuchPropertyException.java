package ixoroturg.json.spi;

public class IJsonNoSuchPropertyException extends IJsonException{
	public IJsonNoSuchPropertyException(String message){
		super(message);
	}
	public IJsonNoSuchPropertyException(String message, IJsonParseContext ctx) {
		super(message, ctx);
	}
}
