package ixoroturg.json;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

class IJsonBoolean extends IJsonEntry{
	boolean value;
	private static final char[] t = {'t', 'r', 'u', 'e'};
	private static final char[] f = {'f', 'a', 'l', 's', 'e'};
	IJsonBoolean(){}
	IJsonBoolean(boolean value){
		this.value = value;
	}

	@Override
	void parse(IJsonParseContext ctx) throws JsonParseException, JsonInvalidBooleanException{
		value = validate(ctx);
		ctx.pointer--;
	}
	static boolean validate(IJsonParseContext ctx) throws JsonParseException, JsonInvalidBooleanException {
		
		if(ctx.buffer.remaining() < 6){
		int remaining = 6 - ctx.buffer.remaining();
			ctx.builder.clear();
			ctx.builder.put(ctx.buffer);
			ctx.read();
			int limit = ctx.builder.limit();
			ctx.buffer.limit(remaining);
			ctx.builder.put(ctx.buffer);
			ctx.buffer.limit(limit);
		}

		// if(ctx.buffer.length - ctx.pointer < 6){
		// 	ctx.read();
		// }

		// final long trueTest = (byte)',' << 32 + (byte)'e' << 24 + (byte)'u' << 16 + (byte)'r' << 8 + (byte)'t';
		// final long falseTest = (byte)',' << 40 + (byte)'e' << 32 + (byte)'s' << 24 + (byte)'l' << 16 + (byte)'a' << 8 + (byte)'f';

		final long trueTest = ((byte)'t' << 40) + ((byte)'r' << 32) + ((byte)'u' << 24) + ((byte)'e' << 16) + ((byte)',' << 8);
		final long falseTest = ((byte)'f' << 40) + ((byte)'a' << 32) + ((byte)'l' << 24) + ((byte)'s' << 16) + ((byte)'e' << 8) + (byte)',';

		long test = ctx.buffer.getLong() & 0xffffffffffffl;
		ctx.buffer.position(ctx.buffer.position()-1);

		if(test == trueTest){
			return true;
		} else if (test == falseTest){
			return false;
		} else {
			byte[] b = new byte[8];
			ByteBuffer.allocate(8).putLong(test).get(b);
			String field = new String(b, StandardCharsets.UTF_8);
			throw new JsonInvalidBooleanException("Expected true/false, but given: "+field, ctx);
		}


		// char[] test;
		// 	switch(ctx.buffer[ctx.pointer]){
		// 		case 't' -> {
		// 			test = t;
		// 		}
		// 		case 'f' -> {
		// 			test = f;
		// 		}
		// 		default -> {
		// 			throw new JsonInvalidBooleanException("Unexpected symbol "+ctx.buffer[ctx.pointer], ctx);
		// 		}
		// 	}
		//
		// 	for(int i = 0; i < test.length; i++, ctx.pointer++, ctx.index++, ctx.column++){
		// 		if(test[i] != ctx.buffer[ctx.pointer]){
		// 			throw new JsonInvalidBooleanException("Unexpected symbol "+ctx.buffer[ctx.pointer], ctx);
		// 		}
		// 	}
		// 	return test == t;
	}

	@Override
	public String toFormatedString() {
		return value ? "true" : "false";
	}

	@Override
	public int buffSize() {
		return value ? 4 : 5;
	}

	@Override
	public int buffSizeFormat() {
		return value ? 4 : 5;
	}

	@Override
	void toString(IJsonFormatContext ctx) throws IOException {
		ctx.writer.write(value ? "true" : "false");
	}

	@Override
	int buffSize(IJsonFormatContext ctx) {
		return value ? 4 : 5;
	}
	@Override
	public String toString() {
		return value ? "true" : "false";
	}
	@Override
	public IJsonEntry clone(){
		IJsonBoolean js = new IJsonBoolean(value);
		return js;
	}
	@Override
	public boolean equals(Object obj){
		if(obj instanceof IJsonBoolean bool){
			return bool.value == value;
		}
		return false;
	}
}
