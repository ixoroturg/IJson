package ixoroturg.json;

import java.io.IOException;
import java.io.Writer;

import ixoroturg.pool.IPool;;

class IJsonFormatContext{
  Writer writer;
  int depth;
  boolean format = false;
  IPool<IJsonFormatContext>.IPoolEntry entry;

	char FORMAT_INDENT_SYMBOL;
	byte FORMAT_INDENT_COUNT;
	boolean SHOW_INNER_DOUBLE_VALUE;
	boolean FORMAT_DIRECT_WRITE_CONTROL_CHARACTER;
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

  static IJsonFormatContext openContext(Writer writer){
	IPool<IJsonFormatContext>.IPoolEntry entry = pool.open();
	entry.value.entry = entry;
	entry.value.open(writer);
    return entry.value;
  }

  private void open(Writer writer){
    this.writer = writer;
    depth = 0;

	FORMAT_INDENT_SYMBOL = IJsonSetting.FORMAT_INDENT_SYMBOL;
	FORMAT_INDENT_COUNT = IJsonSetting.FORMAT_INDENT_COUNT;
	SHOW_INNER_DOUBLE_VALUE = IJsonSetting.SHOW_INNER_DOUBLE_VALUE;
	FORMAT_DIRECT_WRITE_CONTROL_CHARACTER = IJsonSetting.FORMAT_DIRECT_WRITE_CONTROL_CHARACTER;
	AUTO_FLUSH = IJsonSetting.AUTO_FLUSH;
  }
  void close() throws IOException{
    if(AUTO_FLUSH)
      writer.flush();
	entry.close();
  }
}
