package de.minedesso.banPlugin.api.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Reason {

    private long reasonId;
    private String reason;

}
