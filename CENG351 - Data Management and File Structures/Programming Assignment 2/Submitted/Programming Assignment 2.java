package ceng.ceng351.labdb;

import java.util.Enumeration;
import java.util.Vector;

import static java.lang.Math.pow;

class Bucket {
    int bucket_depth, size;
    Vector<String> values;

    int insert(String studentID){
        int count = 0;
        if(values.contains(studentID))  return -1;
        for (String ignored : values)   ++count;
        if(count == size)               return 0;
        values.add(studentID);          return 1;
    }

    public Bucket(int depth, int size) {
        this.bucket_depth = depth;
        this.size = size;
        values = new Vector<>(size);
    }
}

public class LabDB {

    int depth;
    int bucket_size;
    Vector<Bucket> extend;

    int pairIndex(int no, int local_depth) {
        return no ^ (1 << (local_depth-1));
    }

    void split(int no) {
        int localed     = ++extend.get(no).bucket_depth;
        int pair_index  = pairIndex(no, localed);
        int i;

        if(localed > depth){
            for(int p = 0; p < (1 << depth) ; p++) {
                Bucket bucket = new Bucket(extend.get(p).bucket_depth, bucket_size);
                bucket.values = extend.get(p).values;
                extend.add(bucket);
            }
            ++depth;
        }

        for(i = 0 ; i < 1 << depth; ++i)
            if(extend.get(no).values == extend.get(i).values)
                extend.get(i).bucket_depth = extend.get(no).bucket_depth;

        extend.get(pair_index).values = new Bucket(localed, bucket_size).values;
        Vector<String> temp = new Vector<>(extend.get(no).values);
        extend.get(no).values.clear();

        int id  = (int) pow(2, localed);
        int dir_size    = (int) pow(2, depth);

        for(i = pair_index - id; i >= 0; i -= id)
            extend.get(i).values = extend.get(pair_index).values;
        for(i = pair_index + id ; i < dir_size ; i += id)
            extend.get(i).values = extend.get(pair_index).values;
        Enumeration<String> en = temp.elements();
        for(i = 0; en.hasMoreElements(); ++i, en.nextElement())
            enter(temp.get(i));
    }

    public LabDB(int bucketSize) {
        this.depth = 1;
        this.bucket_size = bucketSize;
        extend = new Vector<>(1 << depth);
        for(int i = 0; i < pow(2, depth); ++i){
            Bucket bucket = new Bucket(depth, bucketSize);
            extend.add(bucket);
        }
    }

    public void enter(String studentID) {
        int f = hashing(studentID);
        int status = extend.get(f).insert(studentID);
        if(status == 0){
            split(f);
            enter(studentID);
        }
    }

    public int hashing(String studentID) {
        String studentid_without_e = studentID.replace("e", "");
        int studentidinteger = Integer.parseInt(studentid_without_e);
        String binaryformatofstdinteger  = Integer.toBinaryString(studentidinteger);
        String last1;
        if(depth >= binaryformatofstdinteger.length())
            last1 = binaryformatofstdinteger;
        else
            last1 = binaryformatofstdinteger.substring(binaryformatofstdinteger.length() - depth);
        return Integer.parseInt(last1, 2);
    }

    void merge(int no){
        if(no < 1 << depth) {
            int pair;
            pair = pairIndex(no, extend.get(no).bucket_depth);
            if (extend.get(no).values.isEmpty() && extend.get(no).bucket_depth == extend.get(pair).bucket_depth && no < 1 << depth) {
                extend.get(no).values = extend.get(pair).values;
                --extend.get(pair).bucket_depth;
                --extend.get(no).bucket_depth;
            }
        }
    }

    public void leave(String studentID) {
        int f = hashing(studentID);
        extend.get(f).values.remove(studentID);
        int pair = pairIndex(f, extend.get(f).bucket_depth);
        while(extend.get(f).values.isEmpty() && extend.get(f).bucket_depth == extend.get(pair).bucket_depth && extend.get(f).bucket_depth > 1)
        {
            pair = pairIndex(f, extend.get(f).bucket_depth);
            merge(f);
        }
        shrink();
    }

    public void shrink(){
        int i;
        for(i = 0; i < extend.size(); ++i){
            if(extend.get(i).bucket_depth == depth){
                return;
            }
        }
        --depth;
        for(i = 0; i < 1 << depth; ++i)
            extend.remove(extend.size() - 1);
        shrink();
    }

    public String search(String studentID) {
        for (int i = 0; i < (1 << depth); ++i) {
            if(extend.get(i).values.contains(studentID)) {
                int lenght = Integer.toBinaryString(i).length();
                StringBuilder str = new StringBuilder(Integer.toBinaryString(i));
                for(int j = depth - lenght; j > 0; --j)     str.insert(0, "0");
                return str.toString();
            }
        }
        return "-1";
    }

    public void printLab() {
        int it = (int) pow(2, depth);
        System.out.println("Global depth : " +  depth);
        for(int i = 0; i < it; ++i){
            int length = Integer.toBinaryString(i).length();
            for(int j = depth - length; j > 0; --j )        System.out.print("0");
            System.out.print(Integer.toBinaryString(i) + " : " + "[Local depth:" + extend.get(i).bucket_depth + "]");
            for (String s : extend.get(i).values)
                System.out.print("<" + s + ">");
            System.out.print("\n");
        }
    }
}
