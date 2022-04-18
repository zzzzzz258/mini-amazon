package amazon.backend.model;

public class Product {
    private int id;
    private String description;
    private int count;

    public Product(int id, String description, int count) {
        this.id = id;
        this.description = description;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
