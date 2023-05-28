package dev.unjinxed.unjinxedmicroservices.utils;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public class MockitoInit {
    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }
}
