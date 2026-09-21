package ixoroturg.json.spi;

class IJsonUtil {
  static boolean isWhiteSpace(byte ch){
    return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
  }
  static boolean testNull(IJsonParseContext ctx) throws IJsonParseException{
    if(ctx.buffer.length - ctx.pointer - 1 < 4){
      ctx.read();
    }
    
    if(ctx.buffer[ctx.pointer] != 'n')
      throw new IJsonParseException("Unexpected symbol "+ctx.buffer[ctx.pointer],ctx);
    ctx.pointer++;
    ctx.column++;
    ctx.index++;
    if(ctx.buffer[ctx.pointer] != 'u')
      throw new IJsonParseException("Unexpected symbol "+ctx.buffer[ctx.pointer],ctx);
    ctx.pointer++;
    ctx.column++;
    ctx.index++;

    if(ctx.buffer[ctx.pointer] != 'l')
      throw new IJsonParseException("Unexpected symbol "+ctx.buffer[ctx.pointer],ctx);
    ctx.pointer++;
    ctx.column++;
    ctx.index++;

    if(ctx.buffer[ctx.pointer] != 'l')
      throw new IJsonParseException("Unexpected symbol "+ctx.buffer[ctx.pointer],ctx);
    ctx.pointer++;
    ctx.column++;
    ctx.index++;
    return true;
  }
}
