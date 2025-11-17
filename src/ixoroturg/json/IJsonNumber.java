package ixoroturg.json;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Writer;

public class IJsonNumber extends IJsonEntry<Double>{
  protected double value;
  String strValue;
  IJsonNumber(){}
  IJsonNumber(double value){
    this.value = value;
    strValue = String.valueOf(value);
  }

  private static boolean isDigit(char ch){
    ch = Character.toLowerCase(ch);
    return (ch >= '0') && (ch <= '9');
  }
  private static boolean isEnd(char ch){
    return IJsonSetting.isWhiteSpace(ch) || ch == ',' || ch == '}' || ch == ']';
  }

  @Override
  void parse(IJsonParseContext ctx) throws JsonInvalidNumberException, JsonParseException {
    strValue = validate(ctx);
    try{
      value = Double.parseDouble(strValue);
    }catch(NumberFormatException e){
      JsonInvalidNumberException exp = new JsonInvalidNumberException("Unexpected error");
      exp.initCause(e);
      throw exp;
    }
  }
  // static boolean wasExp = false;
  // static boolean wasDot = false;
  // static boolean shouldDot = false;
  // static boolean firstPass = true;
  static String validate(IJsonParseContext ctx) throws JsonInvalidNumberException, JsonParseException {
    // int i;
    if(ctx.firstPass){
      ctx.builder.setLength(0);
      ctx.firstPass = false;
      ctx.wasDot = false;
      ctx.shouldDot = false;
      ctx.wasExp = false;
    }
    for(; ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
      char ch = ctx.buffer[ctx.pointer];
      if(ch == 65535 || ch == 0)
        throw new JsonParseException("Unexpected end of line",ctx);
      if(isEnd(ch)){
        if(!isDigit(ctx.buffer[ctx.pointer-1]))
          throw new JsonInvalidNumberException("Unexpected end of line",ctx);
        // ctx.pointer = i;
        ctx.pointer--;
        ctx.firstPass = true;
        return ctx.builder.toString();
      }
      if(ctx.shouldDot && ch != '.' && ch != 'e' && ch != 'E')
        throw new JsonInvalidNumberException("After 0 must be dot or exponent",ctx);
      ctx.shouldDot = false;
      if(ctx.builder.length() == 0){
        if(!(isDigit(ch) || ch == '-'))
          throw new JsonInvalidNumberException("At first place must be digit or minus", ctx);
      }
      switch(ch){
        case '.' -> {
          if(ctx.wasDot)
            throw new JsonInvalidNumberException("Dot already was",ctx);
          ctx.wasDot = true;
          if(!isDigit(ctx.buffer[ctx.pointer-1]))
            throw new JsonInvalidNumberException("Before dot must be digit",ctx);
        }
        case 'e', 'E' -> {
          if(ctx.wasExp)
            throw new JsonInvalidNumberException("Exponent already was", ctx);
          ctx.wasExp = true;
          if(!isDigit(ctx.buffer[ctx.pointer-1]))
            throw new JsonInvalidNumberException("Before exponent must be digit",ctx);
        }
        case '0' -> {
          if(ctx.builder.length() == 0){
            ctx.shouldDot = true;
          } else if(ctx.builder.length() == 1 && ctx.buffer[ctx.pointer-1] == '-'){
            ctx.shouldDot = true;
          }
        }
        case '-','+' -> {
          if(ctx.builder.length() != 0){
            if(!(ctx.buffer[ctx.pointer] == 'e' || ctx.buffer[ctx.pointer] != 'E'))
              throw new JsonInvalidNumberException("Before "+ch+" should be exponent",ctx);
          } else if(ch == '+'){
            throw new JsonInvalidNumberException("At first place must be digit or minus", ctx);
          }
        }
        default -> {
          if(!isDigit(ch))
            throw new JsonInvalidNumberException("Unexpected symbol "+ch,ctx);
        }
      }
      ctx.builder.append(ch);
    }
    // ctx.pointer = i;
    ctx.read();
    return validate(ctx);
  }

  @Override
  public String toFormatedString() {
    return strValue;
  }

  @Override
  public int buffSize() {
    return strValue.length();
  }

  @Override
  public int buffSizeFormat() {
    return strValue.length();
  }

  @Override
  void toString(IJsonFormatContext ctx) throws IOException {
    if(IJsonSetting.SHOW_INNER_DOUBLE_VALUE)
      ctx.writer.write(String.valueOf(value));
    else 
      ctx.writer.write(strValue);
  }

