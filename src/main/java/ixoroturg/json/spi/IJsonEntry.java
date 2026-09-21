package ixoroturg.json.spi;

import java.io.IOException;

public abstract class IJsonEntry implements Cloneable{
  int size;
  IJsonEntry parent;
  String paramName;
  IJsonEntry back() throws IJsonNoParentException{
    if(parent == null)
      throw new IJsonNoParentException("This json has no parent");
    return parent;
  }
  IJsonEntry backOrNull() {
    return parent;
  }

  abstract void parse(IJsonParseContext ctx) throws IJsonParseException, IJsonInvalidStringException, IJsonInvalidNumberException, IJsonInvalidBooleanException, IJsonInvalidObjectException, IJsonInvalidArrayException;
  abstract String toFormatedString();
  abstract int buffSize();
  abstract int buffSizeFormat();

  abstract int buffSize(IJsonFormatContext ctx);
  abstract void toString(IJsonFormatContext ctx) throws IOException;

  public abstract IJsonEntry clone();
}
