package ru.mephi.vikingdemo.model;

public record VikingEntity(
        Integer id,
        String name,
        int age,
        int heightCm,
        HairColor hairColor,
        BeardStyle beardStyle,
        String description
) {
}
