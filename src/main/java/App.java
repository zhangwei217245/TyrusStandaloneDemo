
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glassfish.tyrus.server.Server;
import x.spirit.tyrusdemo.config.AppConfig;
import x.spirit.tyrusdemo.websocket.StringEndPoint;



/**
 * Hello world!
 *
 */
public class App 
{
    static Server server = null;
    static Options options = new Options();
    
    private static void buildOptions() {
        // build option tables
        options.addOption(new Option("help", "print this message"));
        options.addOption(Option.builder("port").hasArg()
                .desc("port number")
                .build());
        options.addOption(Option.builder("ip").hasArg()
                .desc("external ip address")
                .build());
    }
    public static String[] parseArgs(String[] args) {
        String[] rst = new String[2];
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("indexing server", options);
                System.exit(0);
            }
            rst[0] = line.getOptionValue("ip", "127.0.0.1");
            rst[1] = line.getOptionValue("port", "8080");
        } catch (ParseException exp) {
            System.out.println("Arguments Error:" + exp.getMessage());
            System.exit(-1);
        }
        return rst;
    }
    public static void main( String[] args )
    {
        
        AppConfig config = AppConfig.getInstance();
        buildOptions();
        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String[] argValues = parseArgs(args);
        config.setIp_addr(argValues[0]);
        config.setPort_num(Integer.valueOf(argValues[1]));
        
        server = new Server(config.getIp_addr(), config.getPort_num(), "/websockets", null, StringEndPoint.class);
        
        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
