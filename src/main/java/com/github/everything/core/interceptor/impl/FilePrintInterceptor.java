package com.github.everything.core.interceptor.impl;

import com.github.everything.core.util.FileConvertThing;
import com.github.everything.core.interceptor.FileInterceptor;
import com.github.everything.core.model.Thing;

import java.io.File;

public class FilePrintInterceptor implements FileInterceptor {
    @Override
    public void apply(File file) {
        //Thing thing = FileConvertThing.convert(file);
        System.out.println(file);

    }
}
