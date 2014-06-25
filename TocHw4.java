/*
 * author : 蔡宗倫
 * student number : F74006187
 * purpose : parse the data from the web and 
 * 			 find the highest & lowest price based on the "大道", "路", "街", "巷"
 * 			 most frequency occur in different month.
 * */

//import java.util.Map;
import java.util.regex.*;
import java.io.*;
import java.net.URL;
import org.json.*;

public class TocHw4 {
	
public static void main(String[] args) throws JSONException, IOException {
	
		//System.out.println("read() running " ); 
		try{    
				URL pageUrl = new URL(args[0]); // parse from web and store in .json file
				BufferedReader br = new BufferedReader(new InputStreamReader(pageUrl.openStream(), "UTF-8"));
	            BufferedWriter bw = new BufferedWriter(new FileWriter("data.json", false));    
	            String oneLine = null ;
            
	            while ((oneLine = br.readLine()) != null){
	                bw.write(oneLine);                
	            }
	            bw.close();
	            br.close();
	            
	            JSONArray parse = new JSONArray(new JSONTokener(new FileReader("data.json")));
	            JSONObject dataprice;  // read .json, store in jsonarray, and new a jsonobject
	            int num = 0, year, fix_yr, price;
				int temp1, temp2, temp3, temp4;
				temp1 = temp2 = temp3 = temp4 = 0;
	            int[][] price_index = new int[50000][2000];
	            String[] road = new String[50000];
				int[] matched_price = new int[100];
	            String name;
	            for(int i = 0, j = 0; i < parse.length(); i++){
	                dataprice = parse.getJSONObject(i);
	                name = dataprice.optString("土地區段位置或建物區門牌");
	                price = dataprice.optInt("總價元");
					temp1 = name.indexOf("大道");  // give the priority of "大道", "路", "街", "巷"
					temp2 = name.indexOf('路');
					temp3 = name.indexOf('街');
					temp4 = name.indexOf('巷');
	                if(temp1 != -1) 
						name = name.substring(0, temp1 + 3); // 大道後面會接著路或段, 納入區分
	                else if(temp2 != -1) 
						name = name.substring(0, temp2 + 1);
	                else if(temp3 != -1) 
						name = name.substring(0, temp3 + 1);
	                else if(temp4 != -1) 
						name = name.substring(0, temp4 + 1);

	                for(j = num-1; j >= -1; j--){   
	                    if(j == -1){
							j = num++; 
							road[j] = name; 
							price_index[j][2] = price; 
							break;
						}
	                    else if(name.equalsIgnoreCase(road[j])) 
							break; // check whether the road name has already existed
	                }
					
	                year = dataprice.optInt("交易年月"); // XXXXX (year-month)
	                fix_yr = ((104 - year/100)*12) + (year%100);	// seperated by year and month			
	                if(price_index[j][fix_yr] == 0){
						price_index[j][fix_yr] = 1; 
						price_index[j][0]++;
					}
					
	                if(price >= price_index[j][1]){ // find max & min price
						price_index[j][1] = price;
					}
	                else if(price < price_index[j][2]){ 
						price_index[j][2] = price;
					}
	            } // end for

	            int bigm = 0, index = 0;
	            for(int i = 0; i < num; i++){ // find the most freq road
	                if(price_index[i][0] > bigm){
						bigm = price_index[i][0]; 
						matched_price[index=0] = i;
					}
	                else if(price_index[i][0] == bigm){ 
						matched_price[++index] = i;
					}
				}
	            for(int i = 0; i <= index; i++){
	                System.out.println(road[matched_price[i]] + ", 最高成交價: " + price_index[matched_price[i]][1] + ", 最低成交價: " + price_index[matched_price[i]][2]);
	            }
		}catch (IOException e){			// exception handler
			System.out.println("invalid form");
			System.out.println("correct form : java –jar TocHw4.jar URL");
			e.printStackTrace();
		}
    } // end of main
}


