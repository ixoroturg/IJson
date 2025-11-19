package ixoroturg.json;

import java.io.Reader;

public class JsonException extends Exception{

  
  public JsonException(String description, IJsonParseContext ctx){
    super(createMessage(description, ctx));
  }

  public JsonException(String description){
    super(description);
  }

  
  protected static String createMessage(String description, IJsonParseContext ctx){
    StringBuilder builder = new StringBuilder( description.length() + 1 + IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX + 1 +IJsonSetting.CHARACTERS_AFTER_ERROR_INDEX + 1 + IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX + 1 + 8 + 24);
    builder.append(description);
    // builder.append(' ');
    builder.append(" at (");
    builder.append(String.valueOf(ctx.row+1));
    builder.append(',');
    builder.append(String.valueOf(ctx.column));
    builder.append('/');
    builder.append(String.valueOf(ctx.index));
    builder.append(')');
    builder.append('\n');
    int test = ctx.pointer - IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX;
    int length = IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX;
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
    for(int i = ctx.pointer+1, j = 0; j < IJsonSetting.CHARACTERS_AFTER_ERROR_INDEX && i < ctx.buffer.length; i++, j++){
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
    // if(length > 0)
      builder.append(" ".repeat(length));
    builder.append('^');
    // builder.append('\n');

    // builder.append("buffer shapshot:\n");
    // for(int i = 0; i < ctx.buffer.length; i++){
    //   if(IJsonUtil.isWhiteSpace(ctx.buffer[i])){
    //     builder.append(' ');
    //     continue;
    //   }
    //   builder.append(ctx.buffer[i]);
    // }

    // builder.append('\n');
    // if(ctx.pointer > 0)
    //   builder.append(" ".repeat(ctx.pointer-1));
    // builder.append('^');
    // builder.append("\nPointer position: ");
    // builder.append(String.valueOf(ctx.pointer));
    // builder.append("\nCharacter: "+ctx.buffer[ctx.pointer]);
    return builder.toString();
  }
  //   reader.reset();
  //   if(errorIndex > IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX){
  //     reader.skip(errorIndex - IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX);
  //     errorIndex = IJsonSetting.CHARACTERS.BEFORE.ERROR.INDEX;
  //   }
  //
  //
  //   if(reader == null || errorIndex < 0) {
  //     return description;
  //   }
  //   StringBuilder builder = new StringBuilder(description.length() + 1 + 2*errorIndex + 1 + IJsonSetting.CHARACTERS_AFTER_ERROR_INDEX + 1 + 1);
  //   builder.append(description);
  //   builder.append("\n");
  //
  //   for(int i = 0; i <= errorIndex; i++){
  //     builder.append((char)reader.read());
  //   }
  //
  //   for(int i = 0; i < IJsonSetting.CHARACTERS_AFTER_ERROR_INDEX; i++){
  //     int ch = reader.read();
  //     if(ch == -1)
  //       break;
  //     builder.append((char)ch);
  //   }
  //
  //   builder.append("\n");
  //   builder.append(" ".repeat(errorIndex));
  //   builder.append("^");
  //
  //   return builder.toString();
  //
  // }
}
