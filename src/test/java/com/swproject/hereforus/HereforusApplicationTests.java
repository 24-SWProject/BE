package com.swproject.hereforus;

import com.swproject.hereforus.service.event.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HereforusApplicationTests {

	@Autowired
	private EventService eventService;

	@Test
	void testFetchFestivals() {
		eventService.fetchFestivals();
	}

}
