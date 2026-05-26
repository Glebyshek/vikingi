package ru.mephi.vikingdemo.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Форма бороды викинга")
public enum BeardStyle {
    SHORT,
    LONG,
    BRAIDED,
    FORKED,
    CLEAN_SHAVEN
}
