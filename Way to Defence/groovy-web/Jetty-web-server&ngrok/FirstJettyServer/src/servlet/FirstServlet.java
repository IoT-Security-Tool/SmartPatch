package servlet; 
 
import java.io.IOException; 
import javax.servlet.ServletException; 
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
import java.util.*;
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.NoSuchAlgorithmException;  
import java.security.Signature; 
import java.nio.charset.StandardCharsets;

public class FirstServlet extends HttpServlet { 
	public static final long serialVersionUID = 1L; 
  
    //record the ID0 and deviceID
    // key  is deiveID, value is a List<Object> which stores the ID0(0), KeyPair(1)
    //deviceID: <ID0,privateKey, publicKey>
    public Map<String,List<Object>> deviceIDKeysMap = new HashMap<String,List<Object>>();
    
    
    
    //record the ID_h and ID_e (generated by web server for each event)
    //key is ID_e, value is ID_hs
    //meaning, an event would only be processed by the same handler once, but could be processed by different handlers
    public Map<String,List<String>> IDeIDhMap = new HashMap<String,List<String>>();
 
    public FirstServlet() { 
    }   
    
    public void showdeviceIDKeysMap() {
    	if(null == deviceIDKeysMap || deviceIDKeysMap.size() == 0)
    		System.out.println("deviceIDKeysMap is empty");
    	Iterator<Map.Entry<String,List<Object>>> entries = deviceIDKeysMap.entrySet().iterator();
    	 
    	while (entries.hasNext()) {
    	    Map.Entry<String,List<Object>> entry = entries.next();
    	    System.out.println("Key = " + entry.getKey() + ", Value (ID0) = " + entry.getValue().get(0));
    	}
    }
    
    //map 
    //Map<String, String> map1 = new HashMap<String, String>();
    //Map<String, String> map2 = new HashMap<String, String>();
    //map1.put(new String(""), "xml");
    
    //list  
    //List<Object> list = new ArrayList<Object>();
    //list.add("abc");
    //list.add(123);
    //list.add(new HashMap<Integer,String>());
  
    public static KeyPair getKeypair(){  
      //产生RSA密钥对(myKeyPair)  
        KeyPairGenerator myKeyGen = null;  
        try {  
            myKeyGen = KeyPairGenerator.getInstance("RSA");  
            myKeyGen.initialize(1024);              
        } catch (NoSuchAlgorithmException e) {             
            e.printStackTrace();  
        }  
        KeyPair myKeyPair = myKeyGen.generateKeyPair();  
        return myKeyPair;  
    }
    
 

