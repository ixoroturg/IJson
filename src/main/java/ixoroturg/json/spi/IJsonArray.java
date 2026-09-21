package ixoroturg.json.spi;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

class IJsonArray extends IJsonEntry{
  List<IJsonEntry> list = new LinkedList<>();
  boolean firstPass = true;
  boolean needDot = false;
  boolean wasDot = false;

  IJsonArray(List<IJsonEntry> list){
    for(IJsonEntry entry: list){
      this.list.add(entry.clone());
    }
  }
  IJsonArray(){}

  @Override
  void parse(IJsonParseContext ctx) throws IJsonParseException, IJsonInvalidArrayException, IJsonInvalidStringException, IJsonInvalidNumberException, IJsonInvalidBooleanException, IJsonInvalidObjectException{
    for(; ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
      byte ch = ctx.buffer[ctx.pointer];
      if(IJsonUtil.isWhiteSpace(ch)){
        if(ch == '\n'){
          ctx.row++;
          ctx.column = 0;
        }
        continue;
      }
      if(ch == -1)
        throw new IJsonParseException("Unexpected end of line", ctx);
      if(ch == '[' && firstPass){
        firstPass = false;
        continue;
      }
      if(ch == ']')
        return;
      if(needDot && ch != ','){
          throw new IJsonInvalidArrayException("Expected ,",ctx);
      }
      switch(ch){
        case '[' -> {
          if(firstPass){
            firstPass = false;
            continue;
          }
          addEntry(new IJsonArray(), ctx);
        }
        case '{' -> {
          addEntry(new IJsonObject(), ctx);
        }
        case '\"' -> {
          addEntry(new IJsonString(), ctx);
        }
        case 't', 'f' -> {
          addEntry(new IJsonBoolean(), ctx);
        }
        case 'n' -> {
          if(!IJsonUtil.testNull(ctx))
            throw new IJsonParseException("Expected null",ctx);
          ctx.pointer--;
          addEntry(null,ctx);
        }
        case ',' -> {
          if(wasDot)
            throw new IJsonInvalidArrayException("Unexpected second ,", ctx);
          wasDot = true;
          needDot = false;
        }
        default -> {
          addEntry(new IJsonNumber(), ctx);
        }
      }
    }
    ctx.read();
    parse(ctx);
  }
  private int addEntry(IJsonEntry value, IJsonParseContext ctx) throws IJsonInvalidObjectException, IJsonInvalidStringException, IJsonInvalidNumberException, IJsonInvalidBooleanException, IJsonParseException{
    needDot = true;
    wasDot = false;
    if(value != null)
      value.parse(ctx);
    list.add(value);
    if(value != null)
    	value.parent = this;
    return ctx.pointer;
  }
  
  @Override
  public String toFormatedString() {
    return toString(true);
  }
  @Override
  public String toString(){
    return toString(false);
  }

  private String toString(boolean format){
    if(list.size() == 0)
      return "[]";
    IJsonFormatContext ctx = IJsonFormatContext.openContext(null);
    ByteArrayOutputStream writer = new ByteArrayOutputStream(buffSize(ctx));
    ctx.writer = writer;
    ctx.format = format;
    try{
      toString(ctx);
    } catch(IOException e){}
    String result = writer.toString();
    try{
      ctx.close();
    }catch(IOException e){}
    return result;
  }

  @Override
  public int buffSize() {
    if(list.size() == 0)
      return 2;
    int contentLength = 0;
    for(IJsonEntry entry: list){
      if(entry == null)
        contentLength +=4;
      else
        contentLength += entry.buffSize();
    }
    return contentLength + list.size() + 1;
  }

  @Override
  public int buffSizeFormat() {
    IJsonFormatContext ctx = IJsonFormatContext.openContext(null);
    ctx.format = true;
    int size = buffSize(ctx);
    try{
      ctx.close();
    }catch(IOException e){}
    return size;
  }

  @Override
  void toString(IJsonFormatContext ctx) throws IOException{
    if(list.size() == 0){
      ctx.writer.write("[]".getBytes(StandardCharsets.UTF_8));
      return;
    }
    boolean first = true;
    ctx.depth++;
    ctx.writer.write('[');
    for(IJsonEntry entry: list){
      if(!first){
        ctx.writer.write(',');
      }
      first = false;
      if(ctx.format){
        ctx.writer.write('\n');
        for(int j = 0; j < ctx.FORMAT_INDENT_COUNT * ctx.depth; j++){
          ctx.writer.write(ctx.FORMAT_INDENT_SYMBOL);
        }
      }
      if(entry == null){
        ctx.writer.write("null".getBytes(StandardCharsets.UTF_8));
      } else 
        entry.toString(ctx);
    }
    ctx.depth--;
    if(ctx.format){
      ctx.writer.write('\n');
        for(int j = 0; j < ctx.FORMAT_INDENT_COUNT * ctx.depth; j++){
          ctx.writer.write(ctx.FORMAT_INDENT_SYMBOL);
        }
    }
    ctx.writer.write(']');
  }

  @Override
  int buffSize(IJsonFormatContext ctx) {
    if(list.size() == 0)
      return 2;
    int result = 2;
    result += list.size() - 1;
    if(ctx.format){
      result += 2 +list.size() * (1 + ctx.FORMAT_INDENT_COUNT * (ctx.depth + 1) ) + ctx.depth*ctx.FORMAT_INDENT_COUNT ;
    }
    ctx.depth++;
    for(IJsonEntry entry: list){
      if(entry == null)
        result += 4;
      else
        result += entry.buffSize(ctx);
    }
    ctx.depth--;
    // result += contentLength;
    return result;
  }

  @Override
  public IJsonEntry clone(){
    IJsonArray js = new IJsonArray(list);
    return js;
  }

  @Override
  public boolean equals(Object obj){
    if(obj instanceof IJsonArray a){
      if(list.size() != a.list.size())
        return false;
      Iterator<IJsonEntry> i1 = list.iterator();
      Iterator<IJsonEntry> i2 = a.list.iterator();
      for(;i1.hasNext();){
        if(!i1.next().equals(i2.next()))
          return false;
      }
      return true;
    }
    return false;
  }
}
