import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class TesterDeCasosSinErrores {

    private static final String msgExito = "[SinErrores]";
    private static final String testFilesDirectoryPath = "resources/sinErrores/";

    //TODO: el tipo de esta variable init tiene que ser la clase que tiene el main
    private static final MainKt init = null;
   
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
     
    @Before
    public  void setUpClass() {
        System.setOut(new PrintStream(outContent));
    }
    
    @After
    public  void tearDownClass() {
        System.setOut(originalOut);
    }
    
    @Parameters(name = "{0}")
    public static Iterable<? extends Object> data() {
        File folder = new File(testFilesDirectoryPath);
        ArrayList<String> names = new ArrayList();
        for(File f: folder.listFiles()){
            names.add(f.getName());
        }
        names.sort(String::compareTo);
        return names;
        
    }
    
    private String input;
    
    public TesterDeCasosSinErrores(String input){
        this.input = input;
    }

       
        
    @Test
    public void testIterado() {
        probarExito(input);
    }


    void probarExito(String name){
        String path = testFilesDirectoryPath+name;
        String[] expectedValues = getExpectedValues(path);

        List<Matcher<? super String>> expectedValuesMatchers = new ArrayList();
        for(String s: expectedValues){
            expectedValuesMatchers.add(CoreMatchers.containsString(s));
        }
        String generatedFileName = "["+name+"].out";
        String[] args = {path, generatedFileName};
        init.main(args);
        String result ="";
        try {
            Process proc = Runtime.getRuntime().exec("java -jar CeIVM-cei2011.jar " + generatedFileName + " -v");
            result = new BufferedReader(new InputStreamReader(proc.getInputStream())).lines().collect(Collectors.joining("\n"));
            result += new BufferedReader(new InputStreamReader(proc.getErrorStream())).lines().collect(Collectors.joining("\n"));
            result += "\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n";
            proc.destroy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.setOut(originalOut);

        System.out.println(outContent.toString());
        System.out.println(result);


        assertThat("Mensaje Incorrecto en: " + path,  result.toString(), CoreMatchers.allOf(expectedValuesMatchers));

    }

    public static void showSource(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
            while((line = reader.readLine()) != null){
                System.out.println(line );
            }
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String[] getExpectedValues(String path)  {
        String lineWithTheCode = null;
        try {
            lineWithTheCode = (new BufferedReader(new FileReader(path))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        lineWithTheCode = lineWithTheCode.substring(3);
        String[] codeAlternatives = lineWithTheCode.split("&");
        return codeAlternatives;
    }
    
     
    
    
    
    
}
