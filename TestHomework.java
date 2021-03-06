import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class TestHomework {
	private static int tests = 0;
	private static int giusti = 0;
	private static int punteggio = 0;
	
	
	public static void normalizza() {// normalizza il punteggio a 100
		punteggio = (int)Math.round((double)(giusti * 100.0)/ (double)tests);
	}
	
	public static void stampaRisultatoTest() { //stampa il risultato
		normalizza();

		System.err.println();
		System.err.println("===============================================");
		System.err.println("(*) ESITO TEST ");
		System.err.println("  > Test passati: " + giusti + "/" + tests);
		System.err.println("  > Punteggio   : " + punteggio + "%");
		System.err.println("-----------------------------------------------");
	}

	private static void compareFiles(int t, boolean verbose) throws Exception {
		boolean tuttogiusto = true;
		System.err.println();
		System.err.println("===============================================");
		System.err.println("(*) TEST " + t);

		File f1 = new File("sol"+t+".in");
		File f2 = new File("res"+t+".out");

  		FileInputStream is1 = new FileInputStream(f1);
  		FileInputStream is2 = new FileInputStream(f2);

		BufferedReader sc1 = new BufferedReader(new InputStreamReader(is1));
		BufferedReader sc2 = new BufferedReader(new InputStreamReader(is2));

		int count1 = 0;
		int count2 = 0;

		String str1 = sc1.readLine();
		String str2 = sc2.readLine();

		while (str1 != null && str2 != null) {
			
			count1++;
			count2++;

			if (!str1.equals(str2)) {
				tuttogiusto = false;
				if (verbose) {
					System.err.println("  > Valore calcolato:" + str2);
					System.err.println("  > Valore atteso   :" + str1);
					System.err.println("  > Linea           :" + count1);
					System.err.println("  > ############## [ERR] ##############");
					System.err.println("-----------------------------------------------");
				}
	
			} else {
				if (verbose) {
					System.err.println("  > Valore calcolato:" + str2);
					System.err.println("  > Valore atteso   :" + str1);
					System.err.println("  > Linea           :" + count1);
					System.err.println("  > [OK]");
					System.err.println("-----------------------------------------------");
				}
				
			}
			str1 = sc1.readLine();
			str2 = sc2.readLine();
		}
		
		while (str2 == null && str1 != null) {
			count1++;
			tuttogiusto = false;
			if (verbose) {
				System.err.println("  > Valore calcolato:" + str1);
				System.err.println("  > Valore atteso   :EOF");
				System.err.println("  > Linea           :" + count1);
				System.err.println("  > ############## [ERR] ##############");
				System.err.println("-----------------------------------------------");
			}
					str1 = sc1.readLine();
		}
		
		while (str1 == null && str2 != null) {
			count2++;
			tuttogiusto = false;

			if (verbose) {
				System.err.println("  > Valore calcolato:EOF");
				System.err.println("  > Valore atteso   :" + str2);
				System.err.println("  > Linea           :" + count2);
				System.err.println("  > ############## [ERR] ##############");
				System.err.println("-----------------------------------------------");
			}
	
			str2 = sc2.readLine();
		}

		sc1.close();
		sc2.close();

		if (tuttogiusto)
			giusti++;

		if (!verbose)
			System.err.println((tuttogiusto?"  > [OK]":"  > ############## [ERR] ##############"));
			
	}

	private static void doTest(int t, Class<?> c, boolean verbose) throws Exception {
		tests++;
		PrintStream realSystemOut = System.out;
		InputStream realSystemIn = System.in;

    	System.setOut(new PrintStream(new FileOutputStream("res"+t+".out")));
    	System.setIn(new FileInputStream(t+".in"));

    	executeProgram(c);

    	System.setIn(realSystemIn);
    	System.setOut(realSystemOut);

    	compareFiles(t, verbose);
	}

	public static void executeProgram(Class<?> c) throws Exception {
		Class[] params = {String[].class};
		Method mymain = c.getDeclaredMethod("main", params);
		Object[] args = new Object[1];
		args[0] = new String[] {};
		mymain.invoke(null, args);
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("Uso: java TestHomework <NomeClasse> [opzioni]");
			System.err.println("<NomeClasse> il nome della classe che vuoi testare (senza .java or .class)");
			System.err.println("[opzioni]:");
			System.err.println("            -v          modo dettagliato");
			System.err.println("            -s=[num]    esegue solo il test numero [num]");
			System.exit(1);
		}

		String className = args[0];

		if (className.endsWith(".java")) {
			System.err.println("Inerire il nome della classe SENZA .java");
			System.exit(1);
		}

		if (className.endsWith(".class")) {
			System.err.println("Inerire il nome della classe SENZA .class");
			System.exit(1);
		}

		Class<?> myclass = null;
		try {
			myclass = Class.forName(className);
			int modifier = myclass.getModifiers();

			if (!className.equals("Main") && modifier == Modifier.PUBLIC) {
				System.err.println("La classe NOT DEVE essere dichiarata PUBLIC");
				System.exit(1);	
			}

			boolean verbose = false;
			boolean single = false;
			if (args.length == 2) {
				if (args[1].equals("-v"))
					verbose = true;
				else 
					single = args[1].substring(0,3).equals("-s=");
			}
			
		if (!single) {
			int t = 1;
			while(new File(t+".in").exists()) {
				doTest(t, myclass, verbose);
				t++;
			}

		} else {
			int t = Integer.parseInt(args[1].substring(3,args[1].length()));
			doTest(t, myclass, true);
		}

			stampaRisultatoTest();

		} catch (ClassNotFoundException cnfe){
			System.err.println("Devi compilare manualmente la classe " + args[0] + " prima di testarla");
		}
	}

	private static void init() {
        System.err.println("@Test iniziato");
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.err.flush();
                System.err.flush();
                System.err.println("===============================================");
                e.printStackTrace();
                System.err.println("===============================================");
                System.err.println(
                        "E' stata generata una eccezione inattesa.\nIl test del programma e' stato interrotto.");
                System.err.println("===============================================");
                System.err.flush();
            }
        });
    }

	static {
		init();
	}
}