  @Override
  int buffSize(IJsonFormatContext ctx) {
    return strValue.length();
  }

  @Override
  public String toString(){
    return strValue;
  }
}
  // int parse(Reader r, int offset) throws JsonParseException, JsonInvalidNumberException{
  //   PushbackReader reader = new PushbackReader(r);
  //   int ch;
  //   int index = 0;
  //   boolean wasExp = false;
  //   boolean wasDot = false;
  //   boolean start = false;
  //   int prevCh = -1;
  //   try{
  //     StringBuilder builder = new StringBuilder();
  //     while( !isEnd(ch = reader.read()) ){
  //
  //       if(IJsonSetting.isWhiteSpace(ch)){
  //         if(!start){
  //           index++;
  //           continue;
  //         } else {
  //           reader.unread(ch);
  //           return index;
  //         }
  //       } else {
  //         start = true;
  //       }
  //
  //       switch(ch){
  //         case -1 -> {
  //           throw new JsonParseException("Unexpected end of line", r, index+offset-1);
  //         }
  //         case 'e', 'E' -> {
  //           if(wasExp)
  //             throw new JsonInvalidNumberException("Symbol "+ch+" already was", reader, index + offset);
  //           wasExp = true;
  //           if(index == 0)
  //             throw new JsonInvalidNumberException("Symbol "+ch+" cannot be at first position", reader, index + offset);
  //           int test = reader.read();
  //           if(!(isDigit(test) || test == '+' || test == '-')){
  //             throw new JsonInvalidNumberException("After \"e\" or \"E\" should be digit or \"+\" or \"-\"",reader, index + offset);
  //           }
  //           reader.unread(test);
  //         }
  //         case '-' -> {
  //           if(index != 0){
  //             if(!(prevCh == 'e' || prevCh == 'E')) {
  //               throw new JsonInvalidNumberException("Unexpected \"-\"", reader, index + offset);
  //             }
  //             int test = reader.read();
  //             if(!isDigit(test)){
  //               throw new JsonInvalidNumberException("After \"-\" should be digit", reader, index + offset);
  //             }
  //             reader.unread(test);
  //           } else {
  //             int test = reader.read();
  //             if(!isDigit(test))
  //               throw new JsonInvalidNumberException("After \"-\" at first position should be digit");
  //             reader.unread(test);
  //           }
  //         }
  //         case '+' -> {
  //           if(!(prevCh == 'e' || prevCh == 'E')){
  //             throw new JsonInvalidNumberException("Unexpected \"+\"", reader, index + offset);
  //           }
  //           int test = reader.read();
  //           if(!isDigit(test))
  //             throw new JsonInvalidNumberException("After \"+\" should be digit", reader, index + offset);
  //           reader.unread(test);
  //         }
  //         case '.' -> {
  //           if(wasDot)
  //             throw new JsonInvalidNumberException("Symbol "+ ch+ " already was", reader ,index + offset);
  //           wasDot = true;
  //           if(index == 0)
  //             throw new JsonInvalidNumberException("Symbol "+ch+" cannot be at first position", reader, index + offset);
  //           int test = reader.read();
  //           if(!isDigit(test))
  //             throw new JsonInvalidNumberException("After \".\" should be digit", reader, index + offset);
  //           reader.unread(test);
  //         }
  //         default -> {
  //           if(isDigit(ch)){
  //             if(index == 0 && ch == '0'){
  //               int test = reader.read();
  //               if(!(isEnd(test) || test == '.' || test == 'e' || test == 'E')){
  //                 throw new JsonInvalidNumberException("After 0 at first position should be \".\" or \"e\" or \"E\"", reader, index + offset);
  //               }
  //               reader.unread(test);
  //             }
  //           } else {
  //             throw new JsonInvalidNumberException("Unexpected symbol \"" + ch +"\"", reader, index + offset);
  //           }
  //         }
  //       }
  //
  //       index++;
  //       prevCh = ch;
  //       builder.append(ch);
  //     }
  //
  //   reader.unread(ch);
  //   try{
  //     value = Double.parseDouble(builder.toString());
  //   }catch(NumberFormatException e){
  //     JsonParseException exp = new JsonParseException("Unexpected error");
  //     exp.initCause(e);
  //     throw exp;
  //   }
  //   return index;
  //
  //   }catch(IOException e){
  //     JsonParseException exp = new JsonParseException("IO error");
  //     exp.initCause(e);
  //     throw exp;
  //   }
  // }
