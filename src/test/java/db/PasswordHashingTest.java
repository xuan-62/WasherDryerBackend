package db;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import static org.junit.Assert.*;

public class PasswordHashingTest {

	@Test
	public void testHashAndVerify() {
		String password = "mySecret123";
		String hash = BCrypt.hashpw(password, BCrypt.gensalt());
		assertTrue(BCrypt.checkpw(password, hash));
	}

	@Test
	public void testWrongPasswordFails() {
		String hash = BCrypt.hashpw("correct", BCrypt.gensalt());
		assertFalse(BCrypt.checkpw("wrong", hash));
	}

	@Test
	public void testSaltsAreUnique() {
		String password = "same";
		String hash1 = BCrypt.hashpw(password, BCrypt.gensalt());
		String hash2 = BCrypt.hashpw(password, BCrypt.gensalt());
		assertNotEquals(hash1, hash2);
		assertTrue(BCrypt.checkpw(password, hash1));
		assertTrue(BCrypt.checkpw(password, hash2));
	}
}
