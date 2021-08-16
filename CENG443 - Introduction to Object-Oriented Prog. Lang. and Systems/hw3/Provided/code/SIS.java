import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SIS {
    private static String fileSep = File.separator;
    private static String lineSep = System.lineSeparator();
    private static String space   = " ";

    private List<Student> studentList = new ArrayList<>();

    public SIS(){ processOptics(); }

    private void processOptics(){
        // TODO
    }

    public double getGrade(int studentID, int courseCode, int year){
        // TODO
        return 0;
    }

    public void updateExam(int studentID, int courseCode, String examType, double newGrade){
        // TODO
    }

    public void createTranscript(int studentID){
        // TODO
    }

    public void findCourse(int courseCode){
        // TODO
    }

    public void createHistogram(int courseCode, int year){
        // TODO
    }
}