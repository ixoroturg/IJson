package ixoroturg.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface Json {


  public Json put(String key, String value);
  public Json add(String value);
  public Json get(String key) throws JsonNoSuchPropertyException, JsonNoParentException, UnsupportedOperationException;
  public Json get(int key) throws JsonNoSuchPropertyException;

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


  // public Json getParent() throws JsonNoParentException;
  // public Json getParentOrNull();
  public String getPropertyName() throws JsonNoParentException;
  public String getPropertyNameOr(String value);
  public Json back() throws JsonNoParentException;
  public Json back(int depth) throws JsonNoParentException;
  public boolean has(String key);
  public int size() throws UnsupportedOperationException;
  public long getParseTime();



  

}
