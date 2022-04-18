package amazon.backend.model;

import java.io.Serializable;

public class ProductPK implements Serializable {
    protected long id;
    protected  long packageId;

    public ProductPK() {
    }

    public ProductPK(long id, long packageId) {
        this.id = id;
        this.packageId = packageId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }
}
