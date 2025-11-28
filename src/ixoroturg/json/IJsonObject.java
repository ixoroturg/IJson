package ixoroturg.json;

import java.util.Map;
import java.io.StringWriter;
import java.util.HashMap;
import java.io.IOException;

public class IJsonObject extends IJsonEntry {
  Map<String, IJsonEntry> map = new HashMap<>();

  String key;
  boolean needKey = true;
  boolean firstPass = true;
  boolean needQuote = false;
  boolean wasQuote = false;
  boolean needDot = false;
  boolean wasDot = false;

  IJsonObject(){}
  IJsonObject(Map<String, IJsonEntry> map){
    for(Map.Entry<String, IJsonEntry> entry: map.entrySet()){
      this.map.put(entry.getKey(), entry.getValue().iClone());
    }
  }
  @Override
  void parse(IJsonParseContext ctx) throws JsonParseException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException, JsonInvalidArrayException{

    for(;ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
      char ch = ctx.buffer[ctx.pointer];
      if(IJsonUtil.isWhiteSpace(ch)){
        if(ch == '\n'){
          ctx.row++;
          ctx.column = 0;
        }
        continue;
      }
      if(ch == '{' && firstPass){
        firstPass = false;
        continue;
      }
      if(ch == '}'){
        if(wasQuote)
          throw new JsonParseException("Unexpected end of line",ctx);
        return;
      }
      if(ch == -1 || ch == 0){
          throw new JsonParseException("Unexpected end of line",ctx);
      }
      if(needKey && ch != '\"'){
        throw new JsonInvalidObjectException("Expected \" but found "+ch, ctx);
      }
      if(needQuote && ch != ':'){
        throw new JsonInvalidObjectException("Expected : but found "+ch, ctx);
      }
      if(needDot && ch != ','){
        throw new JsonInvalidObjectException("Expected , but found "+(int)ch, ctx);
      }
      switch(ch){
        case ',' -> {
          if(wasDot)
            throw new JsonInvalidObjectException("Unexpected second ,", ctx);
          wasDot = true;
          needDot = false;
          needKey = true;
        }
        case '\"' -> {
          if(needKey){
            StringBuilder result = IJsonString.validate(ctx);
            key = result.toString();
            needKey = false;
            needQuote = true;
          } else {
            addEntry(new IJsonString(), ctx);
          }
        }
        case ':' -> {
          if(wasQuote){
            throw new JsonInvalidObjectException("Unexpected second :",ctx);
          }
          wasQuote = true;
          needQuote = false;
          continue;
        }
        case 'n' -> {
          if(!IJsonUtil.testNull(ctx))
            throw new JsonParseException("Expected null", ctx);
          ctx.pointer--;
          addEntry(null,ctx);
        }
        
        case '{' -> {
          addEntry(new IJsonObject(), ctx);
        }
        case '[' -> {
          addEntry(new IJsonArray(), ctx);
        }
        case 't', 'f' -> {
          addEntry(new IJsonBoolean(), ctx);
        }
        
        default -> {
          addEntry(new IJsonNumber(),ctx);
        }
      }
    }
    ctx.read();
    parse(ctx);
  }
    
    private int addEntry(IJsonEntry value, IJsonParseContext ctx) throws JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException, JsonParseException{
      wasQuote = false;
      needDot = true;
      wasDot = false;
      if(value != null)
        value.parse(ctx);
      map.put(key,value);
      if(value != null)
    	  value.parent = this;
      return ctx.pointer;
    }

  @Override
  public String toString(){
    IJsonFormatContext ctx = IJsonFormatContext.openContext(null);
    ctx.format = false;
    StringWriter writer = new StringWriter(buffSize(ctx));
    ctx.writer = writer;
    try{
      toString(ctx);
    }catch(IOException e){}
    String result = writer.toString();
    try{
      ctx.close();

    }catch(IOException e){}
    return result;
  }
  @Override
  public String toFormatedString() {
    IJsonFormatContext ctx = IJsonFormatContext.openContext(null);
    ctx.format = true;
    StringWriter writer = new StringWriter(buffSize(ctx));
    ctx.writer = writer;

    try{
      toString(ctx);
    } catch(IOException e){}
    String result = writer.toString();
    try{
      ctx.close();
    } catch(IOException e){}
    return result;
  }

