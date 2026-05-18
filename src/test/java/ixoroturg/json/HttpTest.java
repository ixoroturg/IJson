package ixoroturg.json;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.*;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import java.net.*;
import java.io.*;

import ixoroturg.json.provider.*;

public class HttpTest {

	public void test() throws Exception{


		
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

		String query = "http://10.20.139.70/api/climat/v2";

		
		HttpRequest request = HttpRequest.newBuilder()
			.uri(new URI(query))
			.POST(IJsonBodyPublisher.of(req))
			.build();
		CompletableFuture<HttpResponse<Json>> response = client.sendAsync(request, new IJsonBodyHandler());
		System.out.println("Запрос отправлен");
		Json js = response.get().body();
		System.out.println("Ответ получен");
		IJsonSetting.AUTO_FLUSH = true;
		js.writeToFormat(new FileOutputStream("climatResponse"));
		// System.out.println(js);
	}
}

