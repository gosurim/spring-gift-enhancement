package giftproject.gift.entity;

import giftproject.wishlist.entity.Wish;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wish> wishes = new ArrayList<>();

    public Product() {
    }

    public Product(Long id, String name, Integer price, String imageUrl) {
        validateName(name);
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Product(String name, Integer price, String imageUrl) {
        this(null, name, price, imageUrl);
    }

    private void validateName(String name) {
        if (name.contains("카카오")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "\"카카오\"가 포함된 문구는 담당 MD와 협의한 경우에만 사용 가능합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Wish> getWishes() {
        return wishes;
    }

    public void update(String name, Integer price, String url) {
        validateName(name);
        this.name = name;
        this.price = price;
        this.imageUrl = url;
    }

    public void addWish(Wish wish) {
        this.wishes.add(wish);
        if (wish.getProduct() != this) {
            wish.setProduct(this);
        }
    }

    public void removeWish(Wish wish) {
        this.wishes.remove(wish);
        if (wish.getProduct() == this) {
            wish.setProduct(null);
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
