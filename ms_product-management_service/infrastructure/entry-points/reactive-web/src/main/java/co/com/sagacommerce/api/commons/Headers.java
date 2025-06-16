package co.com.sagacommerce.api.commons;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Headers {

    public static final String CONSUMER_ID = "consumer-id";

    public static final String MESSAGE_ID = "message-id";

    public static final  String API_VERSION = "api-versión";

    public static final  String CLIENT_ID = "client-id";

}
