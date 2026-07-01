package ixoroturg.json;

import java.io.IOException;
import java.io.OutputStream;
// import java.nio.channels.WritableByteChannel;
// import java.io.Writer;
import java.nio.charset.StandardCharsets;

import ixoroturg.pool.IPool;;

class IJsonFormatContext{
	JsonWriter writer = null;
	// IJsonWriteChannel channel;
	int depth;
	boolean format = false;
	int available;
	IPool<IJsonFormatContext>.IPoolEntry entry;

	char FORMAT_INDENT_SYMBOL;
	byte FORMAT_INDENT_COUNT;
	boolean SHOW_INNER_DOUBLE_VALUE;
	boolean FORMAT_DIRECT_WRITE_CONTROL_CHARACTER;
	boolean FORMAT_FLUSH_WHEN_FULL;
	int FORMAT_BUFFER_SIZE;
	boolean AUTO_FLUSH;

	private static IPool<IJsonFormatContext> pool = generatePool(8, 0, 2);

	static IPool<IJsonFormatContext> generatePool(int initSize, int maxSize, float scale){
		IPool<IJsonFormatContext> pool = IPool.<IJsonFormatContext>newBuilder()
		.size(initSize)
		.maxSize(maxSize)
		.scale(scale)
		.generator(()->{
			return new IJsonFormatContext();
		})
		.build();
	return pool;
	}

	static IJsonFormatContext openContext(OutputStream stream){
	JsonWriter writer = new JsonWriter(stream);
	IPool<IJsonFormatContext>.IPoolEntry entry = pool.open();
	entry.value.entry = entry;
	entry.value.open(writer);
		return entry.value;
	}

	private void open(JsonWriter stream){

		this.writer = stream;
	
		depth = 0;

		FORMAT_INDENT_SYMBOL = IJsonSetting.FORMAT_INDENT_SYMBOL;
		FORMAT_INDENT_COUNT = IJsonSetting.FORMAT_INDENT_COUNT;
		SHOW_INNER_DOUBLE_VALUE = IJsonSetting.SHOW_INNER_DOUBLE_VALUE;
		FORMAT_DIRECT_WRITE_CONTROL_CHARACTER = IJsonSetting.FORMAT_DIRECT_WRITE_CONTROL_CHARACTER;
		FORMAT_BUFFER_SIZE = IJsonSetting.FORMAT_BUFFER_SIZE;
		FORMAT_FLUSH_WHEN_FULL = IJsonSetting.FORMAT_FLUSH_WHEN_FULL;
		AUTO_FLUSH = IJsonSetting.AUTO_FLUSH;

		available = FORMAT_BUFFER_SIZE;
	}
	void close() throws IOException{
		if(AUTO_FLUSH)
		writer.flush();
			// writer.flush();
		entry.close();
	}
	static class JsonWriter {
		OutputStream stream = null;
		JsonWriter(OutputStream stream){
			this.stream = stream;
		}

		public void write(String str) throws IOException{
			byte[] b = str.getBytes(StandardCharsets.UTF_8);
			write(b);
		}
		public void write(byte[] b) throws IOException{
			stream.write(b);
		}
		public void write(byte b) throws IOException{
			stream.write(b);
		}
		public void write(char b) throws IOException{
			write((byte)b);
		}

		public void flush() throws IOException {
			stream.flush();
		}

		public void close() throws IOException {
			stream.close();
		}
		
	}
}
