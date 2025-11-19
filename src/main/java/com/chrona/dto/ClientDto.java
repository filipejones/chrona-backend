package com.chrona.dto;

import com.chrona.domain.Client;

public record ClientDto(Long id,
                        String name,
                        String status,
                        String contactName,
                        String contactEmail,
                        String contactPhone,
                        String address,
                        String notes) {

    public static ClientDto from(Client client) {
        return new ClientDto(
                client.getId(),
                client.getName(),
                client.getStatus(),
                client.getContactName(),
                client.getContactEmail(),
                client.getContactPhone(),
                client.getAddress(),
                client.getNotes()
        );
    }
}
