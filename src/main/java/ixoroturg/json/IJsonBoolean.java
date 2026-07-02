package ixoroturg.json;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

class IJsonBoolean extends IJsonEntry{
	boolean value;
	// private static final char[] t = {'t', 'r', 'u', 'e'};
	// private static final char[] f = {'f', 'a', 'l', 's', 'e'};
	IJsonBoolean(){}
	IJsonBoolean(boolean value){
		this.value = value;
	}

	@Override
	void parse(IJsonParseContext ctx) throws JsonParseException, JsonInvalidBooleanException{
		value = validate(ctx);
		// ctx.pointer--;
	}

	static boolean validate(IJsonParseContext ctx) throws JsonParseException, JsonInvalidBooleanException {
	
		long test = 0;
		// System.out.println("Вход на boolean: "+ctx.buffer);
		// int pos = ctx.buffer.position();
		// for(;ctx.buffer.hasRemaining();){
		// 	System.out.print((char)ctx.buffer.get());
		// }
		// ctx.buffer.position(pos);
		// System.out.println("Осталось: "+ctx.buffer.remaining());
		if(ctx.buffer.remaining() >= 8){
			test = ctx.buffer.getLong();
			// System.out.println(Long.toHexString(test));
			ctx.buffer.position(ctx.buffer.position() - 3);
		} else
		if(ctx.buffer.remaining() < 5){
			int remaining = 5 - ctx.buffer.remaining();
			ctx.builder.clear();
			ctx.builder.put(ctx.buffer);
			ctx.read();
			int limit = ctx.buffer.limit();
			ctx.buffer.limit(remaining);
			ctx.builder.put(ctx.buffer);
			ctx.builder.clear();
			ctx.buffer.limit(limit);
			test = ctx.builder.getLong();
			// System.out.println("2: "+Long.toHexString(test));
		} else {
			int limit = ctx.buffer.limit();
			ctx.buffer.limit(ctx.buffer.position() + 5);
			ctx.builder.clear();

			// System.out.println("\nBuffer: "+ctx.buffer);
			// System.out.println("\nBuilder: "+ctx.builder);
			

			// System.out.println("\nЧто за хуйня?");
			// int pos = ctx.buffer.position();
			// for(;ctx.buffer.hasRemaining();){
			// 	System.out.print((char)ctx.buffer.get());
			// }
			// ctx.buffer.position(pos);
			
			ctx.builder.put(ctx.buffer);
			ctx.builder.clear();

			// System.out.println("\nЧто за хуйня?2");
			// pos = ctx.builder.position();
			// for(;ctx.builder.hasRemaining();){
			// 	System.out.print((char)ctx.builder.get());
			// }
			// ctx.builder.position(pos);

			// System.out.println("После чтения: "+ctx.builder);

			ctx.buffer.limit(limit);
			test = ctx.builder.getLong();
			// System.out.println("3: "+Long.toHexString(test));
		}

		if((test & 0xff000000_00000000l) == ((long)'t'<<56)){
			test = test & 0xffffffff_00000000l;
		} else {
			test = test & 0xffffffff_ff000000l;
		}
		
		// if(ctx.buffer.length - ctx.pointer < 6){
		// 	ctx.read();
		// }

		final long trueTest = ((long)'e' << 32) + ((long)'u' << 40) + ((long)'r' << 48) + ((long)'t' << 56);
		final long falseTest = ((long)'e' << 24) + ((long)'s' << 32) + ((long)'l' << 40) + ((long)'a' << 48) + ((long)'f' << 56);

		// long trueTest = ((long)'t' << 40) + ((long)'r' << 32) + ((long)'u' << 24) + ((long)'e' << 16) + ((long)',' << 8);
		// long falseTest = ((byte)'f' << 40) + ((byte)'a' << 32) + ((byte)'l' << 24) + ((byte)'s' << 16) + ((byte)'e' << 8) + (byte)',';
		// falseTest = ((byte)',' << 0) + ((byte)'e' << 8) + ((byte)'s' << 16);

		// long test = ctx.buffer.getLong() & 0xffffffffffffl;
		// ctx.buffer.position(ctx.buffer.position()-1);

		if(test == trueTest){
			ctx.buffer.position(ctx.buffer.position() - 1);
			return true;
		} else if (test == falseTest){
			return false;
		} else {
			byte[] b = new byte[8];
			ctx.buffer.position(ctx.buffer.position()-5);
			// falseTest = falseTest << 16;
			ByteBuffer.allocate(8).putLong(test).flip().get(b);
			// System.out.println("\nБайты");
			// for(byte bt: b){
			// 	System.out.println((char)bt);
			// }
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
