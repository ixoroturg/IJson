package ixoroturg.json;

import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.io.InputStream;

public class InputBuffer {

	ByteBuffer buffer = ByteBuffer.allocate(IJsonSetting.BUFFER_SIZE);
	InputStream input = null;
	Channel channel = null;
}
