package ru.liberation.semenov.commands;

import org.junit.jupiter.api.Test;
import ru.liberation.semenov.MainHandler;
import ru.liberation.semenov.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterCommandTest {

    RegisterCommand registerCommand = new RegisterCommand();

    @Test
    public void testExecuteSuccess() throws IOException {
        Path authDataPath = Files.createTempFile("test-users", null);
        MainHandler.setPathToAuthData(authDataPath.toString());
        String args = "4John12345";
        String result = registerCommand.execute(args);
        assertEquals(Response.SUCCESS.name(), result);
        assertTrue(MainHandler.getUsers().containsKey("John"));
        assertEquals("12345", MainHandler.getUsers().get("John"));
    }

    @Test
    public void testExecuteFailed() {
        MainHandler.getUsers().clear();
        MainHandler.getUsers().put("Alice", "password");
        String args = "5Alice12345";
        String result = registerCommand.execute(args);
        assertEquals(Response.FAILED.name(), result);
        assertTrue(MainHandler.getUsers().containsKey("Alice"));
        assertEquals("password", MainHandler.getUsers().get("Alice"));
    }
}

