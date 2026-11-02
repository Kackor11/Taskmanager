package com.example.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // To wymusza użycie pliku application-test.properties!
class TaskmanagerApplicationTests {

	@Test
	void contextLoads() {
		// Ten test po prostu weryfikuje, czy aplikacja wstaje bez błędów.
	}

}