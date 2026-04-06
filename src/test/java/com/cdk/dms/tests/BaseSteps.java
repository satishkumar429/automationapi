package com.cdk.dms.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

import com.cdk.dms.config.Settings;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseSteps extends Settings {

    private static final Long RESPONSE_TIME_OUT_THRESHOLD = 60L;


    /**
     * Call GET request for the required specification
     */
    public Response createGetRequest(RequestSpecification requestSpecification) {
        return given()
                .spec(requestSpecification)
                .request(Method.GET)
                .then()
                .log()
                .all()
                .extract()
                .response();
    }

    public Response createDeleteRequest(RequestSpecification requestSpecification, String uriPath) {
        return given().
                spec(requestSpecification).
                delete(uriPath).
                thenReturn()
                .then()
                .log()
                .all()
                .extract()
                .response();
    }

    public static Response createPostRequest(RequestSpecification requestSpec, String uriPath,
                                             Object bodyPayload) {
        return given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(bodyPayload)
                .when()
                .post(uriPath)
                .then()
                .log()
                .all()
                .time(lessThan(RESPONSE_TIME_OUT_THRESHOLD), TimeUnit.SECONDS)
                .extract()
                .response();
    }

    public static Response createPutRequest(RequestSpecification requestSpec, String uriPath,
                                            Object bodyPayload) {
        return given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(bodyPayload)
                .when()
                .put(uriPath)
                .then()
                .log()
                .all()
                .time(lessThan(RESPONSE_TIME_OUT_THRESHOLD), TimeUnit.SECONDS)
                .extract()
                .response();
    }

    public Response getResponse(String payload, String url, Method requestType, String basePath, String accessToken,
                                boolean excludeDepartmentHeaderForGet) {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        if (basePath.toLowerCase(Locale.ROOT).startsWith("https://") || basePath.toLowerCase(Locale.ROOT).startsWith("http://")) {
            builder.setBaseUri(basePath);
        }
        Map<String, String> headers = getHeadersForAuth(accessToken);
        if (requestType == Method.GET && excludeDepartmentHeaderForGet) {
            headers.remove("x-department-id");
        }
        builder
                .addHeaders(headers)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        RequestSpecification requestSpecification = builder.build();
        Response response;
        switch (requestType) {
            case POST:
                response = createPostRequest(requestSpecification, url, payload);
                break;
            case PUT:
                response = createPutRequest(requestSpecification, url, payload);
                break;
            case DELETE:
                response = createDeleteRequest(requestSpecification, url);
                break;
            case GET:
            default:
                RequestSpecBuilder getBuilder = new RequestSpecBuilder();
                getBuilder.setBaseUri(basePath);
                getBuilder.setBasePath(url);
                getBuilder.addHeaders(headers).log(LogDetail.ALL);
                requestSpecification = getBuilder.build();
                response = createGetRequest(requestSpecification);
                break;
        }
        log.info("Response is: " + response.asString());
        log.info("Status code is: " + response.getStatusCode());

        return response;
    }

    /**
     * Get All Headers
     */
    private Map<String, String> getHeadersForAuth(String accessToken) {
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", "Bearer "+accessToken);
        map.put("x-enterprise-id", getEnterpriseId());
        map.put("x-store-id", getStoreId());
        map.put("x-department-id",getDepartmentId());
        map.put("REMOTE_USER", getRemoteUser());
        map.put("x-department-name","TOM-I");
        return map;
    }
}
