package ixoroturg.json.spi;

import java.io.IOException;
// import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static ixoroturg.json.spi.IJsonParseContext.*;

public class IJsonNumber extends IJsonEntry{
	// protected double value = Double.NaN;

	byte sign;
	short exp;
	int[] module;

	double dobval = Double.NaN;
	long intval = 0;
	
	byte[] strValue;

	IJsonNumber(){}
	IJsonNumber(double value){
		dobval = value;
		sign = (byte)((sign & ~SIGN_MASK)+DOUBLE);
		// strValue = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
	}
	IJsonNumber(long value){
		intval = value;
		sign = (byte)((sign & ~SIGN_MASK)+DOUBLE);
	}
	// IJsonNumber(BigDecimal value){
	// 	exp = (short)value.scale();
	// 	byte sign = (byte)value.signum();
	// 	// value.abs().toPlainString().
	// 	// TODO:
	// 	sign = (byte)((sign & ~SIGN_MASK)+DECIMAL);
	// }

	private static boolean isDigit(byte ch){
		// ch = Character.toLowerCase(ch);
		return (ch >= '0') && (ch <= '9');
	}
	private static boolean isEnd(byte ch){
		return IJsonSetting.isWhiteSpace(ch) || ch == ',' || ch == '}' || ch == ']';
	}

	static final byte DECIMAL = 0x00, INTEGER = 0x01, DOUBLE = 0x02, TYPE_MASK = 0x0F, SIGN_MASK = (byte)0xF0;
	private byte[] getStrValue(){
		if(strValue != null){
			return strValue;
		}
		if((sign & TYPE_MASK) == DOUBLE){
			strValue = String.valueOf(dobval).getBytes(StandardCharsets.UTF_8);
		}
		if((sign & TYPE_MASK) == INTEGER){
			// byte[] res = new byte[20 + 1];
			byte[] tmp = new byte[20];
			int l = 0;
			long n = intval;
			for(int i = 0; i < tmp.length; i++){
				byte r = (byte)(n % 10);
				r = (byte)(r + '0');
				tmp[i++] = r;
				if(n == 0l){
					l = i;
					break;
				}
			}
			int len = l;
			if(n < 0){
				len++;
			}
			byte[] res = new byte[len];
			if(n < 0){
				res[0] = '-';
				len--;
			}
			for(int i = 0; i < len; i++){
				res[len--] = tmp[i];
			}
			strValue = res;
			return strValue;
		}
		if((sign & TYPE_MASK) == DECIMAL){
			// System.out.println("Число: DECIMAL");
			int[] module = new int[this.module.length];
			System.arraycopy(this.module,0,module,0,this.module.length);
			byte[] value = new byte[1 + 10*module.length + 5 + 1 + 1]; // - + 10*int (может дать до 10 знаков) + 5 от short (экспонента) + e + - от e
			int len = 0;
			if((sign & 0xF0) == 0xF0){
				value[len++] = '-';
			}
			int l = 0;
			byte[] mod = new byte[10*module.length];
			for(int i = 0; i < 10*module.length; i++){
				
				byte ch = div(module);
				// System.out.println("Получена цифра: "+ch);
				if(ch == -1){
					if(i == 0){
						l = 1;
						mod[i] = '0';
					} else {
						l = i;
					}
					break;
				}
				ch = (byte)(ch + '0');
				mod[i] = ch;
			}
			for(int i = l-1; i >= 0; i--){
				value[len++] = mod[i];
			}
			if(exp != 0){
				value[len++] = 'e';
				if(exp < 0){
					value[len++] = '-';
				}
				l = 0;
				short exp = this.exp;
				if(exp < 0){
					exp = (short)(-exp);
				}
				for(int i = 0; i < 5 ;i++){ // размер short
					byte r = (byte)(exp % 10);
					// System.out.println("Exp цифра: "+r);
					r = (byte)(r + '0');
					exp = (short)(exp / 10);
					mod[i] = r;
					if(exp == 0){
						l = i;
						break;
					}
				}
				for(int i = l; i >= 0; i-- ){
					value[len++] = mod[i];
				}
			}

			byte[] result = new byte[len];
			System.arraycopy(value,0,result,0,len);
			// System.out.println("Число: "+debugString());
			// System.out.println("Строка: "+new String(result));
			strValue = result;
		}
		return strValue;
	}
	private static byte div(int[] module){
		if(module.length == 1){
			int t = module[0];
			if(t == 0){
				return -1;
			}
			module[0] = t / 10;
			byte res = (byte)(t % 10);
			return res;
		}
		if(module.length == 2){
			long t = module[1] & 0x00000000FFFFFFFFL;
			t = t << 32;
			t += module[0] & 0x00000000FFFFFFFFL;
			if(t == 0){
				return -1;
			}
			byte res = (byte)(t % 10);
			t = t / 10;
			module[0] = (int)t;
			module[1] = (int)(t>>32);
			return res;
		}
		return 0;
	}