  @Override
  public int buffSize() {
    if(map.size() == 0)
      return 2;
    int contentLength = 0;
    for(Map.Entry<String, IJsonEntry> entry : map.entrySet()){
      contentLength += entry.getKey().length() + 2;
      // contentLength += entry.getValue().buffSize();
      IJsonEntry test = entry.getValue();
      if(test == null)
        contentLength +=4;
      else
        contentLength += test.buffSize();
    }
    return contentLength + map.size() * 2 + 1 ;
  }

  @Override
  public int buffSizeFormat() {
    IJsonFormatContext ctx = IJsonFormatContext.openContext(null);
    int size = buffSize(ctx);
    try{
      ctx.close();
    }catch(IOException e){}
    return size;
  }

  @Override
  void toString(IJsonFormatContext ctx) throws IOException{
      if(map.size() == 0){
        ctx.writer.write("{}");
        return;
      }
      ctx.depth++;
      ctx.writer.write('{');
      boolean first = true;
      IJsonEntry test;
      for(Map.Entry<String, IJsonEntry> entry: map.entrySet()){
        if(!first){
          ctx.writer.write(',');
          // if(ctx.format)
        }
        first = false;
        if(ctx.format){
          ctx.writer.write('\n');
          for(int j = 0; j < IJsonSetting.FORMAT_INDENT_COUNT * ctx.depth; j++){
            ctx.writer.write(IJsonSetting.FORMAT_INDENT_SYMBOL);
          }
        }

        ctx.writer.write('\"');
        ctx.writer.write(entry.getKey());
        ctx.writer.write("\":");
        if(ctx.format)
          ctx.writer.write(' ');
        test = entry.getValue();
        if(test == null)
          ctx.writer.write("null");
        else
          test.toString(ctx);
      }
      ctx.depth--;
      if(ctx.format){
        ctx.writer.write('\n');
        for(int j = 0; j < IJsonSetting.FORMAT_INDENT_COUNT * ctx.depth; j++){
          ctx.writer.write(IJsonSetting.FORMAT_INDENT_SYMBOL);
        }
      }
      ctx.writer.write('}');
  }

  @Override
  int buffSize(IJsonFormatContext ctx) {
    if(map.size() == 0)
      return 2;

    int result = 2;
    result += map.size() * 2 - 1;
    if(ctx.format){
      result += 2 + map.size() * (1 + IJsonSetting.FORMAT_INDENT_COUNT * (ctx.depth + 1)) + ctx.depth*IJsonSetting.FORMAT_INDENT_COUNT;
    }
    int contentLength = 0;
      ctx.depth++;
    for(Map.Entry<String, IJsonEntry> entry: map.entrySet()){
      contentLength += entry.getKey().length() + 2;
      IJsonEntry test = entry.getValue();
      if(test == null)
        contentLength +=4;
      else
        contentLength += test.buffSize(ctx);
    }
      ctx.depth--;
    result += contentLength;
    return result;
  }

  @Override
  public IJsonEntry iClone(){
    IJsonObject js = new IJsonObject(map);
    return js;
  }
  @Override
  public boolean equals(Object obj){
    if(obj instanceof IJsonObject o){
      if(map.size() != o.map.size())
        return false;
      int i = 0;
      for(Map.Entry<String,IJsonEntry> entry: o.map.entrySet()){
        if(!o.map.get(entry.getKey()).equals(entry.getValue())){
          return false;
        }
        i++;
      }
      if(i != map.size())
        return false;
      return true;
    }
    return false;
  }
}
