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

		System.out.println();
		System.out.println("===============================================");
		System.out.println("(*) ESITO TEST ");
		System.out.println("  > Test passati: " + giusti + "/" + tests);
		System.out.println("  > Punteggio   : " + punteggio + "/100");
		System.out.println("-----------------------------------------------");
	}

	private static void compareFiles(int t) throws Exception {
		boolean tuttogiusto = true;
		System.out.println();
		System.out.println("===============================================");
		System.out.println("(*) TEST " + t);

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
				System.out.println("  > Valore calcolato:" + str2);
				System.out.println("  > Valore atteso   :" + str1);
				System.out.println("  > Linea           :" + count1);
				System.out.println("  > ############## [ERR] ##############");
				System.out.println("-----------------------------------------------");
			} else {
				System.out.println("  > Valore calcolato:" + str2);
				System.out.println("  > Valore atteso   :" + str1);
				System.out.println("  > Linea           :" + count1);
				System.out.println("  > [OK]");
				System.out.println("-----------------------------------------------");
			}
			str1 = sc1.readLine();
			str2 = sc2.readLine();
		}
		
		while (str2 == null && str1 != null) {
			count1++;
			tuttogiusto = false;
			System.out.println("  > Valore calcolato:" + str1);
			System.out.println("  > Valore atteso   :EOF");
			System.out.println("  > Linea           :" + count1);
			System.out.println("  > ############## [ERR] ##############");
			System.out.println("-----------------------------------------------");
			str1 = sc1.readLine();
		}
		
		while (str1 == null && str2 != null) {
			count2++;
			tuttogiusto = false;
			System.out.println("  > Valore calcolato:EOF");
			System.out.println("  > Valore atteso   :" + str2);
			System.out.println("  > Linea           :" + count2);
			System.out.println("  > ############## [ERR] ##############");
			System.out.println("-----------------------------------------------");
			str2 = sc2.readLine();
		}

		sc1.close();
		sc2.close();

		System.out.println();

		if (tuttogiusto)
			giusti++;
	}

	private static void doTest(int t, Class<?> c) throws Exception {
		tests++;
		PrintStream realSystemOut = System.out;
		InputStream realSystemIn = System.in;

    	System.setOut(new PrintStream(new FileOutputStream("res"+t+".out")));
    	System.setIn(new FileInputStream(t+".in"));

    	executeProgram(c);

    	System.setIn(realSystemIn);
    	System.setOut(realSystemOut);

    	compareFiles(t);
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
			System.out.println("Uso: java ProvaHomework <NomeClasseDaTestare>");
			System.out.println("<NomeClasseDaTestare> è il nome della classe che vuoi testare (senza .java or .class)");
			System.exit(1);
		}

		String className = args[0];

		if (className.endsWith(".java")) {
			System.out.println("Inerire il nome della classe SENZA .java");
			System.exit(1);
		}

		if (className.endsWith(".class")) {
			System.out.println("Inerire il nome della classe SENZA .class");
			System.exit(1);
		}

		Class<?> myclass = null;
		try {
			myclass = Class.forName(className);
			int modifier = myclass.getModifiers();

			if (!className.equals("Main") && modifier == Modifier.PUBLIC) {
				System.out.println("La classe NOT DEVE essere dichiarata PUBLIC");
				System.exit(1);	
			}

			int t = 1;
			while(new File(t+".in").exists()) {
				doTest(t, myclass);
				t++;
			}

			stampaRisultatoTest();

		} catch (ClassNotFoundException cnfe){
			System.out.println("Devi compilare manualmente la classe " + args[0] + " prima di testarla");
		}
	}

	private static void init() {
        System.out.println("@Test iniziato");
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.flush();
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