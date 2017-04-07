package maven.demo;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import okhttp3.*;

public class Assignment{
	
	private	final OkHttpClient client;
	private final Gson gson;
	/*HashSet for Number of unique customers*/
	private HashSet<BigDecimal> uniqueCust;
	/*HashMap to calculate the Shortest interval between any two consecutive orders placed by the same customer*/
	private HashMap<BigDecimal,Long> timeMap;
	/*List to calculate Median order value*/
	private List<BigDecimal> totalPrices;
	/*HashMap to calculate Most and least frequently ordered items*/
	private HashMap<BigDecimal,Integer> productMap;
	/*boolean to determine the last page*/
	private boolean loop;
	private int page;
	
	private long totalOrder;
	private long minInterval;
	
	private int maxOrder;
	private List<BigDecimal> mostOrdered;
	private List<BigDecimal> leastOrdered;
	
	public Assignment(){
			 client = new OkHttpClient();
			 gson = new Gson();
			 uniqueCust = new HashSet<BigDecimal>();
			 totalPrices = new ArrayList<BigDecimal>();
			 timeMap = new HashMap<BigDecimal, Long>();
			 productMap = new HashMap<BigDecimal,Integer>();
			 loop = true;
			 page = 1;
			 
			 totalOrder = 0;
			 minInterval = Long.MAX_VALUE;
			 maxOrder = Integer.MIN_VALUE;
			 mostOrdered = new ArrayList<BigDecimal>();
			 leastOrdered = new ArrayList<BigDecimal>();
		}
		
//	static class Pair{
//		 Instant date;
//		 long milliSecTime;
//		 public Pair(Instant date, long milli){
//			 this.date = date;
//			 this.milliSecTime = milli;
//		 }
//	 }
	 static class OrderContainer{
		   private List<Order> orders;;
		}
	 
	 static class Order{
//		private BigDecimal id;
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
	 
	public long getMillisecond(String date){
		
		TemporalAccessor creationAccessor = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(date);
	    Instant instant = Instant.from( creationAccessor );
	    long millisSinceEpoch = instant.toEpochMilli( );
		return millisSinceEpoch;
	}
	public void getTotalOrders() throws Exception{
		
	while(loop){
		Request request = new Request.Builder()
				  .url("https://100pure-demo.myshopify.com/admin/orders.json?status=any&page="+ page +"&fields=id,line_items,customer,total_price,created_at")
				  .get()
				  .addHeader("x-shopify-access-token", "b1ade8379e97603f3b0d92846e238ad8")
				  .addHeader("cache-control", "no-cache")
				  .addHeader("postman-token", "aa55cf3c-9688-b70d-0569-d69c77873ff2")
				  .build();
		 		
		
		 		Response response = client.newCall(request).execute();
		 		
		 		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		 		
		 		OrderContainer gist = gson.fromJson(response.body().charStream(), OrderContainer.class);
		        
		        if( gist.orders == null || gist.orders.size() == 0){
		        	loop = false; 
		        	return;
		        }else{
		        	page++;
		        	for (Order entry : gist.orders){
				       
		        			totalOrder++;
				         
		        			CustomerId custId = entry.customer;
					        BigDecimal id = custId.id ;
					        long millisSinceEpoch = getMillisecond(entry.created_at);
					        if(timeMap.containsKey(id)){
					        	
					        	long timeSinceEpoch = timeMap.get(id);
					        	long interval = Math.abs(millisSinceEpoch - timeSinceEpoch);
					        	minInterval = (interval < minInterval) ? interval : minInterval;
					        	
					        }else{
					        	timeMap.put(id,	millisSinceEpoch);
					        }
					        
		        		 /* Creating a list of values*/
		        		  String totalPrice = entry.total_price.replaceAll(",","");
		        		  BigDecimal bd = new BigDecimal(totalPrice);
		        		  totalPrices.add(bd);
		        		  
		        	     /* */
				          List<LineItem> list = entry.line_items;
				          for(LineItem li : list){
						   
						          if(productMap.containsKey(li.product_id)){
						        	  productMap.put(li.product_id, productMap.get(li.product_id)+1);
						        	  maxOrder = Math.max(maxOrder, productMap.get(li.product_id));
						          }else{
						        	  productMap.put(li.product_id,1);
						          }
				          }
				          
				          for(Map.Entry<BigDecimal, Integer> en : productMap.entrySet()){
					        	  
				        	  if(en.getValue() == maxOrder){
					        		  mostOrdered.add(en.getKey());
					        	  }else if(en.getValue() == 1){
					        		  leastOrdered.add(en.getKey());
					        	  }
				        	  
				          }
				          
				          
				          	  
				      }
		        }
		        response.close();
		}			
	
	}
	 
  
public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		 Assignment ass = new Assignment();
		 ass.getTotalOrders();
		 
		 System.out.println("Total No. of Orders : " + ass.totalOrder);
		 System.out.println("No of Unique Customers: "+ ass.timeMap.size());
		 
		 Collections.sort(ass.totalPrices);
		 int index = (int) Math.ceil((ass.totalPrices.size()+0.00)/2);
		 System.out.println("Median Order Value: " + ass.totalPrices.get(index));
		 
		 System.out.println(  "Least Ordered Items ProductId :"+ Arrays.toString(ass.leastOrdered.toArray()));
		 System.out.println(  "Most Ordered Items ProductId  :"+ Arrays.toString(ass.mostOrdered.toArray()));
	}

 }
