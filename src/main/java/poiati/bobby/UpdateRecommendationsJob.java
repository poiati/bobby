package poiati.bobby;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class UpdateRecommendationsJob {

    public static void main(String[] args) {
        final ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        final ConnectionManager connectionManager = context.getBean(ConnectionManager.class);

        connectionManager.updateSuggestions();
    }

}
