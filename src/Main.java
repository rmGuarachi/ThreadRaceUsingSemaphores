public class Main {

	private Racer[] racers;
	private Judge judge;
	private int totalRacers;
	private Forest forest;
	private Semaphores sems;
	private final int MAX_WORDS_IN_FOREST = 350;
	public static long time = System.currentTimeMillis();
	public static final char[] SET_OF_WORDS = { 'a', 'b', 'c', 'd' };

	public Main(int numRacers, int numLines) {
		totalRacers = numRacers;
		sems = new Semaphores();
		forest = new Forest(MAX_WORDS_IN_FOREST, SET_OF_WORDS);
		System.out.println("Race starting");
		initJudge(numLines);
		initRacers(numLines);
		setRacersFriends();

	}

	private void initRacers(int numLines) {
		racers = new Racer[totalRacers];
		String racer_name = "Racer #";
		for (int i = 0; i < totalRacers; i++) {
			racers[i] = new Racer(racer_name + " " + (i + 1), forest.getMap(i), sems, numLines, this.totalRacers, this.judge, i+1);
		}
	}
	
	private void setRacersFriends() {
		// setting friendship between racers
		for (int i = totalRacers-1; i > 0; i--) {
			racers[i].setFriend(racers[i-1]);
		}
	}
	
	private void initJudge(int numLines) {
		judge = new Judge("judge # 1", sems, numLines, totalRacers);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int numberOfRacers = 11;
		int numLines = 3;
		try {
			// allows users to enter any number of parameters in Terminal, however it must
			// be an integer
			if (args.length >= 2) {
				numberOfRacers = Integer.parseInt(args[0]);
				numLines = Integer.parseInt(args[1]);
			}
		} catch (NumberFormatException nfe) {
			System.out.println("NumberFormatException: " + nfe.getMessage());
		}

		Main race = new Main(numberOfRacers, numLines);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("\n\nRace is officially Over");
	}

}