    public static String signwithKeypair(Signature mySig, KeyPair myKeyPair, String toBeSinged){  
        byte[] signResult = null;  
        String signString = null;
        try {
        	byte[] byteArrayoftoBeSigned = toBeSinged.getBytes();
            mySig.initSign(myKeyPair.getPrivate());   
            mySig.update(byteArrayoftoBeSigned);         
            signResult = mySig.sign();       
            //signString = new String(signResult, "UTF-8"); 
            //signString = new String(signResult, 0, returnActualLength(signResult), "UTF-8");
            signString = new String(Arrays.toString(signResult));
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return signString;  
    } 
    
    public static boolean verifywithKeypair(Signature mySig,  KeyPair myKeyPair, String orignalInfo, String signedInfo){  
        boolean verify = false;  
        try {  
            mySig.initVerify(myKeyPair.getPublic());   
            mySig.update(orignalInfo.getBytes()); 
            verify= mySig.verify(StringToByteArray(signedInfo)); 
        } catch (Exception e) {  
        	System.out.println("what....");
            e.printStackTrace();  
        }  
        return verify;  
    } 
  
    public static byte[] StringToByteArray(String line)
    {
        String some=line.substring(1, line.length()-1);     
        int element_counter=1;

        for(int i=0; i<some.length(); i++)
        {           
            if (some.substring(i, i+1).equals(","))
            {
                element_counter++;
            }       

        }
        int [] comas =new int[element_counter-1];
        byte [] a=new byte[element_counter];
        if (a.length==1)
        {
            a[0]= Byte.parseByte(some.substring(0));
        }       
        else 
        {
            int j=0;
            for (int i = 0; i < some.length(); i++) 
            {
                if (some.substring(i, i+1).equals(","))
                {
                    comas[j]=i;
                    j++;
                }
            }           
            for (int i=0; i<element_counter; i++)
            {
                if(i==0)
                {
                    a[i]=Byte.parseByte(some.substring(0, comas[i]));
                }
                else if (i==element_counter-1)
                {
                    a[i]=Byte.parseByte(some.substring(comas[comas.length-1]+2));
                }
                else
                {
                    a[i]=Byte.parseByte(some.substring(comas[i-1]+2, comas[i]));
                }

            }
        }
        return a;
    }
  
  
    public void verifyEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 		System.out.println("before verifyEvent");
 		showdeviceIDKeysMap();
 		
     	response.setCharacterEncoding("UTF-8"); 
         response.setContentType("text/html"); 
         response.setStatus(HttpServletResponse.SC_OK); 
         
     	Map<String,String[]> parameters = request.getParameterMap();
     	
     	if(!parameters.containsKey("name")) {
     		System.out.println("Error: parameter eventName not exists in the HTTP request");
     		response.getWriter().println("false");
     		return;
     	}
     	
     	if(!parameters.containsKey("value")) {
     		System.out.println("Error: parameter eventValue not exists in the HTTP request");
     		response.getWriter().println("false");
     		return;
     	}
     	
     	if(!parameters.containsKey("deviceID")) {
     		System.out.println("Error: parameter deviceID not exists in the HTTP request");
     		response.getWriter().println("false");
     		return;
     	}
     	
     	if(!parameters.containsKey("ID_e")) {
     		System.out.println("Error: parameter ID_e not exists in the HTTP request");
     		response.getWriter().println("false");
     		return;
     	}
     	
     	if(!parameters.containsKey("ID_h")) {
     		System.out.println("Error: parameter ID_h not exists in the HTTP request");
     		response.getWriter().println("false");
     		return;
     	}
     	
     	if(!parameters.containsKey("sign")) {
     		System.out.println("Error: parameter passedSignature not exists in the HTTP request");
     		response.getWriter().println("false");
     		return;
     	}
     	
     	
     	String eventName = parameters.get("name")[0];
     	String eventValue = parameters.get("value")[0];
     	String deviceID = parameters.get("deviceID")[0];
     	String ID_e = parameters.get("ID_e")[0];
     	String ID_h = parameters.get("ID_h")[0];
     	String passedSignature = parameters.get("sign")[0];
     	System.out.println("eventName is: " + eventName + "\n" + "eventValue is: " + eventValue + "\n" + "deviceID is: " + deviceID + "\n" + "ID_e is: " + ID_e + "\n" + "ID_h is: " + ID_h + "\n" + "passedSignature is: " + passedSignature);

     	//check whether the sign is "error: ..."
     	if(passedSignature.length() >= 5 && passedSignature.substring(0,5).contentEquals("Error")) {
     		System.out.println("Error: passedSignature contains Error");
     		response.getWriter().println("false");
     		return;
     	}

     	//check whether the event has already been processed by the handler (replay attack)
     	//IDe（event ID，generated and recorded by web server
     	if(!IDeIDhMap.containsKey(ID_e)){
     		System.out.println("Error: ID_e was not recorded or removed due to timeout \n ID_e is: " + ID_e);
     		response.getWriter().println("false");
     		return;
     	}
     	//whether the list(value) of event ID (key) contains handler ID
     	//if is
     	//    This event has been processed by the current handler, it is a replay attack, and returns false
     	if(IDeIDhMap.get(ID_e) != null && IDeIDhMap.get(ID_e).size() != 0 && IDeIDhMap.get(ID_e).contains(ID_h)) { 
     		System.out.println("Error: event " + ID_e + " has already been processed by handler " + ID_h + " (replay attack)");
         	response.getWriter().println("false");
         	return;
     	}
     
     	if(!deviceIDKeysMap.containsKey(deviceID)) {//deviceID不存在
     		System.out.println("Error: deviceID not recored: " + deviceID);
         	response.getWriter().println("false");
         	return;
     	}
     	
     	
     	//verify the correctness of the signature
     	//                          name            value           ID0                                     deviceID  ID_e
     	String orignalDataSigned = eventName + eventValue + (String) deviceIDKeysMap.get(deviceID).get(0) + deviceID + ID_e;
     	
 		try {
	     
	 		KeyPair keyPair = (KeyPair) deviceIDKeysMap.get(deviceID).get(1);
 			Signature verifySig = Signature.getInstance("SHA1withRSA");
 			if(verifywithKeypair(verifySig, keyPair, orignalDataSigned, passedSignature)) {
 				response.getWriter().println("true");
 	    		System.out.println("verify succeeded");
 	    		//record ID_h to the list of ID_e
 	    		IDeIDhMap.get(ID_e).add(ID_h);
 	    		return;
 			}
 			else { 
 				response.getWriter().println("false");
 	    		System.out.println("verify failed");
 	    		return;
 			}
 		} catch (NoSuchAlgorithmException e) {
 			e.printStackTrace();
 		}
     }
  
    
    public void signEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("before signEvent");
		showdeviceIDKeysMap();
		
    	response.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html"); 
        response.setStatus(HttpServletResponse.SC_OK); 
        
    	Map<String,String[]> parameters = request.getParameterMap();
    	
    	String deviceID = "";
    	if(!parameters.containsKey("deviceID")) {
    		System.out.println("Error: parameter deviceID  not exists in the HTTP request");
    		response.getWriter().println("Error: parameter deviceID  not exists in the HTTP request");
    		return;
    	}
    	deviceID = parameters.get("deviceID")[0];
    	System.out.println("deviceID is: " + deviceID);
    	    	
    	String ID0 = ""; 
    	if(!parameters.containsKey("ID0")) {
    		System.out.println("Error: parameter ID0 not exists in the HTTP request");
    		response.getWriter().println("Error: parameter ID0 not exists in the HTTP request");
    		return;
    	}
    	ID0 = parameters.get("ID0")[0];
    	System.out.println("ID0 is: " + ID0);

