package ixoroturg.json;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public interface JsonParser {

	public CompletableFuture<Json> getFuture();
	public Json fullParse();
	public JsonPartialParser partialParse();

	public interface JsonPartialParser{
		public Json read();
		public Json read(byte[] chunk);
		public Json read(ByteBuffer chunk);
	}
}
