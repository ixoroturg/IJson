package ixoroturg.json.spi;

import java.util.Map;

import ixoroturg.json.spi.IJsonParseContext.ByteBuilder;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class IJsonObject extends IJsonEntry {
	Map<String, IJsonEntry> map = new HashMap<>();

	String key;
	boolean needKey = true;
	boolean firstPass = true;
	boolean needQuote = false;
	boolean wasQuote = false;
	boolean needDot = false;
	boolean wasDot = false;

	IJsonObject(){}
	IJsonObject(Map<String, IJsonEntry> map){
		for(Map.Entry<String, IJsonEntry> entry: map.entrySet()){
			this.map.put(entry.getKey(), entry.getValue().clone());
		}
	}
	@Override
	void parse(IJsonParseContext ctx) throws IJsonParseException, IJsonInvalidObjectException, IJsonInvalidStringException, IJsonInvalidNumberException, IJsonInvalidBooleanException, IJsonInvalidArrayException{
		// System.out.println("Начало IJsonObject");
		for(;ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
			byte ch = ctx.buffer[ctx.pointer];
			if(IJsonUtil.isWhiteSpace(ch)){
				if(ch == '\n'){
					ctx.row++;
					ctx.column = 0;
				}
				continue;
			}
			if(ch == '{' && firstPass){
				// System.out.println("Начало с {");
				firstPass = false;
				continue;
			}
			if(ch == '}'){
				if(wasQuote)
					throw new IJsonParseException("Unexpected end of line",ctx);
				return;
			}
			if(ch == -1){
				throw new IJsonParseException("Unexpected end of line",ctx);
			}
			if(needKey && ch != '\"'){
				throw new IJsonInvalidObjectException("Expected \" but found "+(char)ch, ctx);
			}
			if(needQuote && ch != ':'){
				// System.out.println("needQuote = "+needQuote );
				throw new IJsonInvalidObjectException("Expected : but found "+(char)ch, ctx);
			}
			if(needDot && ch != ','){
				throw new IJsonInvalidObjectException("Expected , but found "+(char)ch, ctx);
			}
			switch(ch){
				case ',' -> {
					// System.out.println("Обнаружена запятая");
					if(wasDot)
						throw new IJsonInvalidObjectException("Unexpected second ,", ctx);
					wasDot = true;
					needDot = false;
					needKey = true;
				}
				case '\"' -> {
					// System.out.println("Обнаружена \"");
					if(needKey){
						// System.out.println("Парсинг ключа на "+ctx.pointer);
						ByteBuilder result = IJsonString.validate(ctx);
						// ByteBuilder result = IJsonString.validate(ctx);
						key = new String(result.toStringB(),StandardCharsets.UTF_8);
						// System.out.println("Ключ получен: "+key);
						needKey = false;
						// System.out.println("Установка needQuote в true");
						needQuote = true;
					} else {
						// System.out.println("Добавление строки");
						addEntry(new IJsonString(), ctx);
					}
				}
				case ':' -> {
					// System.out.println("Обнаружено :");
					if(wasQuote){
						throw new IJsonInvalidObjectException("Unexpected second :",ctx);
					}
					wasQuote = true;
					needQuote = false;
					// System.out.println(": = "+needQuote);
					continue;
				}
				case 'n' -> {
					if(!IJsonUtil.testNull(ctx))
						throw new IJsonParseException("Expected null", ctx);
					ctx.pointer--;
					addEntry(null,ctx);
				}

				case '{' -> {
					addEntry(new IJsonObject(), ctx);
				}
				case '[' -> {
					addEntry(new IJsonArray(), ctx);
				}
				case 't', 'f' -> {
					addEntry(new IJsonBoolean(), ctx);
				}

				default -> {
					addEntry(new IJsonNumber(),ctx);
				}
			}
		}
		ctx.read();
		parse(ctx);
	}

private int addEntry(IJsonEntry value, IJsonParseContext ctx) throws IJsonInvalidObjectException, IJsonInvalidStringException, IJsonInvalidNumberException, IJsonInvalidBooleanException, IJsonParseException{
	// System.out.println("Добавление: "+key+", pointer: "+ctx.pointer);
	wasQuote = false;
	needDot = true;
	// needQuote = false;
	wasDot = false;
	if(value != null)
		value.parse(ctx);
	map.put(key,value);
	if(value != null)
		value.parent = this;
	// System.out.println("Конечный pointer: "+ctx.pointer);
	return ctx.pointer;
}

@Override
public String toString(){
	IJsonFormatContext ctx = IJsonFormatContext.openContext(null);
	ctx.format = false;
	ByteArrayOutputStream writer = new ByteArrayOutputStream(buffSize(ctx));
	ctx.writer = writer;
	try{
		toString(ctx);
	}catch(IOException e){}
	String result = writer.toString(StandardCharsets.UTF_8);
	try{
		ctx.close();

	}catch(IOException e){}
	return result;
}
@Override
public String toFormatedString() {
	IJsonFormatContext ctx = IJsonFormatContext.openContext(null);
	ctx.format = true;
	ByteArrayOutputStream writer = new ByteArrayOutputStream(buffSize(ctx));
	ctx.writer = writer;

	try{
		toString(ctx);
	} catch(IOException e){}
	String result = writer.toString(StandardCharsets.UTF_8);
	try{
		ctx.close();
	} catch(IOException e){}
	return result;
}

@Override
public int buffSize() {
	if(map.size() == 0)
		return 2;
	int contentLength = 0;
	for(Map.Entry<String, IJsonEntry> entry : map.entrySet()){
		contentLength += entry.getKey().length() + 2;
		IJsonEntry test = entry.getValue();
		if(test == null)
			contentLength +=4;
		else
			contentLength += test.buffSize();
	}
	return contentLength + map.size() * 2 + 1 ;
}

@Override
public int buffSizeFormat() {
	IJsonFormatContext ctx = IJsonFormatContext.openContext(null);
	int size = buffSize(ctx);
	try{
		ctx.close();
	}catch(IOException e){}
	return size;
}

@Override
void toString(IJsonFormatContext ctx) throws IOException{
	if(map.size() == 0){
		ctx.writer.write("{}".getBytes(StandardCharsets.UTF_8));
		return;
	}
	ctx.depth++;
	ctx.writer.write('{');
	boolean first = true;
	IJsonEntry test;
	for(Map.Entry<String, IJsonEntry> entry: map.entrySet()){
		if(!first){
			ctx.writer.write(',');
			// if(ctx.format)
		}
		first = false;
		if(ctx.format){
			ctx.writer.write('\n');
			for(int j = 0; j < ctx.FORMAT_INDENT_COUNT * ctx.depth; j++){
				ctx.writer.write(ctx.FORMAT_INDENT_SYMBOL);
			}
		}

		ctx.writer.write('\"');
		ctx.writer.write(entry.getKey().getBytes(StandardCharsets.UTF_8));
		ctx.writer.write("\":".getBytes(StandardCharsets.UTF_8));
		if(ctx.format)
			ctx.writer.write(' ');
		test = entry.getValue();
		if(test == null)
			ctx.writer.write("null".getBytes(StandardCharsets.UTF_8));
		else
			test.toString(ctx);
	}
	ctx.depth--;
	if(ctx.format){
		ctx.writer.write('\n');
		for(int j = 0; j < ctx.FORMAT_INDENT_COUNT * ctx.depth; j++){
			ctx.writer.write(ctx.FORMAT_INDENT_SYMBOL);
		}
	}
	ctx.writer.write('}');
}

@Override
int buffSize(IJsonFormatContext ctx) {
	if(map.size() == 0)
		return 2;

	int result = 2;
	result += map.size() * 2 - 1;
	if(ctx.format){
		result += 2 + map.size() * (1 + ctx.FORMAT_INDENT_COUNT * (ctx.depth + 1)) + ctx.depth*ctx.FORMAT_INDENT_COUNT;
	}
	int contentLength = 0;
	ctx.depth++;
	for(Map.Entry<String, IJsonEntry> entry: map.entrySet()){
		contentLength += entry.getKey().length() + 2;
		IJsonEntry test = entry.getValue();
		if(test == null)
			contentLength +=4;
		else
			contentLength += test.buffSize(ctx);
	}
	ctx.depth--;
	result += contentLength;
	return result;
}

@Override
public IJsonEntry clone(){
	IJsonObject js = new IJsonObject(map);
	return js;
}
@Override
public boolean equals(Object obj){
	if(obj instanceof IJsonObject o){
		if(map.size() != o.map.size())
			return false;
		int i = 0;
		for(Map.Entry<String,IJsonEntry> entry: o.map.entrySet()){
			IJsonEntry test = map.get(entry.getKey());
			if(test == null && entry.getValue() != null){
				return false;
			}
			if(test != null && !test.equals(entry.getValue())){
				return false;
			}
			i++;
		}
		if(i != map.size())
			return false;
		return true;
	}
	return false;
}
}
