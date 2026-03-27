package com.example.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.exception.InvalidMissionFormatException;
import com.example.model.Mission;
import java.io.File;
import java.io.IOException;

public class JsonMissionParser implements MissionParser {
    private final ObjectMapper objectMapper;

    public JsonMissionParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    @Override
    public Mission parse(File file) throws InvalidMissionFormatException {
        try {
            Mission mission = objectMapper.readValue(file, Mission.class);
            validateMission(mission);
            return mission;
        } catch (IOException e) {
            throw new InvalidMissionFormatException("Ошибка парсинга JSON файла: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supportsFormat(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".json");
    }

    private void validateMission(Mission mission) throws InvalidMissionFormatException {
        if (mission == null) {
            throw new InvalidMissionFormatException("Миссия не может быть null");
        }
        if (mission.getMissionId() == null || mission.getMissionId().trim().isEmpty()) {
            throw new InvalidMissionFormatException("Отсутствует missionId");
        }
        if (mission.getCurse() == null) {
            throw new InvalidMissionFormatException("Отсутствует информация о проклятии");
        }
        if (mission.getSorcerers() == null || mission.getSorcerers().isEmpty()) {
            throw new InvalidMissionFormatException("Отсутствуют маги в миссии");
        }
    }
}
