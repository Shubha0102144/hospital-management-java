package com.hospital.management.user.service;

import com.hospital.management.user.dto.UserResponse;
import com.hospital.management.user.model.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(UserDocument document) {
        System.out.println("registered");
        return new UserResponse(
                document.getId(),
                document.getEmail(),
                document.getFullName(),
                document.getRoles(),
                document.isEnabled(),
                document.getCreatedAt()

        );

    }
}
