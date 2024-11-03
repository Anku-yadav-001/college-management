package com.yaduvanshi_brothers.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ApiApplication.class)
@ActiveProfiles("prod")
class ApiApplicationTests {

	@Test
	void contextLoads() {
	}
}
