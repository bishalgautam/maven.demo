package maven.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.*;

public class Assignment{
	
	public	final OkHttpClient client;
	private final Gson gson;
//	final Gist gist;
	
	public Assignment(){
			 client = new OkHttpClient();
			 gson = new Gson();
//			 gist = new Gist();
		}
		
	
	static class GitUser {
	    String name;
	    String url;
	    int id;
	}
	
	static class Gist {
	    Map<String, GistFile> files;
	  }

	  static class GistFile {
	    String content;
	  }
	  
    public void run() throws Exception {
        
    	
        Request request = new Request.Builder()
    	        .url("https://api.github.com/gists/c2a7c39532239ff261be")
    	        .build();
//    	Request request = new Request.Builder()
//    			  .url("https://100pure-demo.myshopify.com/admin/orders.json?status=any&limit=1")
//    			  .get()
//    			  .addHeader("x-shopify-access-token", "b1ade8379e97603f3b0d92846e238ad8")
//    			  .addHeader("cache-control", "no-cache")
//    			  .addHeader("postman-token", "aa55cf3c-9688-b70d-0569-d69c77873ff2")
//    			  .build();

		 client.newCall(request).enqueue(new Callback() {
		      public void onFailure(Call call, IOException e) {
		        e.printStackTrace();
		        throw new RuntimeException(e);
		      }

		      public void onResponse(Call call, Response response) throws IOException {
		        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

//		        Headers responseHeaders = response.headers();
//		        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
//		          System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//		        }

		       // System.out.println(response.body().string());
		        Gist gist = gson.fromJson(response.body().charStream(), Gist.class);
		        
		        for (Map.Entry<String, GistFile> entry : gist.files.entrySet()){
			          System.out.println("key :"+entry.getKey());
			          System.out.println(entry.getValue().content);
			      }
		        
		      }
		      });
		  
		    
		  }	    

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		 Assignment ass = new Assignment();
		 ass.run();
		 	
	}

 }
