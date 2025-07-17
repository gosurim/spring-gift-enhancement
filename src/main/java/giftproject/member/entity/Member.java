package giftproject.member.entity;

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

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wish> wishes = new ArrayList<>();

    public Member() {
    }

    public Member(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public Member(String email, String password) {
        this(null, email, password);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<Wish> getWishes() {
        return wishes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void update(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void addWish(Wish wish) {
        this.wishes.add(wish);
        if (wish.getMember() != this) {
            wish.setMember(this);
        }
    }

    public void removeWish(Wish wish) {
        this.wishes.remove(wish);
        if (wish.getMember() == this) {
            wish.setMember(null);
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }
}
