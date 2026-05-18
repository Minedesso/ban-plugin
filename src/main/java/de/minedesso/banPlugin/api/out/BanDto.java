package de.minedesso.banPlugin.api.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanDto {

    private long reasonId;
    private String duration;
    private LocalDateTime bannedAt;

    private String targetName;
    private UUID bannedBy;

}



