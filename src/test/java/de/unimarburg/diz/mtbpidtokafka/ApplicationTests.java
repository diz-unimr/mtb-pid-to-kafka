package de.unimarburg.diz.mtbpidtokafka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.unimarburg.diz.mtbpidtokafka.utils.StringCreatorFromArray;
@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void createInStringPidsArrayTest() {
		String[] pids = {"1", "2", "3"};
		String sql_string = StringCreatorFromArray.createInStringPidsArray(pids);
		assertEquals("('1', '2', '3')", sql_string);
	}


}
