package ixoroturg.json;

import java.util.concurrent.CompletableFuture;

public interface JsonParser {

	public CompletableFuture<Json> getFuture();
	public Json fullParse();
	public JsonPartialParser partialParse();

	public interface JsonPartialParser{
		public Json read();
	}
}
