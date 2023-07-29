package ru.practicum.ewm.event.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class Location {
    @NotNull
    Float lat;
    @NotNull
    Float lon;
}
