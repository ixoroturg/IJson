package IJson;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public interface Json extends Iterable<Json>, Map<String, Json>{
	
	
	
	public IntStream getIntStream();
	public LongStream getLongStream();
	public DoubleStream getDoubleStream();
	public Stream<String> getStringStream();
	public Stream<Json> getJsonStream();
	
	public IntStream getIntStream(String key);
	public LongStream getLongStream(String key);
	public DoubleStream getDoubleStream(String key);
	public Stream<String> getStringStream(String key);
	public Stream<Json> getJsonStream(String key);
	
	public byte[] getByteArray();
	public short[] getShortArray();
	public int[] getIntArray();
	public long[] getLongArray();
	
	public float[] getFloatArray();
	public double[] getDoubleArray();
	
	public boolean[] getBooleanArray();
	public String[] getStringArray();
	public Json[] getJsonArray();
	
	public byte[] getByteArray(String key);
	public short[] getShortArray(String key);
	public int[] getIntArray(String key);
	public long[] getLongArray(String key);
	
	public float[] getFloatArray(String key);
	public double[] getDoubleArray(String key);
	
	public boolean[] getBooleanArray(String key);
	public String[] getStringArray(String key);
	public Json[] getJsonArray(String key);
	
	public Json add(byte[] array);
	public Json add(short[] array);
	public Json add(int[] array);
	public Json add(long[] array);
	
	public Json add(float[] array);
	public Json add(double[] array);
	
	public Json add(char[] array);
	public Json add(boolean[] array);
	
	public <T> Json add(T[] array);
	
	public Json add(String[] array);
	public Json add(Json[] array);
	
	public Json add(String key, byte[] array);
	public Json add(String key, short[] array);
	public Json add(String key, int[] array);
	public Json add(String key, long[] array);
	
	public Json add(String key, float[] array);
	public Json add(String key, double[] array);
	
	public Json add(String key, char[] array);
	public Json add(String key, boolean[] array);
	
	public <T> Json add(String key, T[] array);
	
	public Json add(String key, String[] array);
	public Json add(String key, Json[] array);
	
	
	public String getRAWJson();
	
	public Json put(String key, String value);
	public Json putValue(String key, String value);
	public Json put(String key, boolean value);
	public Json put(String key, long value);
	public Json put(String key, double value);
	
	public Json put(String key, byte[] array);
	public Json put(String key, short[] array);
	public Json put(String key, int[] array);
	public Json put(String key, long[] array);
	public Json put(String key, float[] array);
	public Json put(String key, double[] array);
	public Json put(String key, char[] array);
	public Json put(String key, boolean[] array);
	public Json put(String key, String[] array);
	public Json put(String key, Json[] array);
	/**
	 * Add json to array
	 * @param json to add
	 * @return true on success
	 */
	public Json add(String json);
	public Json add(Json json);
	public Json add(boolean value);
	public Json add(long value);
	public Json add(double value);
	
	public Json add(String key, String json);
	public Json add(String key, Json json);
	public Json add(String key, boolean value);
	public Json add(String key, long value);
	public Json add(String key, double value);
	/**
	 * Remove json from array
	 * @param json to remove
	 * @return true on success
	 */
	public Json remove(Json json);
	/**
	 * Add json to array
	 * @param json to add
	 * @return true on success
	 */

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
	public Json back(int level);
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
	
	public Json parseHttpRequest(String request);
	public Json parseHttpRequestForce(String request);
	
	public String getPropertyName();
	public String getPropertyNameOrNull();
	public boolean equals(String json);
	public Json setType(JsonType type);
	public byte[] toBytes();
}
