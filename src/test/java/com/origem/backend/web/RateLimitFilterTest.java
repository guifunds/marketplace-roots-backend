package com.origem.backend.web;

import com.origem.backend.config.AppProperties;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitFilterTest {

    @Test
    void blocksRequestsAfterLimitIsReached() throws Exception {
        AppProperties.RateLimit rateLimit = new AppProperties.RateLimit(3);
        AppProperties appProperties = new AppProperties(null, null, rateLimit, List.of("http://localhost:3000"));
        RateLimitFilter filter = new RateLimitFilter(appProperties);
        FilterChain chain = Mockito.mock(FilterChain.class);

        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest request = signupRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, chain);
            assertThat(response.getStatus()).isEqualTo(200);
        }

        MockHttpServletRequest fourthRequest = signupRequest();
        MockHttpServletResponse fourthResponse = new MockHttpServletResponse();
        filter.doFilter(fourthRequest, fourthResponse, chain);

        assertThat(fourthResponse.getStatus()).isEqualTo(429);
        assertThat(fourthResponse.getContentAsString()).contains("Muitas tentativas");
    }

    @Test
    void doesNotLimitOtherEndpoints() throws Exception {
        AppProperties.RateLimit rateLimit = new AppProperties.RateLimit(1);
        AppProperties appProperties = new AppProperties(null, null, rateLimit, List.of("http://localhost:3000"));
        RateLimitFilter filter = new RateLimitFilter(appProperties);
        FilterChain chain = Mockito.mock(FilterChain.class);

        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/signups/some-id");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, chain);
            assertThat(response.getStatus()).isEqualTo(200);
        }
    }

    private MockHttpServletRequest signupRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/signups");
        request.setRemoteAddr("203.0.113.10");
        return request;
    }
}
