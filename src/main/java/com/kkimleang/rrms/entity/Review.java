package com.kkimleang.rrms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.time.LocalDateTime;

@RedisHash("Reviews")
@Getter
@Setter
@ToString
@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"property_id", "user_id"}),
})
public class Review extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    @NotNull
    @Column(name = "comment")
    private String comment;

    @NotNull
    @Column(name = "rating", nullable = false)
    private String rating;

    @Column(name = "visible")
    private Boolean visible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
