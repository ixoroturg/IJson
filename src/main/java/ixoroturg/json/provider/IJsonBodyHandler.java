package ixoroturg.json.provider;

import ixoroturg.json.*;

import java.io.ByteArrayInputStream;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;

public class IJsonBodyHandler implements BodyHandler<Json>{

	@Override
	public BodySubscriber<Json> apply(ResponseInfo responseInfo) {
		HttpHeaders headers = responseInfo.headers();
		
		long bufferSize = headers.firstValueAsLong("Content-Length").orElse(0);
		return new IJsonBodySubscriber(bufferSize);
	}
	
	class IJsonBodySubscriber implements BodySubscriber<Json>{
		ByteBuffer buffer = null;
		CompletableFuture<Json> result = new CompletableFuture<>();
		Json js = null;
		// int bufferSize = -1;
		List<ByteBuffer> received = new LinkedList<>();
		IJsonBodySubscriber(long bufferSize){
			// this.bufferSize = (int) bufferSize;
			buffer = ByteBuffer.allocate((int)bufferSize);
			// buffer = ByteBuffer.allocate((int) bufferSize);
		}
		@Override
		public void onSubscribe(Subscription subscription) {
			subscription.request(Long.MAX_VALUE);
		}

		@Override
		public void onNext(List<ByteBuffer> item) {

			for(ByteBuffer chunk: item){
				buffer.put(chunk);
			}
			// received.addAll(item);
			// if(item.size() != 0 && item.get(0).limit() == bufferSize){
			// 	buffer = item.get(0);
			// } else{
			// 	if(buffer == null){
			// 		buffer = ByteBuffer.allocate(bufferSize);
			// 	}
			// 	for(ByteBuffer buf: item){
			// 		buf.position(0);
			// 		buffer.put(buf);
			// 	}
			//
			// }
		}

		@Override
		public void onError(Throwable throwable) {
			// RuntimeException e = new RuntimeException("Error in http response");
			// e.initCause(throwable);
			// throw e;
			result.completeExceptionally(throwable);
		}


		@Override
		public void onComplete() {

			
			CompletableFuture.supplyAsync(()->{
				
				if(buffer.capacity() == 0){
					return null;
				}

				// ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
				// for(ByteBuffer chunk: received){
				// 	buffer.put(chunk);
				// }
				// for(ByteBuffer buf: received){
				// 	buf.re
				// }
				// int remaining = 0;
				// for(ByteBuffer buf: received){
				// 	remaining += buf.remaining();
				// }
				// byte[] bufs = new byte[remaining];
				// int from = 0;
				// for(ByteBuffer buf: received){
				// 	int length = buf.remaining();
				// 	buf.get(bufs, from, length);
				// 	from += length;
				// }

				buffer.clear();
				ByteArrayInputStream input = new ByteArrayInputStream(buffer.array());
				Json js = IJson.of(input);
				// try{
					// js = IJson.of(new ByteArrayInputStream(bufs));
				// }catch(JsonException e){
				// 	// return null;
				// 	RuntimeException e2 = new RuntimeException("Error while parsing json");
				// 	e2.initCause(e);
				// 	throw e2;
				// }
				return js;
			})
			.thenAccept(js -> {
				result.complete(js);
			});
		}

		@Override
		public CompletionStage<Json> getBody() {
			return result;
		}
	}
}
