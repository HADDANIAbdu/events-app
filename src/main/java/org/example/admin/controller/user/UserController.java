package org.example.admin.controller.user;

import jakarta.validation.Valid;
import org.example.admin.DTO.EventDTO;
import org.example.admin.DTO.UserDTO;
import org.example.admin.DTO.addEventDTO;
import org.example.admin.model.AppUser;
import org.example.admin.payload.ResponseMessage;
import org.example.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/admins")
    public ResponseEntity<ResponseMessage> getAllAdmins() {
        List<AppUser> admins = userService.getAllUsers();
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Admins found successfully !",admins
        ));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createUser(@Valid @RequestBody UserDTO userDTO){
        AppUser appUser = userService.registerNewUser(userDTO.getFullName(), userDTO.getEmail(),
                userDTO.getPassword(), userDTO.getRole());
        if(appUser == null) return ResponseEntity.ok().body(new ResponseMessage("error",
                    "Failed to Create new User","null"));
        return ResponseEntity.ok().body(new ResponseMessage("success",
                "User Created Successfully !", appUser));
    }

    @GetMapping("/admins/{id}")
    public ResponseEntity<ResponseMessage> getAdmin(@PathVariable int id) {
        AppUser admin = userService.findUserById(id);
        if (admin != null) return ResponseEntity.ok().body(new ResponseMessage(
                "success","Admin found successfully.",admin
        ));
        return ResponseEntity.status(404).body(new ResponseMessage("error",
                "Admin not found","null"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseMessage> updateUser(@PathVariable int id, @RequestBody @Valid UserDTO userDTO) {
        AppUser user = userService.updateUser(id, userDTO);
        if(user != null) return ResponseEntity.ok().body(new ResponseMessage(
                "success","user Updated Successfully.", user
        ));
        else return ResponseEntity.ok().body(new ResponseMessage(
                "error","cannot update user with id "+id,"null"
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable int id) {
        AppUser user = userService.findUserById(id);
        if(user != null && userService.deleteUser(id)) {
            return ResponseEntity.ok().body(new ResponseMessage(
                    "success","user Deleted Successfully.", "null"
            ));
        }
        return ResponseEntity.ok().body(new ResponseMessage(
                "error","cannot delete user with id "+id,"null"
        ));
    }

    @GetMapping("/admins/count")
    public ResponseEntity<ResponseMessage> CountAdmins() {
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Admins counted successfully.", userService.countAdmins()
        ));
    }

    @GetMapping("/participants")
    public ResponseEntity<ResponseMessage> getAllParticipants(){
        List<AppUser> participants = userService.getAllParticipants();
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Participants found successfully !",participants
        ));
    }

    @GetMapping("/participants/{id}")
    public ResponseEntity<ResponseMessage> getParticipant(@PathVariable int id){
        AppUser participant = userService.findUserById(id);
        if(participant != null) return ResponseEntity.ok().body(new ResponseMessage(
                "success","Participant found successfully.", participant
        ));
        return ResponseEntity.ok().body(new ResponseMessage(
                "error","Participant not found","null"
        ));
    }

    @PostMapping("/participants/{id}/add-events/{eventId}")
    public ResponseEntity<ResponseMessage> addEvent(@PathVariable int id, @PathVariable int eventId) {
        AppUser participant = userService.addEventToParticipant(id, eventId);
        if(participant == null) return ResponseEntity.ok().body(new ResponseMessage(
                "error","Failed to add Event, check event id","null"
        ));
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Event Added Successfully !", participant
        ));
    }

    @DeleteMapping("/participants/{id}/delete-event/{eventId}")
    public ResponseEntity<ResponseMessage> deleteEvent(@PathVariable int id, @PathVariable int eventId){
        AppUser participant = userService.deleteEventForParticipant(id, eventId);
        if(participant == null) return ResponseEntity.ok().body(new ResponseMessage(
                "error","Failed to delete Event, check event id","null"
        ));
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Event Deleted Successfully !", participant
        ));
    }

    @GetMapping("/participants/{id}/events")
    public ResponseEntity<ResponseMessage> getParticipantEvents(@PathVariable int id){
        AppUser participant = userService.findUserById(id);
        if (participant.getEvents() == null) return ResponseEntity.ok().body(new ResponseMessage(
                "error", "Participants has not registered in no event","null"
        ));
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Participant Events found successfully !",participant.getEvents()
        ));
    }

    @GetMapping("/participants/{id}/payments")
    public ResponseEntity<ResponseMessage> getParticipantPayments(@PathVariable int id){
        AppUser participant = userService.findUserById(id);
        if (participant.getPayments() == null) return ResponseEntity.ok().body(new ResponseMessage(
                "error", "Participants has no payments yet","null"
        ));
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Participant Payments found successfully !",participant.getPayments()
        ));
    }

    @GetMapping("/participants/count")
    public ResponseEntity<ResponseMessage> CountParticipants() {
        return ResponseEntity.ok().body(new ResponseMessage(
                "success","Participants counted successfully.",
                userService.countParticipants()
        ));
    }
}