
public class Judge implements Runnable {

	private Thread thread;
	private int groupSize;
	private int totalRacers;
	private Semaphores sems;

	public Judge(String name, Semaphores sems, int groupSize, int totalRacers) {
		this.groupSize = groupSize;
		this.totalRacers = totalRacers;
		this.sems = sems;
		this.thread = new Thread(this, name);
		thread.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		handleGroupsInRiver();

	}

	private void handleGroupsInRiver() {
		int groupCounter = 1;
		
		// code for river crossing
		while (groupCounter <= getNumGroupTotal()) {
			try {
				msg("signals members of group # " + groupCounter + " to start crossing river when ready");
				sems.getGroupReadyToCrossRiver().acquire();
				for (int i = 1; i <= groupSize; i++) {
					sems.getGroupFinishedCrossing().acquire();
				}
				msg("all members of group # " + groupCounter + " have crossed.");
				sems.getJudgeIsAvailable().release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			groupCounter++;
		}
		
		// code for waiting for every racer to finish race and print report
		while (sems.getWaitForReport().getQueueLength() != totalRacers) {
			msg(sems.getWaitForReport().getQueueLength() + "");
		}
		for (int i = 0; i < totalRacers; i++) {
			sems.getWaitForReport().release();
			sleep();
		}
		
		// code for goHome
		sems.getGoHome().release(); // tells last racer to go home
		try {
			sems.getFinishedRace().acquire(); // judge sees that everyone left
			msg("everyone went home");
			msg("I'm going home.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void printReport(TimeKeeper timeKeeper) {
		printRaceResults(timeKeeper);
		printObstacleResults(timeKeeper);
	}

	private void printRaceResults(TimeKeeper timeKeeper) {
		msg(timeKeeper.getRacerName() + "'s total race time: " + timeKeeper.getTotalRaceTime() + "ms");
	}

	public void printObstacleResults(TimeKeeper timeKeeper) {
		msg(timeKeeper.getRacerName() + "'s obstacle record breakdown" + "\n" + "Forest obstacle: "
				+ timeKeeper.getTotalForestTime() + "ms" + "\n" + "Mountain obstacle: " + timeKeeper.getTotalMountainTime()
				+ "ms" + "\n" + "River obstacle: " + timeKeeper.getTotalRiverTime() + "ms"
				+ "\n" + "Total time excluding rest: " + timeKeeper.getTotalExcludingRest() + "ms");
	}

	public void sleep() {
		try {
			int time = Racer.random.nextInt(10) + 10;
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private int getNumGroupTotal() {
		int totalGroups = totalRacers / groupSize;
		if (totalRacers % groupSize != 0) {
			totalGroups++;
		}
		return totalGroups;
	}

	private void msg(String message) {
		System.out.println("[" + (System.currentTimeMillis() - Main.time) + "] " + thread.getName() + ": " + message);
	}
}
