package ixoroturg.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public interface Json {


  public Json get(String key) throws JsonNoSuchPropertyException, JsonNoParentException, UnsupportedOperationException, JsonParseException;
  public Json get(int key) throws JsonNoSuchPropertyException, UnsupportedOperationException;
  public Json go(String key) throws JsonNoSuchPropertyException, JsonNoParentException, UnsupportedOperationException, JsonParseException;
  public Json go(int key) throws JsonNoSuchPropertyException, UnsupportedOperationException;

  public int buffSize();
  public int buffSizeFormat();

  public Json parse(String json) throws JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException;
  public Json parse(InputStream stream) throws IOException, JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException;
  public Json parse(Reader reader) throws IOException, JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException;


  public String toStringFormat();
  public void writeTo(OutputStream stream) throws IOException;
  public void writeTo(Writer writer) throws IOException;
  public void writeToFormat(OutputStream stream) throws IOException;
  public void writeToFormat(Writer writer) throws IOException;


  // public Json getParent() throws JsonNoParentException;
  // public Json getParentOrNull();
  public String getPropertyName() throws JsonNoParentException;
  public String getPropertyNameOr(String value);
  public Json back() throws JsonNoParentException;
  public Json back(int depth) throws JsonNoParentException;
  public boolean has(String key) throws JsonParseException, UnsupportedOperationException;
  public boolean has(int key) throws UnsupportedOperationException;
  public int size() throws UnsupportedOperationException;
  public long getParseTime();
  public IJsonEntry getInnerRepresentation();


  // put(key, value)
  public Json put(String key, byte value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, short value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, int value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, long value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, float value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, double value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, boolean value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, String value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, Json value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  // add(value)
  public Json add(byte value) throws UnsupportedOperationException;
  public Json add(short value) throws UnsupportedOperationException;
  public Json add(int value) throws UnsupportedOperationException;
  public Json add(long value) throws UnsupportedOperationException;
  public Json add(float value) throws UnsupportedOperationException;
  public Json add(double value) throws UnsupportedOperationException;
  public Json add(boolean value) throws UnsupportedOperationException;
  public Json add(String value) throws UnsupportedOperationException;
  public Json add(Json value) throws UnsupportedOperationException;
  // add(key, value)
  public Json add(String key, byte value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, short value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, int value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, long value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, float value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, double value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, boolean value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, String value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, Json value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  // put(key, value[])
  public Json put(String key, byte[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, short[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, int[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, long[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, float[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, double[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, boolean[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, String[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json put(String key, Json[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  // add(value[])
  public Json add(byte[] value) throws UnsupportedOperationException;
  public Json add(short[] value) throws UnsupportedOperationException;
  public Json add(int[] value) throws UnsupportedOperationException;
  public Json add(long[] value) throws UnsupportedOperationException;
  public Json add(float[] value) throws UnsupportedOperationException;
  public Json add(double[] value) throws UnsupportedOperationException;
  public Json add(boolean[] value) throws UnsupportedOperationException;
  public Json add(String[] value) throws UnsupportedOperationException;
  public Json add(Json[] value) throws UnsupportedOperationException;
  // addAll(value[])
  public Json addAll(byte[] value) throws UnsupportedOperationException;
  public Json addAll(short[] value) throws UnsupportedOperationException;
  public Json addAll(int[] value) throws UnsupportedOperationException;
  public Json addAll(long[] value) throws UnsupportedOperationException;
  public Json addAll(float[] value) throws UnsupportedOperationException;
  public Json addAll(double[] value) throws UnsupportedOperationException;
  public Json addAll(boolean[] value) throws UnsupportedOperationException;
  public Json addAll(String[] value) throws UnsupportedOperationException;
  public Json addAll(Json[] value) throws UnsupportedOperationException;
  // add(key, value[])
  public Json add(String key, byte[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, short[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, int[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, long[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, float[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, double[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, boolean[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, String[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json add(String key, Json[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  // addAll(key, value[])
  public Json addAll(String key, byte[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addAll(String key, short[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addAll(String key, int[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addAll(String key, long[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addAll(String key, float[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addAll(String key, double[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addAll(String key, boolean[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addAll(String key, String[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addAll(String key, Json[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  // getType()
  public byte getByte() throws UnsupportedOperationException;
  public short getShort() throws UnsupportedOperationException;
  public int getInt() throws UnsupportedOperationException;
  public long getLong() throws UnsupportedOperationException;
  public float getFloat() throws UnsupportedOperationException;
  public double getDouble() throws UnsupportedOperationException;
  public boolean getBoolean() throws UnsupportedOperationException;
  public String getString() throws UnsupportedOperationException;
  // getType(key)
  public byte getByte(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public short getShort(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public int getInt(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public long getLong(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public float getFloat(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public double getDouble(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public boolean getBoolean(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public String getString(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
//  // getTypeOr()
//  public byte getByteOr(byte value) throws UnsupportedOperationException;
//  public short getShortOr(short value) throws UnsupportedOperationException;
//  public int getIntOr(int value) throws UnsupportedOperationException;
//  public long getLongOr(long value) throws UnsupportedOperationException;
//  public float getFloatOr(float value) throws UnsupportedOperationException;
//  public double getDoubleOr(double value) throws UnsupportedOperationException;
//  public boolean getBooleanOr(boolean value) throws UnsupportedOperationException;
//  public String getStringOr(String value) throws UnsupportedOperationException;
  // getTypeOr(key)
  public byte getByteOr(String key, byte value) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public short getShortOr(String key, short value) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public int getIntOr(String key, int value) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public long getLongOr(String key, long value) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public float getFloatOr(String key, float value) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public double getDoubleOr(String key, double value) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public boolean getBooleanOr(String key, boolean value) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  public String getStringOr(String key, String value) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,  UnsupportedOperationException;
  // getTypeArray()
  public byte[] getByteArray() throws UnsupportedOperationException, JsonIllegalTypeException;
  public short[] getShortArray() throws UnsupportedOperationException, JsonIllegalTypeException;
  public int[] getIntArray() throws UnsupportedOperationException, JsonIllegalTypeException;
  public long[] getLongArray() throws UnsupportedOperationException, JsonIllegalTypeException;
  public float[] getFloatArray() throws UnsupportedOperationException, JsonIllegalTypeException;
  public double[] getDoubleArray() throws UnsupportedOperationException, JsonIllegalTypeException;
  public boolean[] getBooleanArray() throws UnsupportedOperationException, JsonIllegalTypeException;
  public String[] getStringArray() throws UnsupportedOperationException, JsonIllegalTypeException;
  // getTypeArray(key)
  public byte[] getByteArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public short[] getShortArray(String key)throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public int[] getIntArray(String key)throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public long[] getLongArray(String key)throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public float[] getFloatArray(String key)throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public double[] getDoubleArray(String key)throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public boolean[] getBooleanArray(String key)throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public String[] getStringArray(String key)throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
//  // getTypeArrayOr()
//  public byte[] getByteArrayOr(byte[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public short[] getShortArrayOr(short[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public int[] getIntArrayOr(int[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public long[] getLongArrayOr(long[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public float[] getFloatArrayOr(float[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public double[] getDoubleArrayOr(double[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public boolean[] getBooleanArrayOr(boolean[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public String[] getStringArrayOr(String[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
  // getTypeArrayOr(key) // 14 * 9 (integer x4, float x2, boolean, String, Json) = 136 methods!!!
  public byte[] getByteArrayOr(String key, byte[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public short[] getShortArrayOr(String key, short[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public int[] getIntArrayOr(String key, int[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public long[] getLongArrayOr(String key, long[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public float[] getFloatArrayOr(String key, float[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public double[] getDoubleArrayOr(String key, double[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public boolean[] getBooleanArrayOr(String key, boolean[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  public String[] getStringArrayOr(String key, String[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,  JsonNoSuchPropertyException, JsonIllegalTypeException;
  // getTypeStream()
  public IntStream getIntStream() throws UnsupportedOperationException, JsonIllegalTypeException;
  public LongStream getLongStream() throws UnsupportedOperationException, JsonIllegalTypeException;
  public DoubleStream getDoubleStream() throws UnsupportedOperationException, JsonIllegalTypeException;
  public Stream<String> getStringStream() throws UnsupportedOperationException, JsonIllegalTypeException;
  public Stream<Json> getJsonStream() throws UnsupportedOperationException, JsonIllegalTypeException;
  // getTypeStream(key)
  public IntStream getIntStream(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException;
  public LongStream getLongStream(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException;
  public DoubleStream getDoubleStream(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException;
  public Stream<String> getStringStream(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException;
//  public Stream<Json> getJsonStream(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException;
//  // getTypeStreamOr(Type[])
//  public IntStream getIntStreamOr(int[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public LongStream getLongStreamOr(long[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public DoubleStream getDoubleStreamOr(double[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public Stream<String> getStringStreamOr(String[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
//  public Stream<Json> getJsonStreamOr(Json[] value) throws UnsupportedOperationException, JsonIllegalTypeException;
  // getTypeStreamOr(key, Type[]) // 4 * 5 (IntStream, LongStream, DoubleStream, StringStream, JsonStream, not BooleanStream) = 20 methods
  public IntStream getIntStreamOr(String key, int[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException;
  public LongStream getLongStreamOr(String key, long[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException;
  public DoubleStream getDoubleStreamOr(String key, double[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException;
  public Stream<String> getStringStreamOr(String key, String[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException;
  public Stream<Json> getJsonStreamOr(String key, Json[] value) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException;
  // putAddSpecial
  public Json putObject(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json putGetObject(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json putGoObject(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json putArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json putGetArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json putGoArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  
  public Json addObject() throws UnsupportedOperationException;
  public Json addGetObject() throws UnsupportedOperationException;
  public Json addGoObject() throws UnsupportedOperationException;
  public Json addArray() throws UnsupportedOperationException;
  public Json addGetArray() throws UnsupportedOperationException;
  public Json addGoArray() throws UnsupportedOperationException;
  
  public Json addObject(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addGetObject(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addGoObject(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addGetArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
  public Json addGoArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException;
}
