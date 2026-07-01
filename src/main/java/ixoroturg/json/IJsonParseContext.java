package ixoroturg.json;

// import java.io.InputStream;
import java.nio.ByteBuffer;
// import java.nio.channels.Channel;
// import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.io.IOException;

import ixoroturg.pool.IPool;

class IJsonParseContext {
	// Reader reader;
	ReadableByteChannel channel = null;
	ByteBuffer buffer; //= new char[(1 << IJsonSetting.BUFFER_SIZE) * 2];
	ByteBuffer save = null;
	int column;
	int row;
	int index;
	int pointer;
	byte hex = -1;
	int unicode = 0;
	boolean wasDigit = false;
	boolean wasExpSign = false;
	boolean wasSlash = false;
	boolean wasExp = false;
	boolean wasDot = false;
	boolean shouldDot = false;
	boolean needKey = true;
	int fracSize = 0;
	double numberValue = 0;
	boolean wasMinus = false;
	boolean updateBuffer = false;
	int zeroCount = 0;

	String key;
	long timer; 
	// StringBuilder builder = new StringBuilder(IJsonSetting.STRING_BUILDER_BUFFER_SIZE);
	ByteBuffer builder = ByteBuffer.allocate(IJsonSetting.STRING_BUILDER_BUFFER_SIZE);
	boolean firstPass = true;

	private IPool<IJsonParseContext>.IPoolEntry entry = null;

	byte CHARACTERS_BEFORE_ERROR_INDEX;
	byte CHARACTERS_AFTER_ERROR_INDEX;
	boolean DECODE_UNICODE_SEQUENCE;
	int BUFFER_SIZE;

	boolean NULL_STRING_AS_NULL_VALUE = false;
	boolean ESCAPE_CONTROL_CHARACTERS = false;
	boolean USE_FAST_NUMBER_PARSE = false;
	boolean USE_LAZY_NUMBER_PARSER = true;



	// static IJsonParseContext[] ctx = new IJsonParseContext[IJsonSetting.PARSE_CONTEXT_COUNT];

	static IPool<IJsonParseContext> pool = generatePool(8, 0,2);

	static IPool<IJsonParseContext> generatePool(int initSize, int maxSize, float scale){
		
		IPool<IJsonParseContext> pool = IPool.<IJsonParseContext>newBuilder()
			.size(initSize)
			.maxSize(maxSize)
			.scale(scale)
			.generator(()->{
				return new IJsonParseContext();
			})
			.autoReset()
			.reset((ctx) -> {
				ctx.timer = System.currentTimeMillis();
				
				ctx.index = 0;
				ctx.pointer = 0;
				ctx.column = 0;
				ctx.row = 0;
				ctx.fracSize = 0;
				ctx.unicode = 0;
				ctx.builder.reset();
				// ctx.builder.setLength(0);
				ctx.firstPass = true;
				
				return ctx;
			})
			.build();
		return pool;
	}

	static IJsonParseContext openContext(ByteBuffer buffer) throws JsonParseException {

		IPool<IJsonParseContext>.IPoolEntry entry = pool.open();
		entry.value.entry = entry;
		// ReadableByteChannel chan = Channels.newChannel(input);
		entry.value.open(buffer);
		return entry.value;
	}
	static IJsonParseContext openContext(ReadableByteChannel channel) throws JsonParseException {
	IPool<IJsonParseContext>.IPoolEntry entry = pool.open();
	entry.value.entry = entry;
	// ReadableByteChannel chan = Channels.newChannel(input);
	entry.value.open(channel);
	return entry.value;
	}

