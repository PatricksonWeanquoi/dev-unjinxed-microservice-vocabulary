package dev.unjinxed.unjinxedmicroservices;

import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Test-Case: Unjinxed Microservices Application")
@SpringBootTest
class UnjinxedMicroservicesApplicationTests {

	@Test
	@DisplayName("Load Spring-Boot Context")
	@Disabled("Not In Focus")
	void contextLoads() {
		fail("Adding test for context");
	}

}
