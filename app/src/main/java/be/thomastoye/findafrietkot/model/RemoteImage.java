package be.thomastoye.findafrietkot.model;

public class RemoteImage {
    private int id;

    public RemoteImage(int id) {
        this.id = id;
    }

    public String getSmallImageUrl() {
        return String.format("http://www.vindeenfrituur.be/classes/phpThumb/phpThumb.php?src=/storage/frituur/shops/%d.jpg&w=250", id);
    }
}
