package maven.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.*;

public class Assignment{
	
	private	final OkHttpClient client;
	private final Gson gson;
	private HashSet<BigDecimal> uniqueCust;
	//private HashMap<BigDecimal, >
	private List<BigDecimal> totalPrice;
	private BigDecimal[] countTotal;
	private boolean loop;
	private int page;
	public Assignment(){
			 client = new OkHttpClient();
			 gson = new Gson();
			 uniqueCust = new HashSet<BigDecimal>();
			 totalPrice = new ArrayList<BigDecimal>();
			 countTotal = new BigDecimal[1];
			 loop = true;
			 page = 1;
		}
		
	 
	 static class OrderContainer{
		   private List<Order> orders;;
		}
	 
	 static class Order{
		private BigDecimal id;
		private String created_at;			
		private String total_price;
		private List<LineItem> line_items;
		private CustomerId customer;
		 
	 }
	 static class LineItem{
	    private BigDecimal product_id;
	 }
	  
	 static class CustomerId{
		 private BigDecimal id;
		 
	 }
	 
	 static class TotalOrders{
		 private BigDecimal count;
	 } 
public void getTotalCount() throws Exception{
	
	while(loop){
	Request request = new Request.Builder()
			  .url("https://100pure-demo.myshopify.com/admin/orders.json?status=any&page="+page+"&fields=id,line_items,customer,total_price,created_at")
			  .get()
			  .addHeader("x-shopify-access-token", "b1ade8379e97603f3b0d92846e238ad8")
			  .addHeader("cache-control", "no-cache")
			  .addHeader("postman-token", "aa55cf3c-9688-b70d-0569-d69c77873ff2")
			  .build();
	 		
	// 		private	final OkHttpClient client = new OkHttpClient();		
	 		Response response = client.newCall(request).execute();
	 		
	 		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	 		
	 		OrderContainer gist = gson.fromJson(response.body().charStream(), OrderContainer.class);
	        
	        if( gist.orders == null || gist.orders.size() == 0){
	        	loop = false; 
	        	return;
	        }else{
	        	page++;
	        	System.out.println("else process.");
	        	
	        }
	        
	        response.close();
	}			

}
	 
 public void run() throws Exception {
 
	 while(loop){
    	Request request = new Request.Builder()
    			  .url("https://100pure-demo.myshopify.com/admin/orders.json?status=any&page="+page+"&fields=id,line_items,customer,total_price,created_at")
    			  .get()
    			  .addHeader("x-shopify-access-token", "b1ade8379e97603f3b0d92846e238ad8")
    			  .addHeader("cache-control", "no-cache")
    			  .addHeader("postman-token", "aa55cf3c-9688-b70d-0569-d69c77873ff2")
    			  .build();

		 client.newCall(request).enqueue(new Callback() {
		      
			 public void onFailure(Call call, IOException e) {
		        e.printStackTrace();
		        throw new RuntimeException(e);
		      }

		      public void onResponse(Call call, Response response) throws IOException {
		        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		        
//		        Headers responseHeaders = response.headers();
//		        for (int i = 0; i < responseHeaders.size(); i++) {
//		          System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//		        }

		        OrderContainer gist = gson.fromJson(response.body().charStream(), OrderContainer.class);
		        
		        if( gist.orders.size() == 0){
		        	loop = false; 
		        	return;
		        }else{
		        	page++;
		        	System.out.println("else process.");
		        	
		        }
		        	
		        
//		        for (Order entry : gist.orders){
//			          System.out.println(entry.id);
//			          System.out.println(entry.total_price);
//			          List<LineItem> list = entry.line_items;
//			          for(LineItem li : list)
//			        	  System.out.println(li.product_id);
//			          CustomerId custId = entry.customer;
//			          System.out.println(custId.id);
//			      }
		        response.close(); 
		      }
		      
		      
		      });
	 }
 }		      
public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		 Assignment ass = new Assignment();
		 //ass.getTotalCount();
		// System.out.println(ass.countTotal[0]);
		 
		//while(ass.loop){
			ass.getTotalCount(); 
		//	System.out.println("looping");
		// }
		    //ass.run();
		 	
	}

 }
