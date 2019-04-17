package com.gyb.chat.bean;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Data
@Entity
@Table(name="im_user")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String sign;
    private String status;
    private String avatar;
    private Date created;
    private String password;

}
