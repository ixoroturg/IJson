package ixoroturg.json;

class IJsonUtil {
  static boolean isWhiteSpace(char ch){
    return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
  }
  static boolean testNull(IJsonParseContext ctx) throws JsonParseException{
    if(ctx.buffer.length - ctx.pointer - 1 < 4){
      ctx.read();
    }
    ctx.index+=4;
    ctx.pointer+=4;
    ctx.column+=4;
    return (ctx.buffer[ctx.pointer - 4] == 'n') && (ctx.buffer[ctx.pointer - 3] == 'u') && (ctx.buffer[ctx.pointer - 2] == 'l') && (ctx.buffer[ctx.pointer - 1] == 'l');
  }
}
