package ixoroturg.json;

import java.io.InputStream;

public interface JsonProvider {
	public String getAuthor();
	public String getVersion();

	public Json of(InputStream input);
	public Json of(String string);
	public Json ofStringJson(String string);
	public Json ofObject();
	public Json ofArray();
	// public Json of()
}
