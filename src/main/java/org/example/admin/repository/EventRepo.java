package org.example.admin.repository;

import org.example.admin.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepo extends JpaRepository<Event, Long> {
    Event findById(long id);
    Event findByTitle(String title);
}

