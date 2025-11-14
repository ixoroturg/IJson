package ixoroturg.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface Json {


  public Json put(String key, String value);
  public Json add(String value);

  public int buffSize();
  public int buffSizeFormat();

  public Json parse(String json) throws JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException;
  public Json parse(InputStream stream) throws IOException, JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException;
  public Json parse(Reader reader) throws IOException, JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException;

  public String toStringFormat();
  public void writeTo(OutputStream stream) throws IOException;
  public void writeTo(Writer writer) throws IOException;
  public void writeToFormat(OutputStream stream) throws IOException;
  public void writeToFormat(Writer writer) throws IOException;
}
