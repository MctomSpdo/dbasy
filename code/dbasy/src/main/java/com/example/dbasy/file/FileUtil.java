package com.example.dbasy.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static void saveString(File f, String s) throws IOException {
        FileWriter wr = new FileWriter(f);
        wr.write(s);
        wr.flush();
        wr.close();
    }
}
