package ixoroturg.json;

public class JsonInvalidObjectException extends JsonParseException{
  public JsonInvalidObjectException(String description){
    super(description);
  }
  public JsonInvalidObjectException(String description, IJsonParseContext ctx){
    super(description,ctx);
  }
}
