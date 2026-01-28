package org.example.admin.service;

import org.example.admin.DTO.EventDTO;
import org.example.admin.model.Event;
import org.example.admin.repository.EventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepo eventRepository;

    public Event saveEvent(EventDTO eventDTO) {
        Event event = new Event(eventDTO.getTitle(), eventDTO.getDescription(), eventDTO.getDate(),
                eventDTO.getLocation(), eventDTO.getCapacity(), eventDTO.getPrice());
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Event updateEvent(Long id, EventDTO eventDTO) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setTitle(eventDTO.getTitle());
                    event.setDescription(eventDTO.getDescription());
                    event.setDate(eventDTO.getDate());
                    event.setLocation(eventDTO.getLocation());
                    event.setCapacity(eventDTO.getCapacity());
                    event.setPrice(eventDTO.getPrice());
                    return eventRepository.save(event);
                })
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    public Event findByTitle(String title) {
        return eventRepository.findByTitle(title);
    }

    public boolean deleteEvent(Long id) {
        eventRepository.deleteById(id);
        Event event = getEventById(id);
        return event == null;
    }

    public Long countEvents() {
        return eventRepository.count();
    }
}

