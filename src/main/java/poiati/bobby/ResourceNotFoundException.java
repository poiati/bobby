package poiati.bobby;


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Not found.");
    }

}
