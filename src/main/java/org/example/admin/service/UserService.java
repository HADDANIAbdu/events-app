package org.example.admin.service;

import org.example.admin.DTO.UserDTO;
import org.example.admin.model.AppUser;
import org.example.admin.model.Event;
import org.example.admin.model.Payment;
import org.example.admin.repository.EventRepo;
import org.example.admin.repository.PaymentRepo;
import org.example.admin.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + appUser.getRole()));
        return new User(username, appUser.getPassword(), authorities);
    }

    public AppUser findUserByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }

    public AppUser registerNewParticipant(String fullName, String email, String password) {
        if (userRepo.findByUsername(email).isPresent()) return null;
        AppUser user = new AppUser();
        user.setFullName(fullName);
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("participant");
        return userRepo.save(user);
    }

    public AppUser registerNewUser(String fullName, String email, String password, String role) {
        if(userRepo.findByUsername(email).isPresent()) return null;
        AppUser user = new AppUser();
        user.setFullName(fullName);
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepo.save(user);
    }

    public AppUser addEventToParticipant(int participantId, int eventId){
        AppUser participant = userRepo.findById(participantId).orElse(null);
        Event event = eventRepo.findById(eventId);
        if (event == null) return null;
        participant.AddEvent(event);
        return userRepo.save(participant);
    }

    public AppUser deleteEventForParticipant(int participantId, int eventId){
        AppUser participant = userRepo.findById(participantId).orElse(null);
        Event event = eventRepo.findById(eventId);
        if (event == null) return null;
        participant.getEvents().remove(event);
        return userRepo.save(participant);
    }

    public List<AppUser> getAllUsers() {
        List<AppUser> users = new ArrayList<>();
        userRepo.findAll().forEach(user -> {
            if(user.getRole().equals("admin")) users.add(user);
        });
        return users;
    }

    public List<AppUser> getAllParticipants(){
        List<AppUser> participants = new ArrayList<>();
        userRepo.findAll().forEach(participant -> {
            if(participant.getRole().equals("participant")) participants.add(participant);
        });
        return participants;
    }

    public AppUser findUserById(int id) {
        return userRepo.findById(id).orElse(null);
    }

    public AppUser updateUser(int id, UserDTO userDTO) {
        return userRepo.findById(id).map(appUser -> {
            appUser.setFullName(userDTO.getFullName());
            appUser.setUsername(userDTO.getEmail());
            appUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            appUser.setRole(userDTO.getRole());
            return userRepo.save(appUser);
                }).orElseThrow(null);
    }

    public boolean deleteUser(int id) {
        userRepo.deleteById(id);
        AppUser user = userRepo.findById(id).orElse(null);
        return user == null;
    }
    public String findUserFullName(String email) {
        return userRepo.findByUsername(email).orElse(null).getFullName();
    }

    public Long countAdmins(){
        long count = 0;
        List<AppUser> users = userRepo.findAll();
        for(AppUser user : users){
            if(user.getRole().equals("admin")) count++;
        }
        return count;
    }

    public Long countParticipants(){
        long count = 0;
        List<AppUser> users = userRepo.findAll();
        for(AppUser user : users){
            if(user.getRole().equals("participant")) count++;
        }
        return count;
    }
}
