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
		// System.out.println("Получение parser");
		ctx = IJsonParseContext.openContext(channel);
		ctx.start = true;
		// System.out.println("Читаем в первый раз");
		ctx.read();
		// System.out.println("Прочитали");
		ctx.start = false;
		this.json = json;
		future = new CompletableFuture<Json>();
	}
	IJsonParser(ByteBuffer buffer, IJson json){
		ctx = IJsonParseContext.openContext(buffer);
		this.json = json;
		future = new CompletableFuture<Json>();
	}

	public IJson fullParse(){
		// System.out.println("НАЧАЛО FULL PARSE");
		// System.out.println("full parse");
		PartialParser parser = partialParse();
		IJson result = null;
		// result.parse(json)
		// for(int i = 0; i < 10; i++){
		// 	result = parser.read();
		// 	if(result == null){
		// 		System.out.println("Читаем дальше");
		// 	} else {
		// 		System.out.println("Завершено");
		// 	}
		// }
		while((result = parser.read()) == null){
			// System.out.println("Цикл");
		}
		// System.out.println("Спарсилось");
		// System.out.println(result.currentJson != null);

		// System.out.println("КОНЕЦ FULL PARSE");
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
			// System.out.println("Получаем of(ctx)");
			Thread.ofVirtual().start(()->{
				try{
					json.of(ctx);
				}catch(Exception e){
					// System.err.println("Поломка парсера");
					e.printStackTrace();
				}
			});
		}
		
		@Override
		public IJson read(){
			synchronized(ctx){
				// System.out.println("Взяли монитор");
				if(ctx.done){
					future.complete(json);
					// ctx.close();
					return json;
				}
				ctx.lock = false;
				ctx.notify();
			}
			// System.out.println("Отпустили монитор");
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
					// ctx.close();
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
