package es.upm.isst.grupo08.trackback.model;

import javax.persistence.*;

@Entity
@Table(name="Parcel")
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="Order_Number")
    private long orderNumber;

    @Column(name="Carrier_Id")
    private long carrierId;

    @Column(name="state")
    private String state;

    public Parcel() {
    }

    public Parcel(long orderNumber, long carrierId, String state) {
        this.orderNumber = orderNumber;
        this.carrierId = carrierId;
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
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
