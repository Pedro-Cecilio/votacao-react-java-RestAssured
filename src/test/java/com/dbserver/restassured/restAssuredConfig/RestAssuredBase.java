package com.dbserver.restassured.restAssuredConfig;

import static io.restassured.RestAssured.*;
import org.junit.jupiter.api.BeforeAll;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestAssuredBase {
    @BeforeAll
    public static void setup() {
        baseURI = "http://localhost";
        port = 8080;
    }
}
