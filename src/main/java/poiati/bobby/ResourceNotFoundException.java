package poiati.bobby;


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Not found.");
    }

    public ResourceNotFoundException(final String message) {
        super(message);
    }

}
