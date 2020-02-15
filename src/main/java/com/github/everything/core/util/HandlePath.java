package com.github.everything.core.util;

import lombok.Data;

import java.util.Set;
@Data
public class HandlePath {
    private Set<String> includePath;
    private Set<String> excludePath;
}
