import java.util.Random;
import java.util.concurrent.Semaphore;

public class Racer implements Runnable {

	private Thread thread;
	private TimeKeeper timeKeeper;
	private String magicWord;
	private int riverGroupSize;
	private int racerRiverP;
	private int raceId;
	private Racer friend;
	private Semaphore friendWentHome;
	private Judge judge;
	public final int totalRacers;
	public Semaphores sems;
	public static int currentRacersInRiver = 0;
	public static Random random = new Random();

	public Racer(String threadName, String magicWord, Semaphores sems, int groupSize, int totalRacers, Judge judge, int raceId) {
		this.sems = sems;
		this.raceId = raceId;
		this.friendWentHome = new Semaphore(0);
		this.judge = judge;
		this.totalRacers = totalRacers;
		this.thread = new Thread(this, threadName);
		this.timeKeeper = new TimeKeeper(threadName);
		this.magicWord = magicWord;
		this.riverGroupSize = groupSize;
		thread.start();
	}
	
	public void setFriend(Racer friend) {
		this.friend = friend;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		timeKeeper.setRaceStartTime(System.currentTimeMillis());
		rest();
		runForest();
		rest();
		runMountain();
		rest();
		runRiver();
		getHome();
	}

	public void runForest() {
		timeKeeper.setForestStartTime(System.currentTimeMillis());
		msg("is in forest");
		boolean wordIsInMap;
		msg("will start looking for magic word");
		wordIsInMap = Forest.searchForest(magicWord, this);
		msg("has found the map ( magic word=" + magicWord + "): " + wordIsInMap);
		timeKeeper.setForestEndTime(System.currentTimeMillis());
	}

	public void runMountain() {
		timeKeeper.setMountainStartTime(System.currentTimeMillis());
		msg("has just arrived at mountain");
		try {
			// one racer will get semaphore simulating that a racer is crossing the mountain
			sems.getMountainMutex().acquire();
			crossObstacle("will cross mountain", 500, 500);
			sems.getMountainMutex().release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			msg("has to wait to cross over mountain");
		}
		timeKeeper.setMountainEndTime(System.currentTimeMillis());
	}

	public void runRiver() {
		try {
			timeKeeper.setRiverStartTime(System.currentTimeMillis());
			sems.getRiverMutex().acquire();
			currentRacersInRiver++;
			racerRiverP = currentRacersInRiver;
			if ((currentRacersInRiver % riverGroupSize == 0) || (currentRacersInRiver == totalRacers)) {
				// group is formed, so must notify judge that they are ready to cross.
				sems.getRiverMutex().release();
				msg("Group is formed.");
				sems.getJudgeIsAvailable().acquire(); // the judge has to be available in order to let groups go across river
				msg("Group is ready to cross river.");
				sems.getGroupReadyToCrossRiver().release(); // group is ready to cross river
				for (int i = 0; i < riverGroupSize - 1; i++) {
					sems.getGroupRiver().release();
				}
			} else {
				// racer will wait for group to be formed
				sems.getRiverMutex().release();
				msg("will wait for the group to be formed.");
				sems.getGroupRiver().acquire(); // will make the racer wait until a group is formed.
			}
			crossObstacle("going over river", 500, 500);
			sems.getRiverMutex().acquire();  // mutual exclusion
			if ( isLastRacerInRiver() && totalRacers % riverGroupSize != 0) {
				// this is so that the last user releases semaphores required in order for the judge to continue running
				// It is a problem when there is one group that won't be able to be formed and no more racers are available
				int racersRemaining = riverGroupSize - (totalRacers % riverGroupSize);
				msg(racersRemaining + "");
				for (int i = 0; i < racersRemaining; i++) {
					msg("signal");
					sems.getGroupFinishedCrossing().release(); // lets the judge know that all members in the group finished crossing river
				}
			}
			sems.getRiverMutex().release(); // finish mutual exclusion
			sems.getGroupFinishedCrossing().release(); // lets the judge know that all members in the group finished crossing river
			timeKeeper.setRiverEndTime(System.currentTimeMillis());
			timeKeeper.setRaceEndTime(System.currentTimeMillis());
			msg("finished race");
			sems.getWaitForReport().acquire(); //making racers wait for judge to announce reports
			judge.printReport(timeKeeper);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getHome() { 
		if (isLastRacer()) {
			try {
				sems.getGoHome().acquire(); // getting the message that judge has finished reporting stats
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			try {
				friendWentHome.acquire(); // racer has to wait for friend to finish
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		msg("went home");
		if (!isFirstRacer()) {
			friend.getFriendsSemaphore().release();
		}
		else {
			msg("bye judge");
			sems.getFinishedRace().release();
		}
	}
	
	public Semaphore getFriendsSemaphore() {
		return friendWentHome;
	}
	
	private boolean isFirstRacer() {
		return raceId == 1;
	}
	
	private boolean isLastRacer() {
		return raceId == totalRacers;
	}
	
	private boolean isLastRacerInRiver() {
		return racerRiverP == totalRacers;
	}

	private void rest() {
		sleep("Racer will rest for %d ms.");
	}

	public void crossObstacle(String msg, int randNoMin, int randNoMax) {
		try {
			int crossingTIme = random.nextInt(randNoMax) + randNoMin;
			if (msg != null) {
				msg(String.format(msg, crossingTIme));
			}
			Thread.sleep(crossingTIme);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sleep(String message) {
		try {
			int time = random.nextInt(250) + 4000;
			message = String.format(message, time);
			msg(message);
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private void msg(String message) {
		System.out.println("[" + (System.currentTimeMillis() - Main.time) + "] " + thread.getName() + ": " + message);
	}
}
