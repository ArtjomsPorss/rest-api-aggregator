package com.artjomsporss.restapiaggregator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@ExtendWith(MockitoExtension.class)
public class FinnhubRestCallerImplTest {

    private static final String TOKEN = "TEST TOKEN";

    @Spy
    static FinnhubRestCallerImpl restCaller;

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

//    @BeforeAll
//    public static void setToken() {
//        ReflectionTestUtils.setField(restCaller, "token", TOKEN);
//    }
//
    @Test
    public void testSetRequestToken_called() {
        restCaller.setup();
        verify(restCaller).setRequestToken();
    }




    

}
