package in.koreatech.batch.domain.dining.model;

import static lombok.AccessLevel.*;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Table(schema = "koin", name = "dining_menus")
@NoArgsConstructor(access = PROTECTED)
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "type")
    private String type;

    @Column(name = "place")
    private String place;

    @Column(name = "price_card")
    private Integer priceCard;

    @Column(name = "price_cash")
    private Integer priceCash;

    @Column(name = "kcal")
    private Integer kcal;

    @Column(name = "menu")
    private String menu;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "sold_out")
    private LocalDateTime soldOut;

    @Column(name = "is_changed")
    private LocalDateTime isChanged;

    @Column(name = "likes")
    private Integer likes;

    @Builder
    private Menu(
        Integer id,
        LocalDate date,
        String type,
        String place,
        Integer priceCard,
        Integer priceCash,
        Integer kcal,
        String menu,
        String imageUrl,
        LocalDateTime soldOut,
        LocalDateTime isChanged,
        Integer likes
    ) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.place = place;
        this.priceCard = priceCard;
        this.priceCash = priceCash;
        this.kcal = kcal;
        this.menu = menu;
        this.imageUrl = imageUrl;
        this.soldOut = soldOut;
        this.isChanged = isChanged;
        this.likes = likes;
    }

    // 복합 유니크 키: date, type, place
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Menu menu)) return false;
        return Objects.equals(date, menu.date)
            && Objects.equals(type, menu.type)
            && Objects.equals(place, menu.place);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, type, place);
    }
}
