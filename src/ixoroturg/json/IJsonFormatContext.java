package ixoroturg.json;

import java.io.Writer;

class IJsonFormatContext{
  Writer writer;
  private boolean open = false;
  int depth;
  boolean format = false;
  
  private static IJsonFormatContext[] ctx = new IJsonFormatContext[IJsonSetting.FORMAT_CONTEXT_COUNT];

  static IJsonFormatContext openContext(Writer writer){
    for(int i = 0; i < ctx.length; i++){
      if(ctx[i] == null){
        ctx[i] = new IJsonFormatContext(writer);
        return ctx[i];
      }
      if(!ctx[i].open){
        return ctx[i].open(writer);
      }
    }
    IJsonFormatContext[] tmp = new IJsonFormatContext[ctx.length * 2];
    System.arraycopy(ctx,0,tmp,0,ctx.length);
    return openContext(writer);
  }

  private IJsonFormatContext open(Writer writer){
    this.writer = writer;
    depth = 0;
    open = true;
    return this;
  }
  void close(){
    open = false;
  }
  private IJsonFormatContext(Writer writer){
    open(writer);
  }
}
