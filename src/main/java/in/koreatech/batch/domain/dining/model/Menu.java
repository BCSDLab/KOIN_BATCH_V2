package in.koreatech.batch.domain.dining.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public record Menu(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
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
