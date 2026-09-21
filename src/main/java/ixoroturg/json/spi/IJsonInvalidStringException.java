package ixoroturg.json.spi;

// import java.io.Reader;

public class IJsonInvalidStringException extends IJsonParseException {
 public IJsonInvalidStringException(String description){
   super(description);
 }
 public IJsonInvalidStringException(String description, IJsonParseContext ctx){
   super(description,ctx);
 }
}
