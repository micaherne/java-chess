package uk.co.micaherne.javachess;

import uk.co.micaherne.javachess.io.UCI;

public class Main implements Runnable {

	UCI io;
	
	public Main() {
		super();
		io = new UCI();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main e = new Main();
		//new Thread("engine").start();
		e.run();
	}

	public void run() {
		io.startInput();
	}

}
