package ru.liberation.semenov.commands;

import org.junit.jupiter.api.Test;
import ru.liberation.semenov.MainHandler;

import static org.junit.jupiter.api.Assertions.*;

class AuthCommandTest {

    AuthCommand authCommand = new AuthCommand(new MainHandler());

    @Test
    void testExecute_WithInvalidPassword_ShouldReturnFailedResponse() {
        String nickname = "testUser";
        String password = "testPassword";
        MainHandler.getUsers().put(nickname, password);
        String args = "8testUserWrongPassword";
        String result = authCommand.execute(args);
        assertTrue(result.contains("FAILED"));
        assertTrue(result.contains("Incorrect password"));
    }

    @Test
    void testExecute_WithNonExistentUser_ShouldReturnFailedResponse() {
        String args = "8unknownUser10password";
        String result = authCommand.execute(args);
        assertTrue(result.contains("FAILED"));
        assertTrue(result.contains("There's no user with this nickname"));
    }
}

