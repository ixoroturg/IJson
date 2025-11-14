package ixoroturg.json;

import java.io.Reader;

public class JsonInvalidNumberException extends JsonParseException {
 public JsonInvalidNumberException(String description){
   super(description);
 }
 public JsonInvalidNumberException(String description, IJsonParseContext ctx){
   super(description, ctx);
 }
}
