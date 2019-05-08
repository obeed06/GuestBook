package com.tm.interview.guestbook;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "entry")
class GuestBookEntry {

    @Id
    private long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String imageUrl;

    @NotBlank(message = "Message is mandatory")
    private String message;

    @CreatedDate
    @CreationTimestamp
    @Column(name = "created_date")
    private Date createdDate;

    @Transient
    private MultipartFile image;
}
