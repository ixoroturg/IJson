package ixoroturg.json;

public class JsonInvalidArrayException extends JsonParseException {
  public JsonInvalidArrayException(String description){
    super(description);
  }
  public JsonInvalidArrayException(String description, IJsonParseContext ctx){
    super(description, ctx);
  }
}
