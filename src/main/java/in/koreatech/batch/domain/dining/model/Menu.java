package in.koreatech.batch.domain.dining.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dining_menus")
public record Menu(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Integer id,
        @Column(name = "date")
        LocalDate date,
        @Column(name = "type")
        String type,
        @Column(name = "place")
        String place,
        @Column(name = "price_card")
        Integer priceCard,
        @Column(name = "price_cash")
        Integer priceCash,
        @Column(name = "kcal")
        Integer kcal,
        @Column(name = "menu")
        String menu,
        @Column(name = "image_url")
        String imageUrl,
        @Column(name = "sold_out")
        LocalDateTime soldOut,
        @Column(name = "is_changed")
        LocalDateTime isChanged,
        @Column(name = "likes")
        Integer likes
) implements Serializable {

    private static final long serialVersionUID = 1L;

    // 복합 유니크 키: date, type, place
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Menu menu))
            return false;
        return Objects.equals(type, menu.type) && Objects.equals(place, menu.place)
                && Objects.equals(date, menu.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, type, place);
    }
}
