package ixoroturg.json;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

class IJsonUtil {
	static boolean isWhiteSpace(byte ch){
		return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
	}
	static boolean testNull(IJsonParseContext ctx) throws JsonParseException{
		int test;
		if(ctx.buffer.remaining() < 4){
			int remaining = 4 - ctx.buffer.remaining();
			ctx.builder.clear();
			ctx.builder.put(ctx.buffer);
			ctx.read();
			int limit = ctx.buffer.limit();
			ctx.buffer.limit(remaining);
			ctx.builder.put(ctx.buffer);
			ctx.builder.clear();
			ctx.buffer.limit(limit);
			test = ctx.builder.getInt();
		} else {
			test = ctx.buffer.getInt();
		}
		// if(!ctx.buffer.hasRemaining()){
		// 	ctx.read();
		// 	ctx.buffer.get();
		// }

		// final int nullTest = ((byte)'n' << 24) + ((byte)'u' << 16) + ((byte)'l' << 8) + ((byte)'l');
		final int nullTest = ((int)'l' << 32)+((int)'l' << 40)+((int)'u' << 48)+((int)'n' << 56);

		if(test == nullTest){
			return true;
		} else {
			byte[] b = new byte[4];
			ByteBuffer.allocate(4).putInt(test).get(b);
			String field = new String(b,StandardCharsets.UTF_8);
			ctx.buffer.position(ctx.buffer.position() - 4);
			throw new JsonParseException("Expected null, but found: "+field,ctx);
		}
			
		// if(ctx.buffer.length - ctx.pointer - 1 < 4){
		// 	ctx.read();
		// }
		
		// if(ctx.buffer[ctx.pointer] != 'n')
		//	 throw new JsonParseException("Unexpected symbol "+ctx.buffer[ctx.pointer],ctx);
		// ctx.pointer++;
		// ctx.column++;
		// ctx.index++;
		// if(ctx.buffer[ctx.pointer] != 'u')
		//	 throw new JsonParseException("Unexpected symbol "+ctx.buffer[ctx.pointer],ctx);
		// ctx.pointer++;
		// ctx.column++;
		// ctx.index++;
		//
		// if(ctx.buffer[ctx.pointer] != 'l')
		//	 throw new JsonParseException("Unexpected symbol "+ctx.buffer[ctx.pointer],ctx);
		// ctx.pointer++;
		// ctx.column++;
		// ctx.index++;
		//
		// if(ctx.buffer[ctx.pointer] != 'l')
		//	 throw new JsonParseException("Unexpected symbol "+ctx.buffer[ctx.pointer],ctx);
		// ctx.pointer++;
		// ctx.column++;
		// ctx.index++;
		// return true;
	}
}
