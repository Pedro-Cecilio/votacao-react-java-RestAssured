package com.dbserver.restassured.restAssuredConfig;

import static io.restassured.RestAssured.*;
import org.junit.jupiter.api.BeforeAll;

public class RestAssuredBase {
    @BeforeAll
    public static void setup() {
        baseURI = "http://localhost";
        port = 8080;
    }
}
