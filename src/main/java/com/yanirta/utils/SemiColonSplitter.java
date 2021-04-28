package com.yanirta.utils;

import com.beust.jcommander.converters.IParameterSplitter;

import java.util.Arrays;
import java.util.List;

public class SemiColonSplitter implements IParameterSplitter {
    public List<String> split(String value) {
        return Arrays.asList(value.split(";"));
    }
}
