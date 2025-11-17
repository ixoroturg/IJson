package test;

import java.net.*;
import java.net.http.*;
import ixoroturg.json.*;

public class KeyCloakApiTest {
  public static void test(){
    HttpRequest request = HttpRequest.newBuilder()
      .get()
      .uri(new URL("http://hydroweb.meteo.ru:8081/realms/testrealm/.well-known/openid-configuration"));
      ;
  }
}
