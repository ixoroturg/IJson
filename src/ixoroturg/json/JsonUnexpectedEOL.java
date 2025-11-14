package ixoroturg.json;

import java.io.Reader;

public class JsonUnexpectedEOL extends JsonException {
  public JsonUnexpectedEOL(String description){
    super(description);
  }
  public JsonUnexpectedEOL(String description, IJsonParseContext ctx){
    super(description, ctx);
  }
}
