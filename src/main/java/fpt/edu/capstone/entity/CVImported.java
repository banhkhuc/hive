package fpt.edu.capstone.entity;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "cv_imported")
@Getter
@Setter
public class CVImported {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    private String contentType;

    private long size;

    private long candidateId;


    private LocalDateTime createAt;

    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "data")
    private byte[] data;


}