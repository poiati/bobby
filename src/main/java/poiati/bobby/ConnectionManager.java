package poiati.bobby;


public interface ConnectionManager {

    void connectFriend(Integer fromFacebookId, Integer toFacebookId);

    void connectFriendSuggestion(Integer fromFacebookId, Integer toFacebookId);

    void updateSuggestions();
}
