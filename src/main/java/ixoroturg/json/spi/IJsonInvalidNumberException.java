package ixoroturg.json.spi;

// import java.io.Reader;

public class IJsonInvalidNumberException extends IJsonParseException {
 public IJsonInvalidNumberException(String description){
   super(description);
 }
 public IJsonInvalidNumberException(String description, IJsonParseContext ctx){
   super(description, ctx);
 }
}
