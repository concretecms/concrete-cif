import org.concretecms.concrete_cif.App;

public class AppWrapper extends App {

	public AppWrapper() {
		super(new StringOutputStream(), new StringOutputStream());
	}

	public int execute(String[] args) {
		((StringOutputStream) this.standardOutput).clear();
		((StringOutputStream) this.standardError).clear();
		return super.execute(args);
	}

	public String getStandardOutput() {
		this.standardOutput.flush();
		return ((StringOutputStream) this.standardOutput).getString();
	}

	public String getStandardError() {
		this.standardOutput.flush();
		return ((StringOutputStream) this.standardError).getString();
	}
}
