package ixoroturg.json.spi;

public class IJsonInvalidBooleanException extends IJsonParseException{
    IJsonInvalidBooleanException(String description){
      super(description);
    }
    IJsonInvalidBooleanException(String description, IJsonParseContext ctx){
      super(description, ctx);
    }
}
