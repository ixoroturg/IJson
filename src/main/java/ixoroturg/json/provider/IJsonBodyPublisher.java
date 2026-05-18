package ixoroturg.json.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpRequest.BodyPublisher;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import ixoroturg.json.*;

public class IJsonBodyPublisher implements BodyPublisher{
	private Json js;
	private int length;

	public static IJsonBodyPublisher of(Json js){
		return new IJsonBodyPublisher(js);
	}
	public IJsonBodyPublisher(Json json){
		js = json;
		length = js.buffSize();
	}
	@Override
	public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
		IJsonBodySubscription sub = new IJsonBodySubscription(js, subscriber, length);
		subscriber.onSubscribe(sub);
	}

	@Override
	public long contentLength() {
		return length;
	}
	
	private class IJsonBodySubscription implements Subscription{
		private Subscriber<? super ByteBuffer> subscriber;
		private int length;
		private ByteBuffer buffer = null;
		IJsonBodySubscription(Json json, Subscriber<? super ByteBuffer> sub, int length){
			js = json;
			subscriber = sub;
			this.length = length;
		}

		@Override
		public void request(long n) {
			if(buffer != null){
				subscriber.onComplete();
				return;
			}
			ByteArrayOutputStream stream = null;
			try{
				stream = new ByteArrayOutputStream(length);
				js.writeTo(stream);
				buffer = ByteBuffer.wrap(stream.toByteArray());
			}catch(IOException e){
				e.printStackTrace();
				subscriber.onError(e);
			}
			subscriber.onNext(buffer);
		}
		@Override
		public void cancel() {}
	}
}
