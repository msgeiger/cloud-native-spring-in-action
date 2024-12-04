package com.polarbookshop.orderservice;

import com.polarbookshop.orderservice.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.ExpectedCount.never;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Testcontainers
public class SampleTests {

    private MockRestServiceServer mockServer;

    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        this.restTemplate = new RestTemplate();
        this.mockServer = MockRestServiceServer.bindTo(this.restTemplate).ignoreExpectOrder(true).build();
    }

    @Test
    public void performGet() {

        String responseBody =
            """
                 {
                        "bookIsbn": "1234567892",
                        "bookName": "Polar Journey - Iorek Polarson",
                        "bookPrice": 12.9,
                        "createdDate": "2024-07-07T00:58:50.044889Z",
                        "id": 12,
                        "lastModifiedDate": "2024-07-07T00:58:50.044889Z",
                        "quantity": 4,
                        "status": "ACCEPTED",
                        "version": 1
                    }

                """
            ;

        this.mockServer.expect(requestTo("/orders/1234567892")).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        this.restTemplate.getForObject("/orders/{isbn}", Order.class, 1234567892);

        // We are only validating the request. The response is mocked out.
        // hotel.getId() == 42
        // hotel.getName().equals("Holiday Inn")

        this.mockServer.verify();
    }
}

