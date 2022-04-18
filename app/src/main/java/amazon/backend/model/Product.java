package amazon.backend.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@IdClass(ProductPK.class)
public class Product implements Serializable {
    @Id
    private long id;
    private String description;
    private int count;
    @Id
    @Column(name = "package_id")
    private long packageId;
    @Column(name = "buy_seq")
    private Long buySeq;
    @Column(name = "is_bought")
    private boolean ifBought;

    public Product() {
    }

    public Product(long id, String description, int count) {
        this.id = id;
        this.description = description;
        this.count = count;
    }

    public Product(long id, String description, int count, long packageId) {
        this.id = id;
        this.description = description;
        this.count = count;
        this.packageId = packageId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public Long getBuySeq() {
        return buySeq;
    }

    public void setBuySeq(Long buySeq) {
        this.buySeq = buySeq;
    }

    public boolean isIfBought() {
        return ifBought;
    }

    public void setIfBought(boolean ifBought) {
        this.ifBought = ifBought;
    }
}
