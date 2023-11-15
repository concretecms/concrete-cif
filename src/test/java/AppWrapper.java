import java.io.PrintStream;

import org.concretecms_community.concrete_cif.App;

public class AppWrapper extends App {

	public AppWrapper() {
		super(new PrintStream(PrintStream.nullOutputStream()), new PrintStream(PrintStream.nullOutputStream()));
	}

	public int execute(String[] args) {
		return super.execute(args);
	}
}
