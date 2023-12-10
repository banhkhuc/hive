package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="image")
@Where(clause = "is_deleted=0")
@AllArgsConstructor
@NoArgsConstructor
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "company_id")
    private long companyId;

    @Column(name = "is_deleted")
    private int isDeleted = 0;

    @Column(name = "is_avatar")
    private boolean isAvatar;

    @Column(name = "content_type")
    private String contentType;

    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "data")
    private byte[] data;

    @Column(name = "is_cover")
    private boolean isCover;
}
