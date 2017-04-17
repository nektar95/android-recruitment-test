package dog.snow.androidrecruittest.fetching;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aleksander on 17.04.2017.
 */

public class ApiError {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ApiError: " +
                "status= " + status +
                ", message= " + message;
    }
}