package ixoroturg.json.spi;

import java.io.InputStream;

import ixoroturg.json.spi.IJson;
import ixoroturg.json.Json;
import ixoroturg.json.JsonProvider;

public class IJsonProvider implements JsonProvider{

	@Override
	public String getAuthor() {
		return "ixoroturg";
	}

	@Override
	public String getVersion() {
		return "6.0.0";
	}

	@Override
	public Json of(InputStream input) {
		return IJson.ofInner(input);
	}

	@Override
	public Json of(String string) {
		return IJson.ofInner(string);
	}

	@Override
	public Json ofStringJson(String string) {
		return IJson.ofStringJsonInner(string);
	}

	@Override
	public Json ofObject() {
		return IJson.ofObjectInner();
	}

	@Override
	public Json ofArray() {
		return IJson.ofArrayInner();
	}

}
