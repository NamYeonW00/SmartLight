package mobile.smartteam.smartlight;

import mobile.smartteam.smartlight.json.HomeResult;
import retrofit2.Call;
import retrofit2.http.GET;

public interface LightAPIService {

    @GET("/server_data.json")
    Call<HomeResult> getSituationResult();

    @GET("/test.php")
    Call<String> test();
}
