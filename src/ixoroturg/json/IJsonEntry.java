package ixoroturg.json;

import java.io.IOException;

public abstract class IJsonEntry{
  int size;
  IJsonEntry parent;
  String paramName;
  IJsonEntry back() throws JsonNoParentException{
    if(parent == null)
      throw new JsonNoParentException("This json has no parent");
    return parent;
  }
  IJsonEntry backOrNull() {
    return parent;
  }

  abstract void parse(IJsonParseContext ctx) throws JsonParseException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException, JsonInvalidObjectException, JsonInvalidArrayException;
  abstract String toFormatedString();
  abstract int buffSize();
  abstract int buffSizeFormat();

  abstract int buffSize(IJsonFormatContext ctx);
  abstract void toString(IJsonFormatContext ctx) throws IOException;

  abstract IJsonEntry iClone();
}
