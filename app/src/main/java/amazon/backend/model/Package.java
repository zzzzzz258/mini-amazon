package amazon.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

@Entity
public class Package {
    @Id
    private long id;
    @Column(name = "warehouse_id")
    private int warehouseId;
    @Column(name = "order_id")
    private int orderId;
    @Column(name = "pack_seq")
    private Long packSeq;
    @Column(name="is_packed")
    private boolean isPacked;
    @Column(name = "load_seq")
    private Long loadSeq;
    @Column(name = "is_loaded")
    private boolean idLoaded;

    public Package() {
    }

    public Package(long id, int warehouseId, int orderId) {
        this.id = id;
        this.warehouseId = warehouseId;
        this.orderId = orderId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Long getPackSeq() {
        return packSeq;
    }

    public void setPackSeq(Long packSeq) {
        this.packSeq = packSeq;
    }

    public boolean isPacked() {
        return isPacked;
    }

    public void setPacked(boolean packed) {
        isPacked = packed;
    }

    public Long getLoadSeq() {
        return loadSeq;
    }

    public void setLoadSeq(Long loadSeq) {
        this.loadSeq = loadSeq;
    }

    public boolean isIdLoaded() {
        return idLoaded;
    }

    public void setIdLoaded(boolean idLoaded) {
        this.idLoaded = idLoaded;
    }
}
