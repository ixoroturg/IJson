package ixoroturg.json;

import java.io.Reader;
import java.io.IOException;
import java.io.Writer;

class IJsonString extends IJsonEntry{

  // String strValue;
  String value;
  IJsonString(){}
  IJsonString(String value){
    this.value = value;
    // StringBuilder builder = new StringBuilder(value.length());
    // builder.append('\"')
    //   .append(value)
    //   .append('\"');
    // strValue = builder.toString();
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
      // value = result.substring(1,result.length()-1);
      value = result.toString();
  }
  
  static StringBuilder validate(IJsonParseContext ctx) throws JsonParseException, JsonInvalidStringException{
    // System.out.println("Начало парсинга: " + ctx.pointer);
    if(ctx.firstPass){
      ctx.builder.setLength(0);
      // ctx.builder.append('\"');
      ctx.column++;
      ctx.index++;
      ctx.pointer++;
      ctx.firstPass = false;
    }
    // int i;
    for(; ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
      char ch = ctx.buffer[ctx.pointer];
      
      switch(ch){
        case (char)65535, (char)0 -> {
          // ctx.pointer = i;
          throw new JsonParseException("Unexcepted end of line", ctx);
        }
        case 'n' -> {
          if(ctx.wasSlash){
            ctx.wasSlash = false;
          }
        }
        case '\t', '\n', '\r', '\b', '\f' -> {
          if(!IJsonSetting.ESCAPE_CONTROL_CHARACTERS){
            // ctx.pointer = i;
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
            // ctx.pointer = i;
            ctx.firstPass = true;
            // ctx.builder.append('\"');
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
    // ctx.pointer = i;
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
  // int parse(Reader reader, int offset) throws JsonInvalidStringException, JsonParseException  {
  //   StringBuilder builder = new StringBuilder(16);
  //   int index = 0;
  //   int ch;
  //   boolean wasSlash = false;
  //   boolean open = false;
  //   try{
  //     while((ch = reader.read()) != -1){
  //       if(!open && ch != '\"')
  //         break;
  //       open = true;
  //
  //       switch(ch) {
  //         case '\t', '\b', '\n', '\r', '\f' -> {
  //           throw new JsonInvalidStringException("Invalid character found. Code: " + Integer.toHexString(ch), reader, index + offset);
  //         }
  //
  //         case '\\' -> {
  //           if(!wasSlash){
  //             wasSlash = true;
  //             index++;
  //             continue;
  //           } else {
  //             builder.append('\\').append('\\');
  //             index++;
  //           }
  //         }
  //         case 't','b','n','f','r','/' -> {
  //           if(wasSlash){
  //             wasSlash = false;
  //             builder.append('\\');
  //             index++;
  //           }
  //           builder.append((char)ch);
  //         }
  //         case 'u' -> {
  //           if(wasSlash){
  //             if(!IJsonSetting.DECODE_UNICODE_SEQUENCE){
  //               wasSlash = false;
  //               builder.append('\\').append('u');
  //               for(int i = 0; i < 4; i++){
  //                 int test = reader.read();
  //                 test = Character.toLowerCase(test);
  //                 index++;
  //                 if(test <= 'f' && test >= '0')
  //                   builder.append((char)test);
  //                 else
  //                   throw new JsonInvalidStringException("Expected hex digits, but found: "+test,reader,index+offset);
  //               }
  //             } else {
  //               char[] arr = new char[4];
  //               for(int i = 0; i < 4; i++){
  //                 int test = reader.read();
  //                 test = Character.toLowerCase(test);
  //                 if(test <= 'f' && test >= '0')
  //                   arr[i] = (char)test;
  //                 else
  //                   throw new JsonInvalidStringException("Expected hex digits, but found: "+test,reader,index+offset+i);
  //               }
  //               int symbol = Integer.parseInt(new String(arr),16);
  //               builder.append((char)symbol);
  //               index--;
  //               wasSlash = false;
  //             }
  //           } else 
  //             builder.append((char)ch);
  //         }
  //         case '\"' -> {
  //           if(wasSlash){
  //             builder.append('\\').append('\"');
  //             wasSlash = false;
  //           } else {
  //             value = builder.toString();
  //             return index+1;
  //           }
  //         }
  //         default -> {
  //           if(wasSlash){
  //             throw new JsonInvalidStringException("Invalid character after \\: "+(char)ch, reader, index + offset);
  //           } else {
  //             builder.append((char)ch);
  //           }
  //         }
  //       }
  //       index++;
  //     }
  //     throw new JsonParseException("Unexpected end of line", reader, index + offset - 1);
  //   }catch(IOException e){
  //     JsonParseException exp = new JsonParseException("IO error");
  //     exp.initCause(e);
  //     throw exp;
  //   }
  // }
