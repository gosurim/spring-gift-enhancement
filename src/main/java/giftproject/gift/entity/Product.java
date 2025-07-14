package giftproject.gift.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class Product {

    private Long id;
    private String name;
    private Integer price;
    private String imageUrl;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void update(String name, Integer price, String url) {
        validateName(name);
        this.name = name;
        this.price = price;
        this.imageUrl = url;
    }
}
