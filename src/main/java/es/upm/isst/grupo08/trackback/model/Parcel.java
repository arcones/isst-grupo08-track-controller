package es.upm.isst.grupo08.trackback.model;

import javax.persistence.*;

@Entity
@Table(name = "PARCEL")
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "ID")
    private long id;

    @Column(name = "TRACKING_NUMBER", unique = true, nullable = false)
    private String trackingNumber;

    @Column(name = "CARRIER_ID", nullable = false)
    private long carrierId;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "RECIPIENT", nullable = false)
    private String recipient;

    public Parcel() {
    }

    public Parcel(String trackingNumber, long carrierId, String status, String recipient) {
        this.trackingNumber = trackingNumber;
        this.carrierId = carrierId;
        this.status = status;
        this.recipient = recipient;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecipient(){ return recipient; }

    public void setRecipient(String recipient){ this.recipient = recipient; }
}
