package com.dhruv.assignment2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.source.tree.Tree;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static java.lang.Integer.parseInt;

@RestController
public class MeinController {

    static TreeMap<Integer, WordMapping> wordURLMap; // id and {instance number,word}
    static TreeMap<Integer,String> instances;
    static long totalSize;
    static Integer id;
    static int currentInstanceCounter;
    static ArrayList<Integer> instanceStatus;


/*    URL url = new URL("http://localhost:8080/addword");
    URL url1 = new URL("http://localhost:8080/allwords");
    URL url2 = new URL("http://localhost:8080/deleteall");*/

    static HttpURLConnection conn;


    String sendRequest(String requestType, TreeMap<Integer, String> temp, URL currentURL) throws IOException {

        conn = (HttpURLConnection) currentURL.openConnection();
        StringBuilder responseContent = new StringBuilder();
        if(Objects.equals(requestType, "POST"))
        {
            conn.setDoOutput(true); //triggers post
           // System.out.println("innnnnnnnpo");
            }
        conn.setRequestMethod(requestType);
        conn.setRequestProperty("Content-Type", "application/json");
        // conn.setRequestProperty("Content-Length", Integer.toString(postData.length()));
        ObjectMapper objectMapper = new ObjectMapper();
        String tempres="";
        if(Objects.equals(requestType, "POST")) {
            try {
                String jsonString = objectMapper.writeValueAsString(temp);
                System.out.println(jsonString);

                try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                    dos.writeBytes(jsonString);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }
        try (
                BufferedReader bf = new BufferedReader(new InputStreamReader(
                        conn.getInputStream())))
        {
            String line;

            while ((line = bf.readLine()) != null) {

                System.out.println(line);
                responseContent.append(line);
            }
        }
        //String[] res={"hello","dsfds"};
        /*System.out.println(responseContent.getClass());
        System.out.println(responseContent);
        System.out.println(responseContent.toString());*/
        tempres=responseContent.toString();
        //res=responseContent.toString().split(",");
        conn.disconnect();
        return tempres;
    }

    public void getTotalSize() throws IOException {
        long tot=0;
        TreeMap<Integer,String> temp=new TreeMap<>();
        for(Integer i=0;i<5;i++)
        {
            URL getsize=new URL(instances.get(i)+"/size");
            String rr=sendRequest("GET",temp,getsize);

            Integer tr=parseInt(rr);
            tot+=tr;

        }
        totalSize=tot;
    }

    public MeinController() throws IOException {

        wordURLMap=new TreeMap<>();
        instances= new TreeMap<>();
        instances.put(0,"http://localhost:8080");
        instances.put(1,"http://localhost:8081");
        instances.put(2,"http://localhost:8082");
        instances.put(3,"http://localhost:8083");
        instances.put(4,"http://localhost:8084");
        totalSize=0;
        id=0;
        currentInstanceCounter=0;
        instanceStatus=new ArrayList<Integer>();

        for(int i=0;i<5;i++)
        {
            instanceStatus.add(1);
        }
    }


    @GetMapping("/words")
    public Vector<String> getAllWords() throws IOException {
        Vector<String> result=new Vector<>();
        TreeMap<Integer,String> temp=new TreeMap<>();
        /*String urlString= instances.get(0);
        urlString+="/allwords";
        URL url=new URL(urlString);
        result.add(sendRequest("GET",temp,url));*/
        //for all the instances
        for(int i=0;i<5;i++)
        {
            if(instanceStatus.get(i)==1)
            {
                String urlString= instances.get(i);
                urlString+="/allwords";
                URL url=new URL(urlString);
                result.add(sendRequest("GET",temp,url));
            }
        }
        //System.out.println(result);

        return result;
    }

    @DeleteMapping("/words")
    public void deleteAllWords() throws IOException {

        TreeMap<Integer,String> temp=new TreeMap<>();
        /*String urlString= instances.get(0);
        urlString+="/deleteall";
        URL url=new URL(urlString);
            sendRequest("DELETE",temp,url);*/

        //for all the instances
        for(int i=0;i<5;i++)
        {
            if(instanceStatus.get(i)==1)
            {
                String urlString= instances.get(i);
                urlString+="/deleteall";
                URL url=new URL(urlString);
                sendRequest("DELETE",temp,url);
            }
        }
        wordURLMap.clear();
        id=0;// again starting id from zero so that more values can be used
        totalSize=0;
    }

    @DeleteMapping("/words/{word}")
    public void deleteWord(@PathVariable String word) throws IOException {
        TreeMap<Integer,String> temp=new TreeMap<>();
        /*String urlString= instances.get(0);
        urlString=urlString+"/deleteword/"+word;
        URL url=new URL(urlString);
        sendRequest("DELETE",temp,url);*/

        //for all the instances
        for(int i=0;i<5;i++)
        {
            if(instanceStatus.get(i)==1)
            {
                String urlString= instances.get(i);
                urlString=urlString+"/deleteword/"+word;
                URL url=new URL(urlString);
                sendRequest("DELETE",temp,url);
            }
        }

        Iterator<Map.Entry<Integer, WordMapping>> it = wordURLMap.entrySet().iterator();
        while( it.hasNext())
        {
            Map.Entry<Integer, WordMapping> entry = it.next();
            //System.out.println(entry.getValue());
            if(Objects.equals(entry.getValue().word,word ))
            {
                it.remove();
            }
        }

        getTotalSize();
    }

    @PostMapping("/words")
    public void addWords(@RequestBody String[] words) throws IOException {

       // System.out.println( words);
        TreeMap<Integer,String> temp=new TreeMap<>();
        //String postData = words;
        for(int i=0;i<words.length;i++)
         {
             temp.put(id,words[i]);
             id++;
            //System.out.println(words[i]);
        }
        /*String urlString= instances.get(0);
        urlString+="/addword";
        URL url=new URL(urlString);
        sendRequest("POST",temp,url);*/

        //for all the instances

        Iterator<Map.Entry<Integer, String>> it = temp.entrySet().iterator();
        while( it.hasNext()) {

            Map.Entry<Integer, String> entry = it.next();
            TreeMap<Integer,String> tempo=new TreeMap<>();
            tempo.put(entry.getKey(), entry.getValue());
            for(int x=0;x<5;x++) {

                if ((instanceStatus.get(currentInstanceCounter)==1))
                {
                    if (totalSize == 125) {

                        Integer firstId=wordURLMap.firstKey();
                        Integer instanceId=wordURLMap.get(firstId).instanceId;

                        wordURLMap.put(entry.getKey(),new WordMapping(instanceId,entry.getValue()));
                        wordURLMap.remove(wordURLMap.firstKey());
                        String urlString= instances.get(instanceId);
                        urlString+="/addword";
                        URL url=new URL(urlString);
                        sendRequest("POST",tempo,url);
                        break;

                    } else {
                        URL getsize = new URL(instances.get(currentInstanceCounter) + "/size");
                        String rr = sendRequest("GET", temp, getsize);
                        Integer instanceSize = parseInt(rr);

                        if (instanceSize < 25) {
                            System.out.println(currentInstanceCounter+" size->> "+instanceSize);
                            wordURLMap.put(entry.getKey(),new WordMapping(currentInstanceCounter,entry.getValue()));
                            String urlString= instances.get(currentInstanceCounter);
                            urlString+="/addword";
                            URL url=new URL(urlString);
                            sendRequest("POST",tempo,url);
                            totalSize++;
                            currentInstanceCounter++;
                            currentInstanceCounter%=5;
                            break;
                        }
                        else
                        {
                            currentInstanceCounter++;
                            currentInstanceCounter%=5;
                        }
                    }
                }
                else
                {
                    currentInstanceCounter++;
                    currentInstanceCounter%=5;
                }
            }
        }
        
        /**/
    }

    @GetMapping("/nodes/status")
    public Vector<Integer> activeNodes(@RequestParam String type)
    {
        System.out.println(type);
        String active="active";
        String disabled="disabled";

        Vector<Integer> res=new Vector<>();
            for(Integer i=0;i<5;i++)
            {
                if(instanceStatus.get(i)==1 &&(Objects.equals(type, active)))
                {
                    System.out.println("inn");
                    res.add(i);
                }
                else if(instanceStatus.get(i)==0&&(Objects.equals(type, disabled)))
                {
                    res.add(i);
                }
            }
            return res;
    }

    @PatchMapping("/node/{nodeId}")
    public void instanceStatusUpdate(@PathVariable Integer nodeId, @RequestParam String action)
    {
        System.out.println(nodeId+" "+nodeId.getClass());
        System.out.println(action);
        if(Objects.equals(action, "enable"))
        {
            instanceStatus.set(nodeId,1);
        }
        else if(Objects.equals(action, "disable"))
        {
            instanceStatus.set(nodeId,0);
        }
    }
}
