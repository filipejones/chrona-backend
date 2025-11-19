package com.chrona.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ClientRequest {
    @NotBlank
    private String name;

    private String status;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String notes;
}
