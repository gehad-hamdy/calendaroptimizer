package com.calendaroptimizer.dto;

import com.calendaroptimizer.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopUserDTO {
    private Long userId;
    private String userName;
    private Long distinctPropertiesBooked;

    public TopUserDTO(User user, Long count) {
        this.userId = user.getId();
        this.userName = user.getName();
        this.distinctPropertiesBooked = count;
    }
}
