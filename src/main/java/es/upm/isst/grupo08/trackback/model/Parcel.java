package es.upm.isst.grupo08.trackback.model;

import javax.persistence.*;

@Entity
@Table(name="PARCEL")
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="TRACKING_NUMBER")
    private String trackingNumber;

    @Column(name="CARRIER_ID")
    private long carrierId;

    @Column(name="STATE")
    private String state;

    public Parcel() {
    }

    public Parcel(String trackingNumber, long carrierId, String state) {
        this.trackingNumber = trackingNumber;
        this.carrierId = carrierId;
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String orderNumber) {
        this.trackingNumber = orderNumber;
    }

    public long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(long carrierId) {
        this.carrierId = carrierId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
