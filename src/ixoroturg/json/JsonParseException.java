package ixoroturg.json;

import java.io.Reader;

public class JsonParseException extends JsonException {
  public JsonParseException(String description){
    super(description);
  }
  public JsonParseException(String description, IJsonParseContext ctx){
    super(description, ctx);
  }
}
