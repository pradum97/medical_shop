package com.techwhizer.medicalshop;

import java.io.InputStream;

public class FileLoader {
    public InputStream load (String path){

      return   getClass().getResourceAsStream(path);
    }
}
