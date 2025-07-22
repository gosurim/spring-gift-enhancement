package giftproject.gift.entity;

import giftproject.option.entity.Option;
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
import java.util.NoSuchElementException;

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    protected Product() {
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
            throw new IllegalArgumentException("\"카카오\"가 포함된 문구는 담당 MD와 협의한 경우에만 사용 가능합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long setId(Long id) {
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

    public List<Option> getOptions() {
        return options;
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

    public void addOption(Option option) {
        for (Option existingOption : this.options) {
            if (existingOption.getOptionType().equals(option.getOptionType()) &&
                    existingOption.getOptionValue().equals(option.getOptionValue())) {
                throw new IllegalArgumentException(
                        "동일한 상품 내에 옵션 '" + existingOption.getOptionType() + ": "
                                + existingOption.getOptionValue()
                                + "'이(가) 이미 존재합니다.");
            }
        }
        this.options.add(option);
        option.setProduct(this);
    }

    public void removeOption(Long optionId) {
        if (this.options.size() <= 1) {
            throw new IllegalArgumentException("하나 이상의 옵션이 있어야 하므로 마지막 옵션은 삭제할 수 없습니다.");
        }
        Option optionToRemove = this.options.stream()
                .filter(option -> option.getId().equals(optionId))
                .findFirst()
                .orElseThrow(
                        () -> new NoSuchElementException("ID가 " + optionId + "인 옵션을 찾을 수 없습니다."));
        this.options.remove(optionToRemove);
        optionToRemove.setProduct(null);
    }
}
