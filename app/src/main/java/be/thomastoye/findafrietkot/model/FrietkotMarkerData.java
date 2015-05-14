package be.thomastoye.findafrietkot.model;

public class FrietkotMarkerData {
    private Frietkot frietkot;
    private boolean imageHasBeenRetrievedAlready;

    public FrietkotMarkerData(Frietkot frietkot) {
        this.frietkot = frietkot;
    }

    public Boolean getImageHasBeenRetrievedAlready() {
        return imageHasBeenRetrievedAlready;
    }

    public void setImageHasBeenRetrievedAlready(Boolean imageHasBeenRetrievedAlready) {
        this.imageHasBeenRetrievedAlready = imageHasBeenRetrievedAlready;
    }

    public Frietkot getFrietkot() {
        return frietkot;
    }
}
