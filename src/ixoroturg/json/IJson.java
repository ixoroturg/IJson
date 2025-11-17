package ixoroturg.json;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

public class IJson implements Json {
    IJsonEntry currentJson = null;
    private long parseTime = -1;
    private IJson(IJsonEntry json){
      currentJson = json;
    }

    public static IJson ofObject(){
      return new IJson(new IJsonObject());
    }
    public static IJson ofArray(){
      return new IJson(new IJsonArray());
    }
    public static IJson of(String json) throws JsonParseException {
      StringReader reader = new StringReader(json);
      return of(reader);
    }
    public static IJson of(InputStream input) throws JsonParseException {
      InputStreamReader reader = new InputStreamReader(input);
      return of(reader);
    }
    public static IJson of(Reader reader) throws JsonParseException{
      IJsonParseContext ctx = IJsonParseContext.openContext(reader);
      IJson result = of(ctx);
      // parseTime = ctx.close();
      return result;
    }
    @Override
    public long getParseTime(){
      return parseTime;
    }
    private static IJson of(IJsonParseContext ctx) throws JsonParseException{
      for(; ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
        char ch = ctx.buffer[ctx.pointer];
        if(IJsonUtil.isWhiteSpace(ch)){
          if(ch == '\n'){
            ctx.row++;
            ctx.column=-1;
          }
          continue;
        }
        if(ch == -1){
          return null;
        }
        // System.out.println("Найден не пустой символ на "+i);
        // ctx.pointer = i;
        switch(ch){
          case '{' -> {
            return createEntry(new IJsonObject(), ctx);
          }
          case '[' -> {
            return createEntry(new IJsonArray(), ctx);
          }
          case '\"' -> {
            return createEntry(new IJsonString(), ctx);
          }
          case 't', 'f' -> {
            return createEntry(new IJsonBoolean(), ctx);
          }
          case 'n' -> {
            // ctx.pointer = i;
            if(!IJsonUtil.testNull(ctx))
              throw new JsonParseException("Expected null", ctx);
            if(IJsonSetting.NULL_STRING_AS_NULL_VALUE)
              return null;
            else {
              IJsonString result = new IJsonString("null");
              return new IJson(result);
            }
          }
          default -> {
            return createEntry(new IJsonNumber(), ctx);
          }
        }
      }
      // ctx.pointer = i;
      ctx.read();
      return of(ctx);
    }
    private static IJson createEntry(IJsonEntry entry, IJsonParseContext ctx) throws JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidNumberException, JsonInvalidStringException, JsonInvalidBooleanException{
      entry.parse(ctx);
      IJson result = new IJson(entry);
      result.parseTime = ctx.close();
      return result;
    }

    @Override
    public Json put(String key, String value) {
      return null;
    }

    @Override
    public Json add(String value) {
        return null;
    }


    @Override
    public int buffSize() {
      return currentJson.buffSize();
    }

    @Override
    public int buffSizeFormat() {
      return currentJson.buffSizeFormat();
    }

    @Override
    public Json parse(String json) throws JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException{
      currentJson = IJson.of(json).currentJson;
      return this;
    }

    @Override
    public Json parse(InputStream stream) throws IOException, JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException {
      currentJson = IJson.of(stream).currentJson;
      return this;
    }

    @Override
    public Json parse(Reader reader) throws IOException, JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException{
      currentJson = IJson.of(reader).currentJson;
      return this;
    }

    @Override
    public String toString(){
      return currentJson.toString();
    }
    @Override
    public String toStringFormat() {
      return currentJson.toFormatedString();
    }

    @Override
    public void writeTo(OutputStream stream) throws IOException{
      OutputStreamWriter writer = new OutputStreamWriter(stream);
      writeTo(writer);
    }

    @Override
    public void writeTo(Writer writer) throws IOException{
      IJsonFormatContext ctx = IJsonFormatContext.openContext(writer);
      ctx.format = false;
      currentJson.toString(ctx);
      ctx.close();
    }

    @Override
    public void writeToFormat(OutputStream stream) throws IOException{
      OutputStreamWriter writer = new OutputStreamWriter(stream);
      writeToFormat(writer);
    }

    @Override
    public void writeToFormat(Writer writer) throws IOException{
      IJsonFormatContext ctx = IJsonFormatContext.openContext(writer);
      ctx.format = true;
      currentJson.toString(ctx);
      ctx.close();
    }
}
