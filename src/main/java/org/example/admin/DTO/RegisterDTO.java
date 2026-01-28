package org.example.admin.DTO;


import jakarta.validation.constraints.*;

public class RegisterDTO {
    @NotBlank(message = "FullName is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @Size(min = 8, message = "minimum password length is 8 characters")
    private String password;
    private String confirmPassword;


    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String username) { this.email = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