	@Override
	void parse(IJsonParseContext ctx) throws IJsonInvalidNumberException, IJsonParseException {
		validate(ctx);
		sign = ctx.sign;
		exp = ctx.exp;
		module = new int[ctx.modlen];
		System.arraycopy(ctx.module, 0, module, 0, ctx.modlen);
		// System.out.println("Получено при парсинге: "+debugString());
		// return;
		// strValue = validate(ctx);
		// if(!ctx.USE_LAZY_NUMBER_PARSER){
		// 	try{
		// 		if(!ctx.USE_FAST_NUMBER_PARSE){
		// 			String t = new String(strValue,StandardCharsets.UTF_8);
		// 			value = Double.parseDouble(t);
		// 		}
		// 		else
		// 			value = ctx.numberValue;
		// 	}catch(NumberFormatException e){
		// 		JsonInvalidNumberException exp = new JsonInvalidNumberException("Unexpected error");
		// 		exp.initCause(e);
		// 		throw exp;
		// 	}
		// }
	}
	static void validate(IJsonParseContext ctx) throws IJsonInvalidNumberException, IJsonParseException {
		// int i;
		// System.out.print("Парсим число:");
		// for(int i = ctx.pointer; i < ctx.pointer+16 && i < ctx.buffer.length; i++){
		// 	System.out.print((char)ctx.buffer[i]);
		// }
		// System.out.println();
		if((ctx.flag & firstPass) == 0){
			byte ch = ctx.buffer[ctx.pointer];
			if(!(isDigit(ch) || ch == '-'))
				throw new IJsonInvalidNumberException("At first place must be digit or minus", ctx);
			// if(ch == '-'){
			// 	ctx.sign = (byte)0xF0;
			// }
			// ctx.pointer++;
			// ctx.builder.reset();
			// ctx.firstPass = false;
			ctx.flag = firstPass;
			// ctx.flag = (byte)(firstPass);
			ctx.sign = 0;
			ctx.exp = 0;
			ctx.dotPos = 0;
			ctx.modlen=1;
			ctx.module[0] = 0;
			// ctx.wasDot = false;
			// ctx.shouldDot = false;
			// ctx.wasExp = false;
			// ctx.wasSlash = false; 
			//Флаг wasSlash = был ли минус
			// ctx.unicode = 0; // число после экспоненты
			// ctx.fracSize = 0;
			// ctx.numberValue = 0;
			// ctx.wasMinus = false;
			// ctx.zeroCount = 0;
		}
		for(; ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
			byte ch = ctx.buffer[ctx.pointer];
			if(ch == -1)
				throw new IJsonParseException("Unexpected end of line",ctx);
			if(isEnd(ch)){
				if(!isDigit(ctx.buffer[ctx.pointer-1]))
					throw new IJsonInvalidNumberException("Unexpected end of line",ctx);
				ctx.pointer--;
				// ctx.firstPass = true;
				ctx.flag = (byte)(ctx.flag | firstPass);
				// System.out.println("Экспонента: "+ctx.exp+", знак: "+Integer.toHexString(ctx.sign));
				if((ctx.sign & 0x0F) == 0x0F){
					ctx.exp = (short)(-ctx.exp);
					ctx.sign = (byte)(ctx.sign & 0xF0);
				}
				// System.out.println("Экспонента (корректировка): "+ctx.exp+", знаков после запятой: "+ctx.dotPos);

				ctx.exp += ctx.dotPos;
				// if(ctx.sign < 0){
				// 	ctx.sign=(byte)0xF0;
				// }
				ctx.sign += DECIMAL;
				ctx.flag = (byte)(ctx.flag & ~firstPass);
				// wasSlash - был ли минус
				// if((ctx.flag & wasMinus) != 0){
				// 	ctx.unicode = -ctx.unicode;
				// }
				// ctx.wasSlash = false;
				// ctx.unicode += ctx.fracSize;
				// if(ctx.wasMinus)
				// 	ctx.numberValue = -ctx.numberValue;
				// ctx.numberValue *= Math.pow(10,ctx.unicode);
				// return ctx.builder.toStringB();
				// ctx.pointer--;
				return;
			}
			if((ctx.flag & wasDot) != 0 && ch != 'e' && ch != 'E'){
				ctx.dotPos--;
			}
			// ctx.shouldDot = false;
			// if((ctx.flag & firstPass) == 0){
			// }
			if((ctx.flag & shouldDot) != 0 && ch != '.' && ch != 'e' && ch != 'E'){
				throw new IJsonInvalidNumberException("After 0 must be dot or exponent",ctx);
			}
			switch(ch){
				case '.' -> {
					if((ctx.flag & wasDot) != 0){
						throw new IJsonInvalidNumberException("Dot already was",ctx);
					}
					if(!isDigit(ctx.buffer[ctx.pointer-1])){
						throw new IJsonInvalidNumberException("Before dot must be digit",ctx);
					}
					ctx.flag = (byte)((ctx.flag | wasDot) & ~shouldDot);
					continue;
					// if(ctx.wasSlash){
					// 	ctx.wasSlash = false;
					// }
				}
				case 'e', 'E' -> {
					if((ctx.flag & wasExp) != 0){
						throw new IJsonInvalidNumberException("Exponent already was", ctx);
					}
					if(!isDigit(ctx.buffer[ctx.pointer-1])){
						throw new IJsonInvalidNumberException("Before exponent must be digit",ctx);
					}
					ctx.flag = (byte)((ctx.flag | wasExp) & ~wasDot);
					continue;
				}
				case '0' -> {
					if((ctx.flag & firstPass) == 0 || ctx.buffer[ctx.pointer-1] == '-'){
						ctx.flag = (byte)(ctx.flag | shouldDot);
					}
					// } else if(ctx.builder.length == 1 && ctx.buffer[ctx.pointer-1] == '-'){
					// 	ctx.shouldDot = true;
					// }
				}
				case '+' -> {
					// if(ctx.pointer == 0){
					// 	throw new JsonInvalidNumberException("+ cannot be the first character",ctx);
					// }
					if(!(ctx.buffer[ctx.pointer-1] == 'e' || ctx.buffer[ctx.pointer] == 'E') ){
						throw new IJsonInvalidNumberException("Before + should be exponent", ctx);
					}
					continue;
				}
				case '-' -> {
					// if(ctx.builder.length != 0){
					if((ctx.flag & firstStep) == 0){
						ctx.sign = (byte) 0xF0;
						ctx.flag = (byte)(ctx.flag | firstStep);
						continue;
					}
					if(!(ctx.buffer[ctx.pointer-1] == 'e' || ctx.buffer[ctx.pointer-1] == 'E')){
						throw new IJsonInvalidNumberException("Before "+(char)ch+" should be exponent",ctx);
					} else {
						// ctx.flag = (byte)(ctx.flag | wasMinus);
						// System.out.println("Знак до: "+Integer.toHexString(ctx.sign));
						ctx.sign += 0x0F;
						// System.out.println("Знак после: "+Integer.toHexString(ctx.sign));
					}
					continue;
				}
				default -> {
					if(!isDigit(ch))
						throw new IJsonInvalidNumberException("Unexpected symbol "+ch,ctx);
				}
			}

			// ctx.builder.append(ch);

			if((ctx.setting & USE_DECIMAL) != 0){
				if((ctx.flag & wasExp) == 0){
					addNumber(ctx,ch);
					// System.out.println("Добавлена цифра "+(char)(ch)+", стало: "+Arrays.toString(ctx.module));
				} else {
					ctx.exp *= 10;
					ctx.exp += ch - '0';
					// System.out.println("Добавлена экспонента "+(char)(ch)+", стало: "+ctx.exp);
				}
			}

			// if(!ctx.USE_LAZY_NUMBER_PARSER && ctx.USE_FAST_NUMBER_PARSE  && isDigit(ch)){
			// 	if((ctx.flag & wasExp) == 0){
			// 		ctx.numberValue = ctx.numberValue * 10 + ch - '0';
			// 		if((ctx.flag & wasDot) != 0)
			// 			ctx.fracSize--;
			// 	} else {
			// 		if((ctx.unicode & 0xffff) < 1000)
			// 			ctx.unicode = ctx.unicode * 10 + ch - '0';
			// 	}
			// }
			ctx.flag = (byte)(ctx.flag | firstStep);
		}
		ctx.read();
		validate(ctx);
	}

