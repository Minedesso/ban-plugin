package de.minedesso.banPlugin.api.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanDetailsDto {

    private LocalDateTime bannedAt;
    private LocalDateTime expiresAt;

    private String reason;
    private String bannedBy;

}
