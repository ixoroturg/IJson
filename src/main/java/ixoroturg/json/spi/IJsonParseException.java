package ixoroturg.json.spi;


public class IJsonParseException extends IJsonException {
  public IJsonParseException(String description){
    super(description);
  }
  public IJsonParseException(String description, IJsonParseContext ctx){
    super(description, ctx);
  }
}
