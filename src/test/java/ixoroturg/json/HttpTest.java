package ixoroturg.json;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.*;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import java.net.*;
import java.io.*;

import ixoroturg.json.provider.*;

public class HttpTest {

	@Test
	public void test() throws Exception{

		HttpServer server = HttpServer.create(new InetSocketAddress(8083),1);
		server.createContext("/json", ex -> {
			System.out.println("Запрос получен");
			ex.sendResponseHeaders(200, 0);
			ex.getResponseBody().close();
		});
		server.start();

		Thread.sleep(5000);
		
		Json req = IJson.ofObject()
			.putGoArray("users")
				.addGoObject()
				.put("username","ixoroturg")
				.put("age",23)
				.put("email","ixoroturg@yandex.ru")
				.back()
				.addGoObject()
					.put("username","EcTeeZ")
					.put("age", 22)
					.put("email","EcTeeZ@gmail.ru")
				.back(0)
				.put("totalUsers",2)
				.put("host","10.20.139.70");
	
		HttpClient client = HttpClient.newHttpClient();

		String query = "http://localhost:8083/json";

		
		HttpRequest request = HttpRequest.newBuilder()
			.uri(new URI(query))
			.POST(IJsonBodyPublisher.of(req))
			.build();

		CompletableFuture<HttpResponse<Json>> response = client.sendAsync(request, new IJsonBodyHandler());
		System.out.println("Запрос отправлен");
		Json js = response.get().body();
		System.out.println("Ответ получен");
		IJsonSetting.AUTO_FLUSH = true;
		if(js != null){
			js.writeToFormat(new FileOutputStream("climatResponse"));
		} else {
			System.out.println("Ответ пустой");
		}
		server.stop(5);
		// System.out.println(js);
	}
}

