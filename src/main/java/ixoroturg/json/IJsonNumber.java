package ixoroturg.json;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class IJsonNumber extends IJsonEntry{
	protected double value = Double.NaN;
	String strValue;
	IJsonNumber(){}
	IJsonNumber(double value){
		this.value = value;
		strValue = String.valueOf(value);
	}

	private static boolean isDigit(byte ch){
		// ch = Character.toLowerCase(ch);
		return (ch >= '0') && (ch <= '9');
	}
	private static boolean isEnd(byte ch){
		return IJsonSetting.isWhiteSpace(ch) || ch == ',' || ch == '}' || ch == ']';
	}

	@Override
	void parse(IJsonParseContext ctx) throws JsonInvalidNumberException, JsonParseException {
		ByteBuffer result = validate(ctx);
		result.flip();
		byte[] res = new byte[result.limit()];
		result.get(res);
		strValue = new String(res, StandardCharsets.UTF_8);
		// strValue = validate(ctx);
		if(!ctx.USE_LAZY_NUMBER_PARSER){
			try{
				if(!ctx.USE_FAST_NUMBER_PARSE)
					value = Double.parseDouble(strValue);
				else
					value = ctx.numberValue;
			}catch(NumberFormatException e){
				JsonInvalidNumberException exp = new JsonInvalidNumberException("Unexpected error");
				exp.initCause(e);
				throw exp;
			}
		}
	}
	static ByteBuffer validate(IJsonParseContext ctx) throws JsonInvalidNumberException, JsonParseException {
		// int i;
		if(ctx.firstPass){
			// ctx.builder.setLength(0);
		ctx.builder.reset();
			ctx.firstPass = false;
			ctx.wasDot = false;
			ctx.shouldDot = false;
			ctx.wasExp = false;
			ctx.unicode = 0; // число после экспоненты
			ctx.wasSlash = false; // был ли минус
			ctx.fracSize = 0;
			ctx.numberValue = 0;
			ctx.wasMinus = false;
			ctx.zeroCount = 0;
			ctx.wasDigit = false;
		}
	for(; ctx.buffer.capacity() < ctx.buffer.limit(); ctx.index++, ctx.column++){
		// for(; ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
	
			// char ch = ctx.buffer[ctx.pointer];
			byte ch = ctx.buffer.get();
			if(isEnd(ch)){
				if(!ctx.wasDigit)
					throw new JsonInvalidNumberException("Unexpected end of line",ctx);
				ctx.builder.position(ctx.builder.position() - 1);
				// ctx.pointer--;
				ctx.firstPass = true;
				if(ctx.wasSlash){
					ctx.unicode = -ctx.unicode;
				}
				ctx.wasSlash = false;
				ctx.unicode += ctx.fracSize;
				if(ctx.wasMinus)
					ctx.numberValue = -ctx.numberValue;
				ctx.numberValue *= Math.pow(10,ctx.unicode);
				return ctx.buffer;
				// return ctx.builder.toString();
			}
			if(ctx.shouldDot && ch != '.' && ch != 'e' && ch != 'E')
				throw new JsonInvalidNumberException("After 0 must be dot or exponent",ctx);
			ctx.shouldDot = false;
			if(ctx.builder.position() == 0){
				if(!(isDigit(ch) || ch == '-'))
					throw new JsonInvalidNumberException("At first place must be digit or minus", ctx);
			}
			switch(ch){
				case '.' -> {
					if(ctx.wasDot)
						throw new JsonInvalidNumberException("Dot already was",ctx);
					if(!ctx.wasDigit)
						throw new JsonInvalidNumberException("Before dot must be digit",ctx);
					ctx.wasDot = true;
					if(ctx.wasSlash){
						ctx.wasSlash = false;
					}
				}
				case 'e', 'E' -> {
					if(ctx.wasExp)
						throw new JsonInvalidNumberException("Exponent already was", ctx);
					ctx.wasExp = true;
					ctx.wasDot = true;
					if(!ctx.wasDigit)
						throw new JsonInvalidNumberException("Before exponent must be digit",ctx);
				}
				case '0' -> {
					if(ctx.builder.position() == 0){
						ctx.shouldDot = true;
					} else if(ctx.builder.position() == 1 && ctx.wasMinus){
						ctx.shouldDot = true;
					}
				}
				case '-','+' -> {
					if(ctx.builder.position() != 0){
						if(!ctx.wasExpSign)
							throw new JsonInvalidNumberException("Before "+ch+" should be exponent",ctx);
					} else {
						ctx.wasMinus = true;
					}
					if(ch == '-' && ctx.wasDot){ 
						ctx.wasSlash = true;
					}
				}
				default -> {
					if(!isDigit(ch))
						throw new JsonInvalidNumberException("Unexpected symbol "+ch,ctx);
				}
			}
			ctx.buffer.put(ch);
			
			if(!ctx.USE_LAZY_NUMBER_PARSER && ctx.USE_FAST_NUMBER_PARSE	&& isDigit(ch)){
				if(!ctx.wasExp){
					ctx.numberValue = ctx.numberValue * 10 + ch - '0';
					if(ctx.wasDot)
						ctx.fracSize--;
				} else {
					if((ctx.unicode & 0xffff) < 1000)
						ctx.unicode = ctx.unicode * 10 + ch - '0';
				}
			}
			ctx.wasDigit = isDigit(ch);
			ctx.wasExpSign = ch == 'e' || ch == 'E';
		}
		ctx.read();
		if(ctx.buffer.position() == ctx.buffer.limit()){
			throw new JsonParseException("Unexpected end of line",ctx);
		}
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
		if(ctx.SHOW_INNER_DOUBLE_VALUE){
			if(Double.isNaN(value))
				value = Double.parseDouble(strValue);
			ctx.writer.write(String.valueOf(value));
		}
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
	@Override
	public boolean equals(Object obj){
		if(obj instanceof IJsonNumber num){
			return value == num.value;
		}
		return false;
	}
	@Override
	public IJsonEntry clone(){
		IJsonNumber js = new IJsonNumber(value);
		return js;
	}
}
