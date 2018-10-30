package vo;

public class GPSCoordinate {

    private double latitude;
    private double longtitue;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitue() {
        return longtitue;
    }

    public void setLongtitue(double longtitue) {
        this.longtitue = longtitue;
    }

    public GPSCoordinate(double latitude, double longtitue) {

        this.latitude = latitude;
        this.longtitue = longtitue;
    }
}
