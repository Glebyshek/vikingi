package ru.mephi.vikingdemo.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Цвет волос викинга")
public enum HairColor {
    Blond, 
    Red, 
    Brown, 
    Black, 
    Gray
}
