import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MultiAsyncHttpClient implements HttpClient {
    @Override
    public void exec(List<String> urls) throws IOException {
        for (final String url : urls) {
            CompletableFuture<Response>future = httpCall(url);
            future.thenAccept(response -> {
                System.out.println("[" + url + "] OK!");
            });
        }
    }

    private CompletableFuture<Response> httpCall(String url) {
        CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                return client.newCall(request).execute();
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        });
        return future;
    }
}