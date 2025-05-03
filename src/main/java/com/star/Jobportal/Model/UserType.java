package com.star.Jobportal.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.grammars.hql.HqlParser;

import java.util.List;

@Data
@Entity
@Table(name="users_type")
public class UserType {

    @Id
    private int userTypeId;

    private String UserTypeName;

    @OneToMany(targetEntity =Users.class,mappedBy="userTypeId", cascade=CascadeType.ALL)
    private List<Users> users;

    public UserType() {
    }

    public UserType(int userTypeId, String userTypeName, List<Users> users) {
        this.userTypeId = userTypeId;
        this.UserTypeName = userTypeName;
        this.users = users;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getUserTypeName() {
        return UserTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        UserTypeName = userTypeName;
    }

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "UserType{" +
                "userTypeId=" + userTypeId +
                ", UserTypeName='" + UserTypeName +
                '}';
    }
}
