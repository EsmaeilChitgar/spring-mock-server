package com.example.spring_mock_server.test_all_in_one;

import org.mockserver.mock.action.ExpectationCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;

public class MyCustomCallback implements ExpectationCallback {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (httpRequest.getPath().getValue().endsWith("/callback")) {
            return response()
                    .withStatusCode(200)
                    .withBody("Callback executed with body: "/* + httpRequest.getBodyAsString()*/);
        } else {
            return notFoundResponse();
        }
    }
}

