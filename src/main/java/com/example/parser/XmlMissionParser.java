package com.example.parser;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.example.exception.InvalidMissionFormatException;
import com.example.model.Mission;
import java.io.File;
import java.io.IOException;

public class XmlMissionParser implements MissionParser {
    private final XmlMapper xmlMapper;

    public XmlMissionParser() {
        this.xmlMapper = new XmlMapper();
    }

    @Override
    public Mission parse(File file) throws InvalidMissionFormatException {
        try {
            Mission mission = xmlMapper.readValue(file, Mission.class);
            validateMission(mission);
            return mission;
        } catch (IOException e) {
            throw new InvalidMissionFormatException("Ошибка парсинга XML файла: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supportsFormat(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".xml");
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
    }
}
