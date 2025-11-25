package ixoroturg.json;

import java.io.IOException;

class IJsonString extends IJsonEntry{

  // String strValue;
  String value;
  IJsonString(){}
  IJsonString(String value){
    this.value = value;
  }
  @Override
  public String toString(){
    if(value == null)
      return null;
    StringBuilder builder = new StringBuilder(value.length() + 2);
    builder.append('\"')
      .append(value)
      .append('\"');
    return builder.toString();
  }
  @Override
  void toString(IJsonFormatContext ctx) throws IOException {
    if(value == null){
      ctx.writer.write("null");
      return;
    }
    if(!IJsonSetting.FORMAT_DIRECT_WRITE_CONTROL_CHARACTER){
      ctx.writer.write('\"');
      ctx.writer.write(value);
      ctx.writer.write('\"');
      return;
    }
    ctx.writer.write('\"');
    boolean wasSlash = false;
    for(int i = 0; i < value.length(); i++){
      if(wasSlash){
        switch(value.charAt(i)){
          case 't' -> {ctx.writer.write('\t');}
          case 'r' -> {ctx.writer.write('\r');}
          case 'n' -> {ctx.writer.write('\n');}
          case 'f' -> {ctx.writer.write('\f');}
          case 'b' -> {ctx.writer.write('\b');}
          default -> {ctx.writer.write('\\'); ctx.writer.write(value.charAt(i));}
        }
        wasSlash = false;
        continue;
      }
      if(value.charAt(i) == '\\'){
        if(!wasSlash){
          wasSlash = true;
          continue;
        }
      }
      ctx.writer.write(value.charAt(i));
    }
    ctx.writer.write('\"');
  }

  @Override
  public int buffSize(){
    return value.length()+2;
  }
  @Override
  public int buffSizeFormat(){
    return value.length()+2;
  }
  @Override
  public String toFormatedString(){
    return toString();
  }
  public boolean equals(Json json) {
    if(json instanceof IJsonString str){
      return equals(str.value);
    }
    return false;
  }

  public boolean equals(String str) {
    if(value == null && str == null)
      return true;
    if(value == null || str == null)
      return false;
    return value.equals(str);
  }

  @Override
  void parse(IJsonParseContext ctx) throws JsonParseException, JsonInvalidStringException {
      StringBuilder result = validate(ctx);
      value = result.toString();
  }
  
  static StringBuilder validate(IJsonParseContext ctx) throws JsonParseException, JsonInvalidStringException{
    if(ctx.firstPass){
      ctx.builder.setLength(0);
      ctx.column++;
      ctx.index++;
      ctx.pointer++;
      ctx.firstPass = false;
    }
    for(; ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
      char ch = ctx.buffer[ctx.pointer];
      
      switch(ch){
        case (char)65535, (char)0 -> {
          throw new JsonParseException("Unexcepted end of line", ctx);
        }
        case 'n' -> {
          if(ctx.wasSlash){
            ctx.wasSlash = false;
          }
        }
        case '\t', '\n', '\r', '\b', '\f' -> {
          if(!IJsonSetting.ESCAPE_CONTROL_CHARACTERS){
            throw new JsonInvalidStringException("No control characters allowed", ctx);
          }
          if(ch == '\n'){
            ctx.row++;
            ctx.column = 0;
          }
          ctx.builder.append('\\');
          switch(ch){
            case '\t' -> ctx.builder.append('t');
            case '\n' -> ctx.builder.append('n');
            case '\r' -> ctx.builder.append('r');
            case '\b' -> ctx.builder.append('b');
            case '\f' -> ctx.builder.append('f');
          }
          continue;
        }
        case '\\' -> {
          ctx.wasSlash = !ctx.wasSlash;
        }
        case '\"' -> {
          if(ctx.wasSlash){
            ctx.wasSlash = false;
          } else {
            ctx.firstPass = true;
            return ctx.builder;
          }
        }
        case 'u' -> {
          if(ctx.wasSlash){
            ctx.hex = 0;
            ctx.wasSlash = false;
            continue;
          }
        }
        default -> {
          if(ctx.wasSlash){
            switch(ch){
              case 't', 'b', 'r', 'f', '/' -> {
                ctx.wasSlash = false;
              }
              default -> {
                throw new JsonInvalidStringException("No special character found after slash",ctx);
              }
            }
          } else {
            if(ctx.hex != -1 && ctx.hex < 4){
              ch = Character.toLowerCase(ch);
              if((ch < '0' || ch > '9') && (ch < 'a' || ch > 'f'))
                throw new JsonInvalidStringException("After \\u should be 4 hex digits",ctx);
              if(ctx.hex == 0 && !IJsonSetting.DECODE_UNICODE_SEQUENCE)
                ctx.builder.append('u');

              if(IJsonSetting.DECODE_UNICODE_SEQUENCE){
                ctx.unicode = ctx.unicode << 4;
                if(ch <= '9')
                  ctx.unicode += (ch - '0');
                else
                  ctx.unicode += (ch - 'a' + 10);
                if(ctx.hex == 3){
                  switch((char) ctx.unicode){
                    case '\t' -> {
                      ctx.builder.append('t');
                    }
                    case '\f' -> {
                      ctx.builder.append('f');
                    }
                    case '\n' -> {
                      ctx.builder.append('n');
                    }
                    case '\r' -> {
                      ctx.builder.append('r');
                    }
                    case '\b' -> {
                      ctx.builder.append('b');
                    }
                    default -> {
                      ctx.builder.setCharAt(ctx.builder.length() - 1, (char) ctx.unicode);
                    }
                  }
                  ctx.hex = -1;
                  continue;
                } 
                ctx.hex++;
                continue;
              }
              ctx.hex++;
            }
          }
        }
      };
      ctx.builder.append(ch);
    }
    ctx.read();
    return validate(ctx);
  }

  @Override
  int buffSize(IJsonFormatContext ctx) {
    return value.length()+2;
  }

  @Override
  public IJsonEntry iClone(){
    IJsonString js = new IJsonString(value);
    return js;
  }

  @Override
  public boolean equals(Object obj){
    if(obj instanceof IJsonString str){
      return str.value.equals(value);
    }
    return false;
  }
}
