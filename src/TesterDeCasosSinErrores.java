import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class TesterDeCasosSinErrores {

    private static final String msgExito = "[SinErrores]";
    private static final String testFilesDirectoryPath = "resources/sinErrores/";

    //TODO: el tipo de esta variable init tiene que ser la clase que tiene el main
    private static final LexerMainKt init = null;
   
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private boolean fullCompilerOuputPrintingInEachTest = true;


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
            String[] args = {path};
            init.main(args);

            if(fullCompilerOuputPrintingInEachTest){
                System.setOut(originalOut);
                System.out.println(outContent.toString());
            }

            assertThat("Mensaje Incorrecto en: " + path,  outContent.toString(), CoreMatchers.containsString(msgExito));
           
    }
    
     
    
    
    
    
}
