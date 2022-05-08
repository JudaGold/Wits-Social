import static org.junit.jupiter.api.Assertions.*;

import com.example.softwareproject.CreateUserClass;

import org.junit.jupiter.api.Test;

class CreateUserClassTest {
    @Test
    public void UserClassTest() {
        CreateUserClass fakeUser = new CreateUserClass("Elementrix08",
                "verushannaidoo@gmail.com", "0615805094", "Elementrix",
                "Verushan Naidoo", "Talk is cheap, show me the code", "-");

        assertEquals(fakeUser.getName(), "Verushan Naidoo");
        assertEquals(fakeUser.getUsername(), "Elementrix08");
        assertEquals(fakeUser.getBio(), "Talk is cheap, show me the code");
    }
}