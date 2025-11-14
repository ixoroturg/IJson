package ixoroturg.json;

public class JsonInvalidBooleanException extends JsonParseException{
    JsonInvalidBooleanException(String description){
      super(description);
    }
    JsonInvalidBooleanException(String description, IJsonParseContext ctx){
      super(description, ctx);
    }
}
