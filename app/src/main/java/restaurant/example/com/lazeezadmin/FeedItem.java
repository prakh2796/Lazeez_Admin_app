package restaurant.example.com.lazeezadmin;

/**
 * Created by Pewds on 20-Nov-15.
 */
public class FeedItem {
    private int order_id,bill,type,quantity;
    private Long phone;
    private  String dish;

    public FeedItem() {
    }


    public FeedItem(Integer order_id, Integer bill,Long phone,String dish,Integer quantity) {
        super();
        this.order_id = order_id;
        this.bill = bill;
        this.phone = phone;
        this.dish = dish;
        this.quantity = quantity;
    }

    public Integer getOrder_id() { return order_id; }

    public void setOrder_id(Integer order_id) { this.order_id = order_id; }

    public Integer getBill() { return bill; }

    public void setBill(Integer bill) { this.bill = bill; }

    public Long getPhone() { return phone; }

    public void setPhone(Long phone) { this.phone = phone; }

    public String getDish() { return dish; }

    public void setDish(String dish) { this.dish = dish; }

    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) { this.quantity = quantity;}

    public int getType() {return type;}

    public void setType(int type) {
        this.type = type;
    }

}