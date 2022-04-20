package amazon.backend.model;

import jakarta.persistence.*;

@Entity
public class Warehouse {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private int x;
  private int y;

  public Warehouse(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Warehouse(int id, int x, int y) {
    this.id = id;
    this.x = x;
    this.y = y;
  }

  public Warehouse() {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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
}
