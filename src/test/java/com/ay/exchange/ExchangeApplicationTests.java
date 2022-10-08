package com.ay.exchange;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class ExchangeApplicationTests {


	@Test
	void contextLoads() {
		String s="qwer";
		Stream.of(s.split(""))
				.sorted(Collections.reverseOrder())
				.collect(Collectors.joining())
	}

}
