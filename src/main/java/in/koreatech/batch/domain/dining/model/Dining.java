package in.koreatech.batch.domain.dining.model;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDate;
import java.time.LocalDateTime;

import in.koreatech.batch._common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "dining_menus", uniqueConstraints = {
    @UniqueConstraint(
        name = "ux_date_type_place",
        columnNames = {"date", "type", "place"}
    )
})
public class Dining extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "date")
    private LocalDate date;

    @NotNull
    @Enumerated(STRING)
    @Column(name = "type", nullable = false)
    private Meal type;

    @NotNull
    @Column(name = "place")
    private String place;

    @Column(name = "price_card")
    private Integer priceCard;

    @Column(name = "price_cash")
    private Integer priceCash;

    @Column(name = "kcal")
    private Integer kcal;

    @NotNull
    @Column(name = "menu", nullable = false)
    private String menu;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "sold_out", columnDefinition = "DATETIME")
    private LocalDateTime soldOut;

    @Column(name = "is_changed", columnDefinition = "DATETIME")
    private LocalDateTime isChanged;

    @Column(name = "likes")
    private Integer likes = 0;

    @Column(name = "price")
    private Integer price;

    @Builder
    private Dining(
        LocalDate date,
        Meal type,
        String place,
        Integer priceCard,
        Integer priceCash,
        Integer kcal,
        String menu,
        String imageUrl,
        LocalDateTime soldOut,
        LocalDateTime isChanged,
        Integer likes,
        Integer price
    ) {
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
}
