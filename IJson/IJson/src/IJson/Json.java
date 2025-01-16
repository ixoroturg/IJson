package IJson;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface Json extends Iterable<Json>, Map<String, Json>{
	
	
	public Json put(String key, String value);
	
	/**
	 * Add json to array
	 * @param json to add
	 * @return true on success
	 */
	public boolean add(Json json);
	/**
	 * Remove json from array
	 * @param json to remove
	 * @return true on success
	 */
	public void remove(Json json);
	/**
	 * Add json to array
	 * @param json to add
	 * @return true on success
	 */
	public void add(String json);
	/**
	 * Remove json from object by given property name
	 * @param property name to remove
	 * @return removed Json
	 */
	public Json remove(String json);
	
	/**
	 * Read all bytes as string from given InputStream and parse it into this json
	 * @param in InputStream to read
	 * @return this json
	 * @throws IOException
	 */
	public Json readFrom(InputStream in) throws IOException;
	/**
	 * Write all bytes into given OutputStream from toString()
	 * @param out OutputStream to write
	 * @throws IOException
	 */
	public void writeTo(OutputStream out) throws IOException;
	/**
	 * Return first json from array
	 * @return return first json from array
	 */
	public Json get();
	/**
	 * Return json from array at index
	 * @param index - index of array value
	 * @return return json from array
	 */	
	public Json get(int index);
	/**
	 * Return parent of this Json
	 * @return parent of this Json
	 */
	public Json back();
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return boolean from json
	 */
	public boolean getBoolean(String propertyName);
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return float from json
	 */
	public float getFloat(String propertyName);
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return double from json
	 */
	public double getDouble(String propertyName);
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return byte from json
	 */	
	public byte getByte(String propertyName);
	/**
	 * get and parse
	 * @param propertyName - name  of property
	 * @return short from json
	 */
	public short getShort(String propertyName);
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return int from json
	 */
	public int getInt(String propertyName);
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return long from json
	 */
	public long getLong(String propertyName);
	/**
	 * Get string and format it for human readable output
	 * @param propertyName - name  of property
	 * @return String from json
	 */
	public String getString(String propertyName);
	
}
