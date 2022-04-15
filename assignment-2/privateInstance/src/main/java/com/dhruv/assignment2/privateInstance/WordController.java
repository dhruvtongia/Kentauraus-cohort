package com.dhruv.assignment2.privateInstance;

import com.sun.source.tree.Tree;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class WordController {

    static TreeMap<Integer,String>m;
    static Integer count;

    public WordController() {
        m=new TreeMap<>();
        count=0;
    }

    @GetMapping("/allwords")
    public Vector<String> getAllWords()
    {
        Vector<String> result=new Vector<>();

        for(Map.Entry ele:m.entrySet())
        {
            //System.out.println(ele.getKey()+" "+ele.getValue());
            result.add((String)ele.getValue());
        }
       // System.out.println(result);
        return result;
    }

    @GetMapping("/size")
    public int getSize()
    {
        return m.size();
    }

    @PostMapping("/addword")
    public void addWord(@RequestBody TreeMap<Integer,String> word)
    {
        //System.out.println(word);
        for (Map.Entry ele:word.entrySet()
             ) {
           // System.out.println((Integer) ele.getKey()+" "+(String) ele.getValue());
            m.put((Integer) ele.getKey(),(String) ele.getValue());
        }
        //System.out.println(word.getClass());
        //System.out.println(word);
       /* m.put(count,word);
        count++;*/
        while(m.size()>25)
        {
            m.remove(m.firstKey());
        }

    }

    @DeleteMapping("/deleteall")
    public void deleteAll()
    {
       // System.out.println("innnnnn");
        m.clear();
        /*for(Map.Entry ele:m.entrySet())
        {
            System.out.println(ele.getKey()+" "+ele.getValue());
        }*/
    }

    @DeleteMapping("/deleteword/{word}")
    public void deleteWord(@PathVariable String word)
    {
       // System.out.println(word);
        String res=word;

        Iterator<Map.Entry<Integer, String>> it = m.entrySet().iterator();
        while( it.hasNext())
        {
            Map.Entry<Integer, String> entry = it.next();
            //System.out.println(entry.getValue());
            if(Objects.equals(entry.getValue(), res))
            {
                it.remove();
            }
        }
    }
}
