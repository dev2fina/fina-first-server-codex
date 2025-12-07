package net.fina.server.calendar.model;

import net.fina.server.calendar.entity.CalendarEvent;

import java.util.ArrayList;
import java.util.List;

public class CalendarEventMetaModelHelper {
    public static CalendarEvent toEntity(CalendarEventMetaModel model) {
        CalendarEvent entity = new CalendarEvent();
        entity.setId(model.getId());
        entity.setDate(model.getDate());
        entity.setComment(model.getComment());
        entity.setEventType(model.getEventType());
        entity.setGroupUUID(model.getGroupUUID());
        return entity;
    }

    public static CalendarEventMetaModel fromEntity(CalendarEvent entity) {
        CalendarEventMetaModel model = new CalendarEventMetaModel();
        model.setId(entity.getId());
        model.setDate(entity.getDate());
        model.setComment(entity.getComment());
        model.setEventType(entity.getEventType());
        model.setGroupUUID(entity.getGroupUUID());
        return model;
    }

    public static List<CalendarEventMetaModel> fromEntity(List<CalendarEvent> entities) {
        List<CalendarEventMetaModel> models = new ArrayList<>();
        for (CalendarEvent entity : entities) {
            models.add(fromEntity(entity));
        }
        return models;
    }
}
