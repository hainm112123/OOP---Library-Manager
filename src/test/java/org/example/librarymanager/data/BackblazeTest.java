package org.example.librarymanager.data;

import org.example.librarymanager.services.Backblaze;
import org.junit.jupiter.api.Test;

import java.io.File;

public class BackblazeTest {
    @Test
    public void testConnection() {
        Backblaze.getInstance();
    }

    @Test
    public void testUpload() {
        File file = new File("C:\\yuuhi\\courses\\Year 2 - Sem 1\\OOP - INT2204 7\\code\\LibraryManager\\src\\main\\resources\\org\\example\\librarymanager\\image\\UserProfile.png");
        System.out.println(Backblaze.getInstance().upload("default-profile.png", file.getAbsolutePath()));
    }
}
