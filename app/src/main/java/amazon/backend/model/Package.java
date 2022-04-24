package amazon.backend.model;

import jakarta.persistence.*;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

@Entity
@Table(name = "package")
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long packageId;
    @Column(name = "warehouse_id")
    private int warehouseId;
    @Column(name = "order_id")
    private int orderId;
    private int x;
    private int y;
    @Column(name = "ups_account_name")
    private String upsAccountName;
    @Column(name = "pack_seq")
    private Long packSeq;
    @Column(name="is_packed")
    private boolean isPacked;
    @Column(name = "truck_id")
    private Integer truckId;
    @Column(name = "load_seq")
    private Long loadSeq;
    @Column(name = "is_loaded")
    private boolean idLoaded;

    public Package() {
    }

    public Package(int warehouseId, int orderId, int x, int y) {
        this.warehouseId = warehouseId;
        this.orderId = orderId;
        this.x = x;
        this.y = y;
    }

    public Package(int warehouseId, int orderId, int x, int y, String upsAccountName) {
        this.warehouseId = warehouseId;
        this.orderId = orderId;
        this.x = x;
        this.y = y;
        this.upsAccountName = upsAccountName;
    }

    public Package(Long packageId) {
        this.packageId = packageId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getUpsAccountName() {
        return upsAccountName;
    }

    public void setUpsAccountName(String upsAccountName) {
        this.upsAccountName = upsAccountName;
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

    public Integer getTruckId() {
        return truckId;
    }

    public void setTruckId(Integer truckId) {
        this.truckId = truckId;
    }

    public boolean isIdLoaded() {
        return idLoaded;
    }

    public void setIdLoaded(boolean idLoaded) {
        this.idLoaded = idLoaded;
    }
}
