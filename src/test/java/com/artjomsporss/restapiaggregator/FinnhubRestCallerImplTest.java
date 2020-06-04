package com.artjomsporss.restapiaggregator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@ExtendWith(MockitoExtension.class)
public class FinnhubRestCallerImplTest {

    private static final String TOKEN_PREFIX = "?token=";
    private static final String TOKEN = "TEST_TOKEN";
    private static final String DOMAIN = "https://finnhub.io/api/v1/stock/";
    private static final String ENDPOINT = "exchange";

    @Spy
    static FinnhubRestCallerImpl restCaller;

    @BeforeEach
    public void setToken() {
        ReflectionTestUtils.setField(restCaller, "token", TOKEN);
    }

    @Test
    public void test_InstanceNotNull() {
        assertNotNull(restCaller, "A mock class must be present, cannot be null");
    }

    @Test
    public void testSetup_CreatesNewRestTemplate() {
        restCaller.setup();
        Try<Object> tryField =  ReflectionUtils.tryToReadFieldValue(FinnhubRestCallerImpl.class, "rest", restCaller);
        assertTrue(tryField.toOptional().isPresent());
    }

    @Test
    public void testSetup_SetRequestTokenCalled() {
        restCaller.setup();
        verify(restCaller).setRequestToken();
    }

    @Test
    public void testGenerateToken_returnsExpectedToken() {
        String generatedToken = restCaller.generateToken();
        String expectedToken = "?token=" + TOKEN;
        assertEquals(generatedToken, expectedToken, "Generated and Expected token with prefix must be equal");
    }

    @Test
    public void testGenerateUrl_generateTokenCalled() {
        restCaller.generateUrl("test");
        verify(restCaller).generateUrl(anyString());
    }

    @Test
    public void testGenerateUrl_returnsExpectedUrl() {
        String generatedUrl = restCaller.generateUrl(ENDPOINT);
        String expectedUrl = DOMAIN + ENDPOINT + TOKEN_PREFIX + TOKEN;
        assertEquals(expectedUrl, generatedUrl, "Generated and expeted URL must be equal");
    }

    @Test
    public void testCallRestTemplate_templateIsNotNull() {
        Try<Object> tryField = ReflectionUtils.tryToReadFieldValue(FinnhubRestCallerImpl.class, "rest", restCaller);
        assertTrue(tryField.toOptional().isPresent());
    }

    @Test
    public void testGetExchange_generateUrlIsCalled() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(restCaller, "rest", mockRestTemplate);

        restCaller.getStockExchangeList();
        verify(restCaller).generateUrl(anyString());
    }

    @Test
    public void testGetExchange_getForObjectIsCalled() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(restCaller, "rest", mockRestTemplate);

        restCaller.getStockExchangeList();
        verify(mockRestTemplate).getForObject(anyString(), any());
    }
}
