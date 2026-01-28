package org.example.admin.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class EventDTO {
    @NotBlank(message = "field is required")
    private String title;

    @NotBlank(message = "field is required")
    private String description;

    @NotNull(message = "field is required")
    @Future
    private LocalDate date;

    @NotBlank(message = "field is required")
    private String location;

    @NotNull(message = "field is required")
    @Min(value = 1, message = "Capacity must be greater than 0")
    private int capacity;

    @NotNull(message = "field is required")
    @Min(value = 1, message = "price must be greater than 0")
    private double price;

    public String getTitle() {  return title;  }
    public String getDescription() {  return description;  }
    public LocalDate getDate() {  return date;  }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public double getPrice() { return price; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setLocation(String location) { this.location = location; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setPrice(double price) { this.price = price; }
}
