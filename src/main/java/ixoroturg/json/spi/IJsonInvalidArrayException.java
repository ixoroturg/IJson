package ixoroturg.json.spi;

public class IJsonInvalidArrayException extends IJsonParseException {
  public IJsonInvalidArrayException(String description){
    super(description);
  }
  public IJsonInvalidArrayException(String description, IJsonParseContext ctx){
    super(description, ctx);
  }
}
