package ixoroturg.json;

import java.io.Reader;

public class JsonInvalidStringException extends JsonParseException {
 public JsonInvalidStringException(String description){
   super(description);
 }
 public JsonInvalidStringException(String description, IJsonParseContext ctx){
   super(description,ctx);
 }
}
