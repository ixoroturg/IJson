package ixoroturg.json.spi;

import java.io.InputStream;

import ixoroturg.json.spi.IJson;
import ixoroturg.json.Json;
import ixoroturg.json.JsonProvider;

public class IJsonProvider implements JsonProvider{
	// public IJsonProvider(){};

	@Override
	public String getAuthor() {
		return "ixoroturg";
	}

	@Override
	public String getVersion() {
		return "5.1.0";
	}

	@Override
	public Json of(InputStream input) {
		return IJson.of(input);
	}

	@Override
	public Json of(String string) {
		return IJson.of(string);
	}

	@Override
	public Json ofStringJson(String string) {
		return IJson.ofStringJson(string);
	}

	@Override
	public Json ofObject() {
		return IJson.ofObject();
	}

	@Override
	public Json ofArray() {
		return IJson.ofArray();
	}

}
