package ixoroturg.json;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

public class IJsonWriteChannel {

	IJsonFormatContext ctx = null;
	OutputStream out = null;
	IJson json = null;
	
	IJsonWriteChannel(OutputStream stream, IJson json){
		this.out = stream;
		this.json = json;
		IJsonFormatContext ctx = IJsonFormatContext.openContext(stream);
	}

	void flush(){
	}

	void write(byte[] output) throws IOException{
		if(ctx.FORMAT_BUFFER_SIZE == 0){
			out.write(output);
			return;
		}
		if(output.length <= ctx.available){
			out.write(output);
			ctx.available -= output.length;
			return;
		}

		int available = ctx.available;
		int remaining = output.length;
		int position = 0;
		while(remaining != 0){
			out.write(output, position, available);
			remaining -= available;
		}
	}
}
