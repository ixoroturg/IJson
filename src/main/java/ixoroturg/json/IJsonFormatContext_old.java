package ixoroturg.json;

import java.io.IOException;
import java.io.Writer;

class IJsonFormatContext_old{
  Writer writer;
  boolean open = false;
  int depth;
  boolean format = false;
  
  private static IJsonFormatContext_old[] ctx = new IJsonFormatContext_old[IJsonSetting.FORMAT_CONTEXT_COUNT];

  static IJsonFormatContext_old openContext(Writer writer){
    for(int i = 0; i < ctx.length; i++){
      if(ctx[i] == null){
        ctx[i] = new IJsonFormatContext_old(writer);
        return ctx[i];
      }
      if(!ctx[i].open){
        return ctx[i].open(writer);
      }
    }
    IJsonFormatContext_old[] tmp = new IJsonFormatContext_old[ctx.length * 2];
    System.arraycopy(ctx,0,tmp,0,ctx.length);
    return openContext(writer);
  }

  private IJsonFormatContext_old open(Writer writer){
    this.writer = writer;
    depth = 0;
    open = true;
    return this;
  }
  void close() throws IOException{
    open = false;
    if(IJsonSetting.AUTO_FLUSH)
      writer.flush();
  }
  private IJsonFormatContext_old(Writer writer){
    open(writer);
  }
}