    	String evtName = ""; 
    	if(!parameters.containsKey("name")) {
    		System.out.println("Error: parameter evtName not exists in the HTTP request");
    		response.getWriter().println("Error: parameter evtName not exists in the HTTP request");
    		return;
    	}
    	evtName = parameters.get("name")[0];
    	System.out.println("evtName is: " + evtName);

    	String evtValue = ""; 
    	if(!parameters.containsKey("value")) {
    		System.out.println("Error: parameter evtValue not exists in the HTTP request");
    		response.getWriter().println("Error: parameter evtValue not exists in the HTTP request");
    		return;
    	}
    	evtValue = parameters.get("value")[0];
    	System.out.println("evtValue is: " + evtValue);
    	
    
    	String toBeSinged = evtName + evtValue + ID0 + deviceID;
    	
    	//check if the deviceID exists in the deviceIDKeysMap
    	//if it exists, check if ID0 is the same，
    	//if it does not exist, create a new key, save ID0 and keys
    	if(deviceIDKeysMap.containsKey(deviceID)) { 
        	//System.out.println(deviceIDKeysMap.get(deviceID).get(0));
        	//System.out.println(ID0);
    		if(!((String) deviceIDKeysMap.get(deviceID).get(0)).contentEquals(ID0)) { /
        		System.out.println("Error: wrong ID0 for deviceID");
        		response.getWriter().println("Error: wrong ID0 for deviceID");
        		
        		//////////////////////////////////////////
        		System.out.println("after signEvent");
        		showdeviceIDKeysMap();
        		//////////////////////////////////////////
        		
        		return;
    		}
    		else {
				try {
	    			KeyPair myKeyPair = (KeyPair) deviceIDKeysMap.get(deviceID).get(1);
	    			String IDe = UUID.randomUUID().toString();
	    			IDeIDhMap.put(IDe, new ArrayList<String>());
	    			toBeSinged = toBeSinged + IDe;
	    	    	System.out.println("toBeSinged: " + toBeSinged);
					Signature signSig = Signature.getInstance("SHA1withRSA");
					String signature = signwithKeypair(signSig,myKeyPair,toBeSinged); 
		    		response.getWriter().println("-IDe-" + IDe + "+IDe+");
		    		response.getWriter().println("-sign-" + signature + "+sign+");
		    		System.out.println("signature: " + signature);
		    		
		    		
					//////////////////////////////////////////
					System.out.println("after signEvent");
					showdeviceIDKeysMap();
					//////////////////////////////////////////
		    		
		    		return;
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	else { 
			try {
	    		KeyPair newKeyPair = getKeypair();	    		
	    		List<Object> newList = new ArrayList<Object>();
	    		newList.add(ID0);
	    		newList.add(newKeyPair);
	    	    deviceIDKeysMap.put(deviceID, newList);
				String IDe = UUID.randomUUID().toString();
    			IDeIDhMap.put(IDe, new ArrayList<String>());
				toBeSinged = toBeSinged + IDe;
		    	System.out.println("toBeSinged: " + toBeSinged);	    	   
				Signature signSig = Signature.getInstance("SHA1withRSA");
				String signature = signwithKeypair(signSig,newKeyPair,toBeSinged);  
	    		response.getWriter().println("-IDe-" + IDe + "+IDe+");
	    		response.getWriter().println("-sign-" + signature + "+sign+");
	    		System.out.println("signature: " + signature);
	    		
	    		
	    		
				//////////////////////////////////////////
				System.out.println("after signEvent");
				showdeviceIDKeysMap();
				//////////////////////////////////////////
	    		
	    		return;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    	    
    	}
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
        //String name = request.getParameter("name"); 
        //String password = request.getParameter("password");  
    	
    	Map<String,String[]> parameters = request.getParameterMap();
    	if(!parameters.containsKey("func")) {
    		System.out.println("Error: parameter func not exists in the HTTP request");
    		response.getWriter().println("Error: parameter func not exists in the HTTP request");
    		return;
    	}
        
    	String currentFunc = request.getParameter("func");
    	//System.out.println("path is " + currentFunc);
    	
    	switch(currentFunc){
	    	case "verify":
	    		verifyEvent(request, response);
	    		return;
	    	case "sign":
	    		signEvent(request, response);
	    		return;
	    	default:
	    		System.out.println("Error: only support verify and sign function, currentFunc is " + currentFunc);
	    		response.getWriter().println("Error: only support verify and sign function, currentFunc is " + currentFunc);
	    		return;
    	}
        /**
		Iterator<Map.Entry<String,String[]>> entries = parameters.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String,String[]> entry = entries.next();
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()[0]);
		    response.getWriter().println("Key = " + entry.getKey() + ", Value = " + entry.getValue()[0]);
		}

		
        if(name!=null) { 
        	
            response.getWriter().println("name is (first parameter from the request):</br>"+name+"</br></br>");
			System.out.println(name);
        }
 
        if(password!=null) { 
            response.getWriter().println("password is (second parameter from the request):</br>"+password+"</br>");  
			System.out.println(password); 
        }
		*/
    } 
} 