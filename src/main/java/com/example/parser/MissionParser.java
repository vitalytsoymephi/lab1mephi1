package com.example.parser;

import com.example.exception.InvalidMissionFormatException;
import com.example.model.Mission;
import java.io.File;

public interface MissionParser {
    Mission parse(File file) throws InvalidMissionFormatException;
    boolean supportsFormat(File file);
}
