package ru.mephi.vikingdemo.model;

public record EquipmentItemEntity(
        Integer id,
        Integer vikingId,
        String name,
        String quality
) {
}
