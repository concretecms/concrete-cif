import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class StringOutputStream extends PrintStream {
	public StringOutputStream() {
		super(new ByteArrayOutputStream(), true);
	}

	public void clear() {
		this.flush();
		this.out = new ByteArrayOutputStream();
	}

	public String getString() {
		return this.out.toString();
	}
}