package com.ayman.distributed.simuclothing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "fashion_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FashionProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String userId; // Linked to Auth Service 'sub'

    private Double heightCm;
    private Double weightKg;
    
    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @Embedded
    private AvatarConfig avatarConfig;
}

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AvatarConfig {
    private String skinTone;
    private String hairStyle;
    private String hairColor;
    private String eyeColor;
    private String faceShape;
}

enum BodyType {
    ECTOMORPH, MESOMORPH, ENDOMORPH, HOURGLASS, PEAR, APPLE, RECTANGLE, INVERTED_TRIANGLE
}
