package ixoroturg.json;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public interface Json extends Iterable<Json>, Map<String, Json>{
	
	
	
	public IntStream getIntStream();
	public LongStream getLongStream();
	public DoubleStream getDoubleStream();
	public Stream<String> getStringStream();
	public Stream<Json> getJsonStream();

	// public IntStream getIntStreamOr(String value);
	// public LongStream getLongStreamOr(String value);
	// public DoubleStream getDoubleStreamOr(String value);
	// public Stream<String> getStringStreamOr(String value);
	// public Stream<Json> getJsonStreamOr(String value);
	
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
	// or

	public byte[] getByteArrayOr(String key, byte[] value);
	public short[] getShortArrayOr(String key, short[] value);
	public int[] getIntArrayOr(String key, int[] value);
	public long[] getLongArrayOr(String key, long[] value);
	
	public float[] getFloatArrayOr(String key, float[] value);
	public double[] getDoubleArrayOr(String key,double[] value);
	
	public boolean[] getBooleanArrayOr(String key, boolean[] value);
	public String[] getStringArrayOr(String key,String[] value);
	public Json[] getJsonArrayOr(String key, Json[] value);

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
	public Json putString(String key, String value);
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
	public Json addString(String json);
	public Json addValue(String json);
	public Json add(Json json);
	public Json add(boolean value);
	public Json add(long value);
	public Json add(double value);
	
	
	public Json addString(String key, String json);
	public Json addValue(String key, String value);
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
	public Json remove(String key);
	public Json remove(int index);
	public Json remove(String key, int index);
	public Json remove();
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
	public int getIndex();
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
	public boolean getBoolean();
	public boolean getBoolean(String propertyName);
	public boolean getBooleanOr(String propertyName, boolean value);
	public boolean getBoolean(int index);
	public boolean getBooleanOr(int index, boolean value);
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return float from json
	 */
	public float getFloat();
	public float getFloat(String propertyName);
	public float getFloatOr(String propertyName, float value);
	public float getFloat(int index);
	public float getFloatOr(int index, float value);
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return double from json
	 */
	public double getDouble();
	public double getDouble(String propertyName);
	public double getDoubleOr(String propertyName, double value);
	public double getDouble(int index);
	public double getDoubleOr(int index, double value);
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return byte from json
	 */	
	public byte getByte();
	public byte getByte(String propertyName);
	public byte getByteOr(String propertyName, byte value);
	public byte getByte(int index);
	public byte getByteOr(int index, byte value);
	/**
	 * get and parse
	 * @param propertyName - name  of property
	 * @return short from json
	 */
	public short getShort();
	public short getShort(String propertyName);
	public short getShortOr(String propertyName, short value);
	public short getShort(int index);
	public short getShortOr(int index, short value);
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return int from json
	 */
	public int getInt();
	public int getInt(String propertyName);
	public int getIntOr(String propertyName, int value);
	public int getInt(int index);
	public int getIntOr(int index, int value);
	/**
	 * Get and parse
	 * @param propertyName - name  of property
	 * @return long from json
	 */
	public long getLong();
	public long getLong(String propertyName);
	public long getLongOr(String propertyName, long value);
	public long getLong(int index);
	public long getLongOr(int index, long value);
	/**
	 * Get string and format it for human readable output
	 * @param propertyName - name  of property
	 * @return String from json
	 */
	public String getString();
	public String getString(String propertyName);
	public String getStringOr(String propertyName, String value);
	public String getString(int index);
	public String getStringOr(int index, String value);
	
	public Json parseHttpRequest(String request);
	public Json parseHttpRequestForce(String request);
	
	public String getPropertyName();
	public String getPropertyNameOrNull();
	public boolean equals(String json);
	public Json setType(JsonType type);
	public byte[] toBytes();

  public Json putJson(String key);
  public Json putGetJson(String key);
  public Json addJson();
  public Json addJson(String key);

  public String toFormatedString();
	// public boolean hasKey(String key);
}
