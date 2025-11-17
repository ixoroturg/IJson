package test;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.io.*;

//import java.net.http.*;
import ixoroturg.json.*;

public class KeyCloakApiTest {
  public static void test() throws Exception{
    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .uri(new URI("http://hydroweb.meteo.ru:8081/realms/testrealm/.well-known/openid-configuration"))
      .build()
      ;
    
    HttpResponse<InputStream> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofInputStream());
    Json js = IJson.of(response.body());
    System.out.println(js.toStringFormat());
  }
}