	long close(){
		long result = System.currentTimeMillis() - timer;
		entry.close();
		return result;
	}
	void open(ReadableByteChannel channel) throws JsonParseException{
		buffer = save;
		BUFFER_SIZE = IJsonSetting.BUFFER_SIZE;

	if(buffer == null || buffer.capacity() != BUFFER_SIZE){
		// buffer = new byte[BUFFER_SIZE << 1];
		buffer = ByteBuffer.allocate(BUFFER_SIZE);
	}

	CHARACTERS_BEFORE_ERROR_INDEX = IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX;
	CHARACTERS_AFTER_ERROR_INDEX = IJsonSetting.CHARACTERS_AFTER_ERROR_INDEX;
	DECODE_UNICODE_SEQUENCE = IJsonSetting.DECODE_UNICODE_SEQUENCE;
	
	// FORMAT_INDENT_SYMBOL = IJsonSetting.FORMAT_INDENT_SYMBOL;
	// FORMAT_INDENT_COUNT = IJsonSetting.FORMAT_INDENT_COUNT;
	NULL_STRING_AS_NULL_VALUE = IJsonSetting.NULL_STRING_AS_NULL_VALUE;
	ESCAPE_CONTROL_CHARACTERS = IJsonSetting.ESCAPE_CONTROL_CHARACTERS;
	// SHOW_INNER_DOUBLE_VALUE = IJsonSetting.SHOW_INNER_DOUBLE_VALUE;
	// FORMAT_DIRECT_WRITE_CONTROL_CHARACTER = IJsonSetting.FORMAT_DIRECT_WRITE_CONTROL_CHARACTER;
	USE_FAST_NUMBER_PARSE = IJsonSetting.USE_FAST_NUMBER_PARSE;
	USE_LAZY_NUMBER_PARSER = IJsonSetting.USE_LAZY_NUMBER_PARSER;
	// AUTO_FLUSH = IJsonSetting.AUTO_FLUSH;
	// KEY_DELIMETER = IJsonSetting.KEY_DELIMETER;
	// PARENT_CHARACTER = IJsonSetting.PARENT_CHARACTER;
	// USE_ARRAY_SYNTAX = IJsonSetting.USE_ARRAY_SYNTAX;

		this.channel = channel;
		try{
		int pos = channel.read(buffer);
			if(pos < buffer.capacity()){
			buffer.put((byte)-1);
		}
		buffer.flip();
		}catch(IOException e){
			JsonParseException exp = new JsonParseException("Cannot read the stream/channel");
			exp.initCause(e);
			throw exp;
		}
	}

	void open(ByteBuffer buffer){
		save = this.buffer;
		this.buffer = buffer;
		
		CHARACTERS_BEFORE_ERROR_INDEX = IJsonSetting.CHARACTERS_BEFORE_ERROR_INDEX;
		CHARACTERS_AFTER_ERROR_INDEX = IJsonSetting.CHARACTERS_AFTER_ERROR_INDEX;
		DECODE_UNICODE_SEQUENCE = IJsonSetting.DECODE_UNICODE_SEQUENCE;
		NULL_STRING_AS_NULL_VALUE = IJsonSetting.NULL_STRING_AS_NULL_VALUE;
		ESCAPE_CONTROL_CHARACTERS = IJsonSetting.ESCAPE_CONTROL_CHARACTERS;
		USE_FAST_NUMBER_PARSE = IJsonSetting.USE_FAST_NUMBER_PARSE;
		USE_LAZY_NUMBER_PARSER = IJsonSetting.USE_LAZY_NUMBER_PARSER;

	}


	boolean lock = false;
	boolean done = false;
	boolean newBuffer = false;
	void read() throws JsonParseException{

		// synchronized(this){
		lock = true;
		newBuffer = false;
		while(lock){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		if(newBuffer){
			return;
		}
	// }
	
	int buf = BUFFER_SIZE>>1;
	buffer.clear();
	byte[] innerBuf = buffer.array();
	buffer.get(buf,innerBuf,0,buf);
	buffer.position(buf);
		// System.arraycopy(buffer, buf, buffer, 0 ,buf);
		try{
		
		int len = channel.read(buffer);
		if(len < buf){
			buffer.put((byte)-1);
		}
			// channel.read(buffer,buf, buf);
		} catch(Exception e){
			JsonParseException exp = new JsonParseException("Cannot read the stream/channel");
			exp.initCause(e);
			throw exp;
		}
		// pointer -= buf;
		// return buf;
	}

}
