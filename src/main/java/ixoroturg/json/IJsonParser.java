package ixoroturg.json;

import java.nio.ByteBuffer;
// import java.io.ByteArrayInputStream;
// import java.io.InputStream;
// import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
// import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class IJsonParser implements JsonParser{

	IJsonParseContext ctx = null;
	IJson json = null;
	CompletableFuture<Json> future = null;

	IJsonParser(ReadableByteChannel channel, IJson json){
		ctx = IJsonParseContext.openContext(channel);
		this.json = json;
		future = new CompletableFuture<Json>();
	}

	public IJson fullParse(){
		PartialParser parser = partialParse();
		IJson result = null;
		while((result = parser.read()) == null){}
		return result;
	}

	public PartialParser partialParse(){
		return new PartialParser();
	}

	public CompletableFuture<Json> getFuture(){
		return future;
	}

	public class PartialParser implements JsonPartialParser{
		PartialParser(){
			json.of(ctx);
		}
		
		@Override
		public IJson read(){
			synchronized(ctx){
				if(ctx.done){
					future.complete(json);
					return json;
				}
				ctx.lock = false;
				ctx.notify();
			}
			return null;
		}

		@Override
		public Json read(byte[] chunk) {
			ByteBuffer buf = ByteBuffer.wrap(chunk);
			return read(buf);
		}

		@Override
		public Json read(ByteBuffer chunk) {
			synchronized(ctx){
				if(ctx.done){
					future.complete(json);
					return json;
				}
				ctx.buffer = chunk;
				ctx.buffer.clear();
				ctx.lock = false;
				ctx.newBuffer = true;
				ctx.notify();
			}
			return null;
		}
	}
}
