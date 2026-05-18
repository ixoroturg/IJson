package ixoroturg.json;

import java.io.IOException;
import java.io.OutputStream;
// import java.nio.channels.WritableByteChannel;

import ixoroturg.pool.IPool;;

class IJsonFormatContext{
	OutputStream out = null;
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
	IPool<IJsonFormatContext>.IPoolEntry entry = pool.open();
	entry.value.entry = entry;
	entry.value.open(stream);
    return entry.value;
  }

  private void open(OutputStream stream){
    this.out = stream;
	
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
		out.flush();
      // writer.flush();
	entry.close();
  }
}
