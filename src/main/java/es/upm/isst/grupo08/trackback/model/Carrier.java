package es.upm.isst.grupo08.trackback.model;

import javax.persistence.*;

@Entity
@Table(name="CARRIER")
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name= "NAME")
    private String name;

    @Column(name="PASSWORD")
    private String password;

    public Carrier() {
    }

    public Carrier(String name, String password) {
        this.name = name;
        this.password = password;
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
}
