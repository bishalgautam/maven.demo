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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import okhttp3.*;

public class Assignment{
	
	private	final OkHttpClient client;
	private final Gson gson;
	
	/*HashMap to calculate the Shortest interval between any two consecutive orders placed by the same customer*/
	private HashMap<BigDecimal,List<Long>> timeMap;
	/*List to calculate Median order value*/
	private List<BigDecimal> totalPrices;
	/*HashMap to calculate Most and least frequently ordered items*/
	private HashMap<BigDecimal,Integer> productMap;
	/*boolean to determine the last page*/
	private boolean loop;
	private int page;
	
	private long totalOrder;
//	private int maxOrder;
//	private LineItem minOrder;
//	
	public Assignment(){
			 client = new OkHttpClient();
			 gson = new Gson();
			// uniqueCust = new HashMap<BigDecimal, Long>();
			 totalPrices = new ArrayList<BigDecimal>();
			 timeMap = new HashMap<BigDecimal,List<Long>>();
			 productMap = new HashMap<BigDecimal,Integer>();
			 loop = true;
			 page = 1;
			 
			 totalOrder = 0;
//			 maxOrder = Integer.MIN_VALUE;
//			 minOrder = null;
		}
		
	 static class OrderContainer{
		   private List<Order> orders;;
		}
	 
	 static class Order{
		private String created_at;			
		private String total_price;
		private List<LineItem> line_items;
		private CustomerId customer;
		 
	 }
	 static class LineItem implements Comparable<LineItem>{
	    private BigDecimal product_id;

		public int compareTo(LineItem o) {
			// TODO Auto-generated method stub
			BigDecimal result = this.product_id.subtract(o.product_id);
			return result.signum();	
		}	
	 }
	  
	 static class CustomerId{
		 private BigDecimal id;
		 
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
		        	System.out.println("End of the Pages to Process");
		        	return;
		        }else{
		        	System.out.println("Processing  Page "+ page);
		        	page++;
		        	for (Order entry : gist.orders){
		        			/* count of orders*/
		        			totalOrder++;
				         
		        			/* creating a hashmap with customerId as key and list of ordered_time as values*/
		        			CustomerId custId = entry.customer;
					        BigDecimal id = custId.id ;
					        long millisSinceEpoch = getMillisecond(entry.created_at);
					        if(timeMap.containsKey(id)){
					        	List<Long> temp = timeMap.get(id);
					        	temp.add(millisSinceEpoch);
					        	timeMap.put(id, temp);
					        }else{
					        	List<Long> list = new ArrayList<Long>();
					        	list.add(millisSinceEpoch);
					        	timeMap.put(id,	list);
					        }
					        
					      
		        		 /* Creating a list of values*/
		        		  String totalPrice = entry.total_price.replaceAll(",","");
		        		  BigDecimal bd = new BigDecimal(totalPrice);
		        		  totalPrices.add(bd);
		        		  
		        	     /*creating a hashmap and tracking the max and min values in the hashmap */
				          List<LineItem> list = entry.line_items;
				          for(LineItem li : list){				        	
						          if(productMap.containsKey(li.product_id)){
//							          if(minOrder == null || productMap.get(minOrder.product_id).compareTo(productMap.get(li.product_id)) > 0){
//						        		  minOrder = li;
//						        	  }      
						        	  productMap.put(li.product_id, productMap.get(li.product_id)+1);
						        	 // maxOrder = Math.max(maxOrder, productMap.get(li.product_id));				
						        	  
						          }else{
						        	  productMap.put(li.product_id,1);
						          }
				          }
				             
				      }
		        }
		        response.close();
		}			
	}
	 
  
public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	 final List<BigDecimal> mostOrdered = new ArrayList<BigDecimal>();
	 final List<BigDecimal> leastOrdered = new ArrayList<BigDecimal>();
	 
		 Assignment ass = new Assignment();
		 ass.getTotalOrders();
		 
		 System.out.println("Total No. of Orders : " + ass.totalOrder);
		 System.out.println("No of Unique Customers: "+ ass.timeMap.size());
		 
		  Collections.sort(ass.totalPrices);
		  int index = ass.totalPrices.size()/2 ;
		  System.out.println("Median Order Value: " + ass.totalPrices.get(index));
		 
//		 int max = (ass.maxOrder == Integer.MIN_VALUE ) ? 1 : ass.maxOrder;
//       int min = (ass.minOrder == null) ? 1 : ass.productMap.get(ass.minOrder.product_id);
		 
		 int max = Collections.max(ass.productMap.values());
		 int min = Collections.min(ass.productMap.values());
		 
       	 for(Map.Entry<BigDecimal, Integer> en : ass.productMap.entrySet()){
       	 
       	  		  if(en.getValue() == max){
	        		  mostOrdered.add(en.getKey());
	        	  }else if(en.getValue() == min){
	        		  leastOrdered.add(en.getKey());
	        	  }
       	  
         }   
         	  
		 System.out.println(  "Least Ordered Items ProductId :"+ Arrays.toString(leastOrdered.toArray()));
		 System.out.println(  "Most Ordered Items ProductId  :"+ Arrays.toString(mostOrdered.toArray()));
		 
		 System.out.println("Customers and the shortest interval between their consecutive orders :");
		 
		 for(Entry<BigDecimal, List<Long>> en : ass.timeMap.entrySet()){
		       	  List<Long> list = en.getValue();
		       	  Collections.sort(list);
		       	  long interval = 0;
		       	  long minInterval = Long.MAX_VALUE;
		       	  
		       	  /*Finding the shortest interval among the times in the sorted list*/
		       	  for(long inter : list){
		       		  if ((inter - interval) < minInterval){
		       			  minInterval = inter - interval;
		       		  } 
		       		  interval = inter;
		       	  }
		       	  if(list.size() < 1){
		       		 System.out.println("[Customer Id :"+ en.getKey()+ ", Shortest Interval : NUll ]"); 
		       	  }else{
		       		 System.out.println("[Customer Id :"+ en.getKey()+ ", Shortest Interval :"+ minInterval+"]");  
		       	  }	 
         }    
	}

 }
