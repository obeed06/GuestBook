package com.tm.interview.guestbook;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Blob;
import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@Table(name = "entry")
public class GuestBookEntry {

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
