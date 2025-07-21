package giftproject.option.entity;

import giftproject.gift.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "product_option",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_id", "option_type", "option_value"})
        })
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "option_type", nullable = false, length = 50)
    private String optionType;

    @Column(name = "option_value", nullable = false)
    private String optionValue;

    @Column(nullable = false)
    private int quantity;

    protected Option() {
    }

    public Option(Product product, String optionType, String optionValue, int quantity) {
        this.product = product;
        this.optionType = optionType;
        this.optionValue = optionValue;
        this.quantity = quantity;
    }

    public Option(String optionType, String optionValue, int quantity) {
        this.optionType = optionType;
        this.optionValue = optionValue;
        this.quantity = quantity;
    }

    public void update(String optionType, String optionValue, int quantity) {
        this.optionType = optionType;
        this.optionValue = optionValue;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Long setId(Long id) {
        return id;
    }

    public String getOptionType() {
        return optionType;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void subtractQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감할 수량은 1 이상이어야 합니다.");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException(
                    "재고가 부족합니다. (요청 수량: " + amount + ", 현재 재고: " + this.quantity + ")");
        }
        this.quantity -= amount;
    }


}
