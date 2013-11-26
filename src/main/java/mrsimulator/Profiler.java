package mrsimulator;

import java.io.*;

class Profiler {

	private String directory;
	private PrintWriter pw = null;

	public Profiler(String dir, String name) {
		directory = dir;
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(name));
		} catch (IOException io) {
			System.out.println("Exception thrown  :" + io);
		}
		
		pw = new PrintWriter(fw, true);
	}

	public void println(String str) {
		pw.println(str);
	}

	public void nextLine() {
		pw.println("");
	}

	public void close() {
		pw.close();
	}

	public void print2ln(String str1, String str2) {
		pw.println(str1 + " " + str2);
	}

	public void print2ln(int str1, int str2) {
		pw.println(str1 + " " + str2);
	}

	public void print(JobInfo job) {
		pw.println(job.jobToString());
		for (int i = 0; i < job.mapNumber; i++)
			pw.println(job.mapTaskToString(i));
		for (int i = 0; i < job.reduceNumber; i++)
			pw.println(job.reduceTaskToString(i));
		this.nextLine();
	}

}