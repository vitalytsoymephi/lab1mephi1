package com.example.parser;

import com.example.exception.InvalidMissionFormatException;
import com.example.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxtMissionParser implements MissionParser {

    @Override
    public Mission parse(File file) throws InvalidMissionFormatException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            Mission mission = new Mission();
            List<Sorcerer> sorcerers = new ArrayList<>();
            List<Technique> techniques = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                parseLine(line, mission, sorcerers, techniques);
            }

            mission.setSorcerers(sorcerers);
            mission.setTechniques(techniques);
            validateMission(mission);

            return mission;
        } catch (IOException e) {
            throw new InvalidMissionFormatException("Ошибка чтения TXT файла: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supportsFormat(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".txt");
    }

    private void parseLine(String line, Mission mission, List<Sorcerer> sorcerers, List<Technique> techniques) {
        String[] parts = line.split(":", 2);
        if (parts.length < 2) return;

        String key = parts[0].trim();
        String value = parts[1].trim();

        // Основные поля
        switch (key) {
            case "missionId": mission.setMissionId(value); return;
            case "date": mission.setDate(value); return;
            case "location": mission.setLocation(value); return;
            case "outcome": mission.setOutcome(value); return;
            case "damageCost":
                try { mission.setDamageCost(Long.parseLong(value)); }
                catch (NumberFormatException e) { /* ignore */ }
                return;
            case "note": mission.setNote(value); return;
        }

        // Проклятие
        if (key.startsWith("curse.")) {
            if (mission.getCurse() == null) {
                mission.setCurse(new Curse());
            }
            String curseField = key.substring(6);
            if ("name".equals(curseField)) {
                mission.getCurse().setName(value);
            } else if ("threatLevel".equals(curseField)) {
                mission.getCurse().setThreatLevel(value);
            }
            return;
        }

        // Маги
        Pattern sorcererPattern = Pattern.compile("sorcerer\\[(\\d+)\\]\\.(\\w+)");
        Matcher sorcererMatcher = sorcererPattern.matcher(key);
        if (sorcererMatcher.matches()) {
            int index = Integer.parseInt(sorcererMatcher.group(1));
            String field = sorcererMatcher.group(2);

            while (sorcerers.size() <= index) {
                sorcerers.add(new Sorcerer());
            }

            Sorcerer sorcerer = sorcerers.get(index);
            if ("name".equals(field)) {
                sorcerer.setName(value);
            } else if ("rank".equals(field)) {
                sorcerer.setRank(value);
            }
            return;
        }

        // Техники
        Pattern techniquePattern = Pattern.compile("technique\\[(\\d+)\\]\\.(\\w+)");
        Matcher techniqueMatcher = techniquePattern.matcher(key);
        if (techniqueMatcher.matches()) {
            int index = Integer.parseInt(techniqueMatcher.group(1));
            String field = techniqueMatcher.group(2);

            while (techniques.size() <= index) {
                techniques.add(new Technique());
            }

            Technique technique = techniques.get(index);
            switch (field) {
                case "name": technique.setName(value); break;
                case "type": technique.setType(value); break;
                case "owner": technique.setOwner(value); break;
                case "damage":
                    try { technique.setDamage(Long.parseLong(value)); }
                    catch (NumberFormatException e) { /* ignore */ }
                    break;
            }
        }
    }

    private void validateMission(Mission mission) throws InvalidMissionFormatException {
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