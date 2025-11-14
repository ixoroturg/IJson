package ixoroturg.json;

import java.io.Writer;
import java.io.IOException;

class IJsonBoolean extends IJsonEntry<Boolean>{
  boolean value;
  private static final char[] t = {'t', 'r', 'u', 'e'};
  private static final char[] f = {'f', 'a', 'l', 's', 'e'};
  IJsonBoolean(){}
  IJsonBoolean(boolean value){
    this.value = value;
  }

  @Override
  void parse(IJsonParseContext ctx) throws JsonParseException, JsonInvalidBooleanException{
    value = validate(ctx);
    ctx.pointer--;
  }
  static boolean validate(IJsonParseContext ctx) throws JsonParseException, JsonInvalidBooleanException {
    // int i = ctx.pointer;
    if(ctx.buffer.length - ctx.pointer < 6){
      ctx.read();
      // i = ctx.pointer;
    }
      switch(ctx.buffer[ctx.pointer]){
        case 't' -> {
          for(int j = 0; j < 4; j++, ctx.column++, ctx.index++){
            if(ctx.buffer[ctx.pointer + j] != t[j])
              throw new JsonInvalidBooleanException("Unexpected symbol "+ctx.buffer[ctx.pointer+j], ctx);
          }
          ctx.pointer+=4;
          return true;
        }
        case 'f' -> {
          for(int j = 0; j < 5; j++, ctx.column++, ctx.index++){
            if(ctx.buffer[ctx.pointer + j] != f[j])
              throw new JsonInvalidBooleanException("Unexpected symbol "+ctx.buffer[ctx.pointer+j], ctx);
          }
          ctx.pointer+=5;
          return false;
        }
        default -> {
          throw new JsonInvalidBooleanException("Unexpected symbol "+ctx.buffer[ctx.pointer], ctx);
        }
      }
    
  }

  @Override
  public String toFormatedString() {
    return value ? "true" : "false";
  }

  @Override
  public int buffSize() {
    return value ? 4 : 5;
  }

  @Override
  public int buffSizeFormat() {
    return value ? 4 : 5;
  }

  @Override
  void toString(IJsonFormatContext ctx) throws IOException {
    ctx.writer.write(value ? "true" : "false");
  }

  @Override
  int buffSize(IJsonFormatContext ctx) {
    return value ? 4 : 5;
  }
  @Override
  public String toString() {
    return value ? "true" : "false";
  }
}
