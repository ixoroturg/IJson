package ixoroturg.json.spi;

public class IJsonInvalidObjectException extends IJsonParseException{
  public IJsonInvalidObjectException(String description){
    super(description);
  }
  public IJsonInvalidObjectException(String description, IJsonParseContext ctx){
    super(description,ctx);
  }
}
