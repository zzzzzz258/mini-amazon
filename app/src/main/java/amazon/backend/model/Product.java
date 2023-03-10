package amazon.backend.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Product {
    @Id
    @Column(name = "package_id")
    private long packageId;
    @Column(name = "product_id")
    private long productId;
    private String description;
    private int count;
    @Column(name = "buy_seq")
    private Long buySeq;
    @Column(name = "is_bought")
    private boolean isBought;

    public Product() {
    }

    public Product(long packageId) {
        this.packageId = packageId;
    }

    public Product(long packageId, long productId, String description, int count) {
        this.packageId = packageId;
        this.productId = productId;
        this.description = description;
        this.count = count;
        this.buySeq = null;
    }

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getBuySeq() {
        return buySeq;
    }

    public void setBuySeq(Long buySeq) {
        this.buySeq = buySeq;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }
}
