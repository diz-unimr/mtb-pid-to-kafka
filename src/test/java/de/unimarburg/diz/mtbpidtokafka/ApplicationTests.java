/*
 This file is part of MTB-ID-TO-KAFKA.

MTB-ID-TO-KAFKA - Get a CSV als Plane Text from MTB (Onkostar), extract the PIDs from the csv file, search all the oder_ids for each PID in NexusDB and produce the info as JSON in a Kafka
topic.

Copyright (C) 2024  Datenintegrationszentrum Philipps-Universität Marburg

MTB-ID-TO-KAFKA  is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

MTB-ID-TO-KAFKA is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package de.unimarburg.diz.mtbpidtokafka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import de.unimarburg.diz.mtbpidtokafka.utils.CustomKafkaKeyGenerator;
import de.unimarburg.diz.mtbpidtokafka.utils.CustomDateFormatter;


class ApplicationTests {

	@Test
	void contextLoads() {
	}


	@Test
	public void customKafkaKeyGeneratorTest() {
		String einsendenummer = "NXP_H/2024/012345";
		String patientenId = "11111112";
		String expected = "H12345-24_PID11111112";
		String result = CustomKafkaKeyGenerator.generateCustomPatientIdentifier(einsendenummer, patientenId);
		assertEquals(expected, result);
	}

	@Test
	public void customDateFormatterTest() {
		String givendate = "07.06.2016";
		String expecteddate = "2016-06-07";
		String result = CustomDateFormatter.convertDateFormat(givendate);
		assertEquals(expecteddate, result);

	}

}