	// We got number symbol
	private static void addNumber(IJsonParseContext ctx, byte number){
		// number -= '0';
		// System.out.println("addNumber: "+(char)number);
		long v = 0;
		int add = 0;
		for(int i = 0; i < ctx.modlen; i++){
			v = ctx.module[i] & 0x00000000FFFFFFFFL;
			// System.out.println("Взято long: "+v);
			v *= 10;
			// System.out.println("Увеличено на 10: "+v);
			v += add;
			// System.out.println("Прибавлено: "+add);
			if(i == 0){
				v += number - '0';
				// System.out.println("Т.к. первый int[0], то прибавлено само число "+(number-'0'));
			}
			ctx.module[i] = (int)v;
			// System.out.println("Новый модуль числа на "+i+" позиции: "+ctx.module[i]);
			add = (int)(v >> 32);
			// System.out.println("Новый add: "+add);
		}
		if(add != 0){
			// System.out.println("add не пустой, но место кончилось");
			if(ctx.modlen == ctx.module.length){
				// System.out.println("Полностью расширяем массив");
				int[] arr = new int[ctx.module.length+1];
				System.arraycopy(ctx.module, 0, arr, 0, ctx.modlen);
				// arr[arr.length-1] = add;
				ctx.module = arr;
			}
			ctx.module[ctx.modlen] = add;
			// System.out.println("Последний int[]: "+add);
			ctx.modlen++;
		}
	}

