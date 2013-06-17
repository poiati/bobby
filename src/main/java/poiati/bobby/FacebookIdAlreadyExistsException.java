package poiati.bobby;


public class FacebookIdAlreadyExistsException extends RuntimeException {

    public FacebookIdAlreadyExistsException(final Integer facebookId) {
        super(facebookId.toString());
    }

}
