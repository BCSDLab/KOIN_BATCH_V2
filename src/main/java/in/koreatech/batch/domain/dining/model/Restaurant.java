package in.koreatech.batch.domain.dining.model;

public enum Restaurant {
    // places = {"한식": "A코너", "일품": "B코너", "특식-전골/뚝배기": "C코너", "능수관": "능수관", "코너1": "1코너"}
    A_CORNER("한식", "A코너"),
    B_CORNER("일품", "B코너"),
    C_CORNER("특식-전골/뚝배기", "C코너"),
    NEUNGSUGWAN("능수관", "능수관"),
    ;

    private final String korean;
    private final String dbName;

    Restaurant(String korean, String dbName) {
        this.korean = korean;
        this.dbName = dbName;
    }

    public String getKorean() {
        return korean;
    }

    public String getDbName() {
        return dbName;
    }

    @Override
    public String toString() {
        return korean;
    }

    public static String parseDBName(String restaurant) {
        for (Restaurant r : Restaurant.values()) {
            if (restaurant.contains(r.korean)) {
                return r.dbName;
            }
        }
        return null;
    }
}
