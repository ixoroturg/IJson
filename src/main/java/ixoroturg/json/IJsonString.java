package ixoroturg.json;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import ixoroturg.json.IJsonParseContext.ByteBuilder;

class IJsonString extends IJsonEntry{
	byte[] value;
	// int len = -1;
	// private static Field sizeField = null;
	// static{
	// 	try{
	// 		sizeField = String.class.getDeclaredField("value");
	// 		sizeField.setAccessible(true);
	// 	}catch(NoSuchFieldException e){
	// 		e.printStackTrace();
	// 	}
	// }
  IJsonString(){}
  IJsonString(byte[] value){
	  this.value = value;
  }
  IJsonString(String value){
	  if(value == null){
		  this.value = "null".getBytes(StandardCharsets.UTF_8);
	} else
    this.value = value.getBytes(StandardCharsets.UTF_8);
  }
  @Override
  public String toString(){
    if(value == null)
      return null;
    StringBuilder builder = new StringBuilder(value.length + 2);
    builder.append('\"')
      .append(value)
      .append('\"');
    return builder.toString();
  }
  @Override
  void toString(IJsonFormatContext ctx) throws IOException {
    if(value == null){
      ctx.writer.write("null".getBytes(StandardCharsets.UTF_8));
      return;
    }
    if(!ctx.FORMAT_DIRECT_WRITE_CONTROL_CHARACTER){
      ctx.writer.write('\"');
      ctx.writer.write(value);
      ctx.writer.write('\"');
      return;
    }
    ctx.writer.write('\"');
    boolean wasSlash = false;
    for(int i = 0; i < value.length; i++){
      if(wasSlash){
        switch(value[i]){
          case 't' -> {ctx.writer.write('\t');}
          case 'r' -> {ctx.writer.write('\r');}
          case 'n' -> {ctx.writer.write('\n');}
          case 'f' -> {ctx.writer.write('\f');}
          case 'b' -> {ctx.writer.write('\b');}
          default -> {ctx.writer.write('\\'); ctx.writer.write(value[i]);}
        }
        wasSlash = false;
        continue;
      }
      if(value[i] == '\\'){
        if(!wasSlash){
          wasSlash = true;
          continue;
        }
      }
      ctx.writer.write(value[i]);
    }
    ctx.writer.write('\"');
  }
  private int getLength(){
  	if(value == null){
		return 4;
	}
	// if(len == -1){
	return value.length;
		// len = 0;
		// for(int i = 0; i < value.length(); i++){
		// 	char ch = value.charAt(i);
		// 	if(ch <= 0x7F){
		// 		len++;
		// 	} else if(ch <= 0x7FF){
		// 		len +=2;
		// 	} else if(Character.isSurrogate(ch)){
		// 		len += 4;
		// 		i++;
		// 	} else {
		// 		len +=3;
		// 	}
		// }
		// try{
		//
		// 	byte[] b = (byte[]) sizeField.get(value);
		// 	len = b.length;
		// }catch(IllegalAccessException e){
		// 	e.printStackTrace();
		// }
	// }
	//
	//    return len;
  }

  @Override
  public int buffSize(){
	return getLength()+2;
  }
  @Override
  public int buffSizeFormat(){
    return getLength()+2;
  }
  @Override
  public String toFormatedString(){
	  return new String(value,StandardCharsets.UTF_8);
    // return toStringB());
  }
  public boolean equals(Json json) {
    if(json instanceof IJsonString str){
      return equals(str.value);
    }
    return false;
  }

  public boolean equals(byte[] str) {
    if(value == null && str == null)
      return true;
    if(value == null || str == null)
      return false;
	if(value.length != str.length){
		return false;
	}
	for(int i = 0; i < value.length; i++){
		if(value[i] != str[i])
			return false;
	}
	return true;
  }

  @Override
  void parse(IJsonParseContext ctx) throws JsonParseException, JsonInvalidStringException {
      ByteBuilder result = validate(ctx);
      value = result.toStringB();
  }
  
  static ByteBuilder validate(IJsonParseContext ctx) throws JsonParseException, JsonInvalidStringException{
    if(ctx.firstPass){
      ctx.builder.reset();
      ctx.column++;
      ctx.index++;
      ctx.pointer++;
      ctx.firstPass = false;
	  ctx.wasSlash = false;
    }
    for(; ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
      byte ch = ctx.buffer[ctx.pointer];
      
      switch(ch){
        case -1 -> {
          throw new JsonParseException("Unexcepted end of line", ctx);
        }
        case 'n' -> {
          if(ctx.wasSlash){
            ctx.wasSlash = false;
          }
        }
        case '\t', '\n', '\r', '\b', '\f' -> {
          if(!ctx.ESCAPE_CONTROL_CHARACTERS){
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
              // ch = Character.toLowerCase(ch);
			  if(ch >= 'A' && ch <= 'F'){
				  ch -= 'A'-'a';
			  }
              if((ch < '0' || ch > '9') && (ch < 'a' || ch > 'f'))
                throw new JsonInvalidStringException("After \\u should be 4 hex digits",ctx);
              if(ctx.hex == 0 && !ctx.DECODE_UNICODE_SEQUENCE)
                ctx.builder.append('u');

              if(ctx.DECODE_UNICODE_SEQUENCE){
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
                      // ctx.builder.setCharAt(ctx.builder.length - 1, (char) ctx.unicode);
					  ctx.builder.value[ctx.builder.length - 1] = (byte)ctx.unicode;
					  ctx.builder.write((byte)(ctx.unicode >> 8));
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
	return getLength()+2;
    // return value.length()+2;
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
