package tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InfoHashUrlEncoderTest {

	@Test
	void encode() {
		assertEquals(
				"%d6%9f%91%e6%b2%ae%4c%54%24%68%d1%07%3a%71%d4%ea%13%87%9a%7f",
				InfoHashUrlEncoder.encode("d69f91e6b2ae4c542468d1073a71d4ea13879a7f")
		);
	}
}