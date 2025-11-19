package ixoroturg.json;

import java.io.Writer;
import java.io.IOException;

public abstract class IJsonEntry{
  // T value;
  int size;
  IJsonEntry parent;
  String paramName;
  
  // String propertyName() throws JsonNoParentException{
  //   if(paramName == null)
  //     throw new JsonNoParentException("This json has no parent");
  //   return paramName;
  // }
  // String propertyNameOrNull() {
  //   return paramName;
  // }
  // String propertyNameOr(String value){
  //   if(paramName == null)
  //     return value;
  //   return paramName;
  // }

  IJsonEntry back() throws JsonNoParentException{
    if(parent == null)
      throw new JsonNoParentException("This json has no parent");
    return parent;
  }
  IJsonEntry backOrNull() {
    return parent;
  }

  abstract void parse(IJsonParseContext ctx) throws JsonParseException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException, JsonInvalidObjectException, JsonInvalidArrayException;
  public abstract String toFormatedString();
  public abstract int buffSize();
  public abstract int buffSizeFormat();

  abstract int buffSize(IJsonFormatContext ctx);
  abstract void toString(IJsonFormatContext ctx) throws IOException;

  abstract IJsonEntry iClone();
}
