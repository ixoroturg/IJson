package ixoroturg.json;

// import java.io.IOException;

public class JsonException extends RuntimeException{

  public JsonException(String description, IJsonParseContext ctx){
    super(createMessage(description, ctx));
  }
  // public JsonException(String description, IJsonFormatContext ctx){
  //   super(description);
  //   ctx.open = false;
  // }
  public JsonException(String description){
    super(description);
  }

  
  protected static String createMessage(String description, IJsonParseContext ctx){
    StringBuilder builder = new StringBuilder( description.length() + 1 + ctx.CHARACTERS_BEFORE_ERROR_INDEX + 1 +ctx.CHARACTERS_AFTER_ERROR_INDEX + 1 + ctx.CHARACTERS_BEFORE_ERROR_INDEX + 1 + 8 + 24);
    builder.append(description);
    builder.append(" at (");
    builder.append(String.valueOf(ctx.row+1));
    builder.append(',');
    builder.append(String.valueOf(ctx.column));
    builder.append('/');
    builder.append(String.valueOf(ctx.index));
    builder.append(')');
    builder.append('\n');
    int test = ctx.pointer - ctx.CHARACTERS_BEFORE_ERROR_INDEX;
    int length = ctx.CHARACTERS_BEFORE_ERROR_INDEX;
    if(test < 0){
      length += test;
    }

    for(int i = ctx.pointer - length, j = 0; j < length + 1; i++, j++){
      if(ctx.buffer[i] == 0 || ctx.buffer[i] == (char)65535){
        break;
      }
      if(IJsonUtil.isWhiteSpace(ctx.buffer[i])){
        builder.append(' ');
        continue;
      }
      builder.append(ctx.buffer[i]);
    }
    for(int i = ctx.pointer+1, j = 0; j < ctx.CHARACTERS_AFTER_ERROR_INDEX && i < ctx.buffer.length; i++, j++){
      char ch = ctx.buffer[i];
      if(ch == 0 || ch == (char)65535){
        break;
      }
      if(IJsonUtil.isWhiteSpace(ch)){
        builder.append(' ');
        continue;
      }
      builder.append(ch);
    }
    builder.append('\n');
      builder.append(" ".repeat(length));
    builder.append('^');
    ctx.close();
    return builder.toString();
  }
}
