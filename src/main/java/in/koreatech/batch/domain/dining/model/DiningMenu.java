package in.koreatech.batch.domain.dining.model;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDate;

import in.koreatech.batch._common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "dining_menus", schema = "koin", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"date", "type", "place"})
})
@NoArgsConstructor(access = PROTECTED)
public class DiningMenu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "date")
    private LocalDate date;

    @NotNull
    @Size(max = 9)
    @Enumerated(STRING)
    @Column(name = "type", length = 9)
    private Meal type;

    @NotNull
    @Size(max = 9)
    @Column(name = "place", length = 9)
    private String place;

    @Column(name = "price_card")
    private Integer priceCard;

    @Column(name = "price_cash")
    private Integer priceCash;

    @Column(name = "kcal")
    private Integer kcal;

    @NotNull
    @Column(name = "menu", columnDefinition = "TEXT")
    private String menu;

    @Size(max = 255)
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "sold_out")
    private LocalDate soldOut;

    @Column(name = "is_changed")
    private LocalDate isChanged;

    @Column(name = "likes")
    private Integer likes;

    @Column(name = "price")
    private Integer price;

    @Builder
    private DiningMenu(
        Integer id,
        LocalDate date,
        Meal type,
        String place,
        Integer priceCard,
        Integer priceCash,
        Integer kcal,
        String menu,
        String imageUrl,
        LocalDate soldOut,
        LocalDate isChanged,
        Integer likes,
        Integer price
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
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
