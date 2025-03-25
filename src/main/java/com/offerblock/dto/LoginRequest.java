package com.offerblock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Email must not be blank")
    @Size(max = 50)
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(max = 80)
    private String password;
}