	@Override
	public String toFormatedString() {
		return new String(getStrValue(),StandardCharsets.UTF_8);
	}

	@Override
	public int buffSize() {
		return getStrValue().length;
	}

	@Override
	public int buffSizeFormat() {
		return getStrValue().length;
	}

	@Override
	void toString(IJsonFormatContext ctx) throws IOException {
		ctx.writer.write(getStrValue());
		// if(ctx.SHOW_INNER_DOUBLE_VALUE){
		// 	if(Double.isNaN(value)){
		// 		value = Double.parseDouble(new String(strValue,StandardCharsets.UTF_8));
		// 		strValue = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
		// 		ctx.writer.write(strValue);
		// 	} else {
		// 		// strValue = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
		// 		ctx.writer.write(strValue);
		// 	}
		//
		// }
		// else 
		// 	ctx.writer.write(strValue);
	}

	@Override
	int buffSize(IJsonFormatContext ctx) {
		return getStrValue().length;
	}

	@Override
	public String toString(){
		return new String(getStrValue(),StandardCharsets.UTF_8);
	}
	// private void stringToValue(){
	// 	String str = new String(strValue,StandardCharsets.UTF_8);
	// 	value = Double.parseDouble(str);
	// }
	@Override
	public boolean equals(Object obj){
		if(obj instanceof IJsonNumber num){
			if((sign & 0x0F) == INTEGER && (num.sign & 0x0F) == INTEGER){
				long t1 = IJson.getInnerLong(this);
				long t2 = IJson.getInnerLong(num);
				return t1 == t2;
			}
			if((sign & 0x0F) == DOUBLE || (num.sign & 0x0F) == DOUBLE){
				double d1 = IJson.getInnerDouble(this);
				double d2 = IJson.getInnerDouble(num);
				return d1 == d2;
			}
			if(sign == num.sign && exp == num.exp && module.length == num.module.length){
				for(int i = 0; i < module.length; i++){
					if(module[i] != num.module[i]){
						return false;
					}
				}
				return true;
			}
			// if(Double.isNaN(value)){
			// 	stringToValue();
			// }
			// if(Double.isNaN(num.value)){
			// 	num.stringToValue();
			// }
			// return value == num.value;
		}
		return false;
	}
	@Override
	public IJsonEntry clone(){
		IJsonNumber js = new IJsonNumber();
		js.sign = sign;
		js.exp = exp;
		js.module = new int[module.length];
		System.arraycopy(module,0,js.module,0,module.length);
		return js;
	}
	public String debugString(){
		String res = "sign: "+Integer.toHexString(sign)+", exp: "+exp + ", module: "+Arrays.toString(module);
		return res;
		
	}
}
