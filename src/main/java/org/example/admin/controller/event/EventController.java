package org.example.admin.controller.event;

import org.example.admin.DTO.EventDTO;
import org.example.admin.model.Event;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/events")
@CrossOrigin("*")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createEvent(@RequestBody EventDTO eventDTO) {
        Event event = eventService.saveEvent(eventDTO);
        if(event != null) return ResponseEntity.ok().body(new ResponseMessage(
                "success","Event added successfully", event
        ));
        else return ResponseEntity.ok().body(new ResponseMessage(
                "error","error adding event","null"
        ));
    }

    @GetMapping("/")
    public ResponseEntity<ResponseMessage> getAllEvents() {
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Events found Successfully", eventService.getAllEvents()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        if(event != null) return ResponseEntity.ok().body(new ResponseMessage(
                "success","Event found successfully", event
        ));
        else return ResponseEntity.ok().body(new ResponseMessage(
                "error","error finding event","null"
        ));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseMessage> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        Event event = eventService.updateEvent(id, eventDTO);
        if(event != null) return ResponseEntity.ok().body(new ResponseMessage(
                "success","Event updated successfully", event
        ));
        else return ResponseEntity.ok().body(new ResponseMessage(
                "error","error updating event"+id,"null"
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteEvent(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        if(event != null && eventService.deleteEvent(id)) return ResponseEntity.ok().body(
                new ResponseMessage("success","Event deleted successfully","id: "+id)
        );
        else return ResponseEntity.ok().body(new ResponseMessage(
                "error","error deleting event"+id,"null"
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<ResponseMessage> getEventsCount() {
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Events counted Successfully", eventService.countEvents()
        ));
    }
}

