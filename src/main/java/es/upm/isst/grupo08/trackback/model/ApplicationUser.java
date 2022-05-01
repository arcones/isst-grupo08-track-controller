package es.upm.isst.grupo08.trackback.model;

import javax.persistence.*;

@Entity
@Table(name="APPLICATION_USER")
public class ApplicationUser {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name= "NAME", nullable = false, unique = true)
    private String name;

    @Column(name="PASSWORD", nullable = false)
    private String password;

    @Column(name = "ROLE")
    private Role role;

    public ApplicationUser() {
    }

    public ApplicationUser(String name, String password, Role role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String carrierName) {
        this.name = carrierName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
