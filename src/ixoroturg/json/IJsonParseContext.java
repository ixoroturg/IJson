package ixoroturg.json;

import java.io.Reader;
import java.io.IOException;

class IJsonParseContext {
  Reader reader;
  char[] buffer = new char[(1 << IJsonSetting.BUFFER_SIZE) * 2];
  int column;
  int row;
  int index;
  int pointer;
  byte hex = -1;
  int unicode = 0;
  boolean wasSlash = false;
  boolean wasExp = false;
  boolean wasDot = false;
  boolean shouldDot = false;
  boolean needKey = true;
  String key;
  long timer; 
  StringBuilder builder = new StringBuilder(IJsonSetting.STRING_BUILDER_BUFFER_SIZE);
  boolean firstPass = true;


  private boolean open = false;

  private static IJsonParseContext[] ctx = new IJsonParseContext[IJsonSetting.PARSE_CONTEXT_COUNT];

  static IJsonParseContext openContext(Reader reader) throws JsonParseException {
      for(int i = 0; i < ctx.length; i++){
        if(ctx[i] == null){
          ctx[i] = new IJsonParseContext(reader);
          return ctx[i];
        }
        if(!ctx[i].open){
          ctx[i].open(reader);
          return ctx[i];
        }
      }
      IJsonParseContext[] tmp = new IJsonParseContext[ctx.length*2];
      for(int i = 0; i < ctx.length; i++){
        tmp[i] = ctx[i];
      }
      ctx = tmp;
      return openContext(reader);
  }

  long close(){
    open = false;
    long result = System.currentTimeMillis() - timer;
    // System.out.println("Миллисекунды на момент закрытия: "+System.currentTimeMillis());
    // System.out.println("Таймер на момент закрытия: "+result);
    return result;
  }
  private void open(Reader reader) throws JsonParseException{
    timer = System.currentTimeMillis();
    // System.out.println("Таймер на момент открытия: "+timer);
    // System.out.println("Миллисекунды на момент закрытия: "+System.currentTimeMillis());
    open = true;
    index = 0;
    pointer = 0;
    column = 0;
    row = 0;
    builder.setLength(0);
    firstPass = true;
    this.reader = reader;
    try{
      int pos = reader.read(buffer);
      if(pos < buffer.length)
        buffer[pos] = (char)-1;
    }catch(IOException e){
      JsonParseException exp = new JsonParseException("Cannot read the stream");
      exp.initCause(e);
      throw exp;
    }
  }
  int read() throws JsonParseException{
    int buf = 1 << IJsonSetting.BUFFER_SIZE;
    System.arraycopy(buffer, buf, buffer, 0 ,buf);
    try{
      reader.read(buffer,buf, buf);
    } catch(IOException e){
      JsonParseException exp = new JsonParseException("Cannot read the stream");
      exp.initCause(e);
      throw exp;
    }
    pointer -= buf;
    return buf;
  }

  private IJsonParseContext(Reader reader) throws JsonParseException{
    open(reader);
  }
}
