package es.upm.isst.grupo08.trackback.model;

import javax.persistence.*;

@Entity
@Table(name="CARRIER")
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name= "CarrierName")
    private String carrierName;

    @Column(name="Password")
    private String password;

    public Carrier() {
    }

    public Carrier(String carrierName, String password) {
        this.carrierName = carrierName;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
