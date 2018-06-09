import java.util.concurrent.Semaphore;

public class Semaphores {

	// obstacle: mountain semaphores
	private Semaphore mountainMutex;
	// obstacle: river semaphores
	private Semaphore groupRiver;
	private Semaphore riverMutex;
	private Semaphore groupReadyToCrossRiver;
	private Semaphore judgeIsAvailable;
	private Semaphore groupFinishedCrossing;
	private Semaphore waitForReport;
	private Semaphore goHome;
	private Semaphore finishedRace;
	private Semaphore mutex;
	

	public Semaphores() {
		mountainMutex = new Semaphore(1);
		groupRiver = new Semaphore(0);
		riverMutex = new Semaphore(1);
		groupReadyToCrossRiver = new Semaphore(0);
		judgeIsAvailable = new Semaphore(1);
		groupFinishedCrossing = new Semaphore(0);
		waitForReport = new Semaphore(0);
		goHome = new Semaphore(0);
		finishedRace = new Semaphore(0);
		mutex = new Semaphore(1);
	}

	// obstacle: mountain semaphores
	public Semaphore getMountainMutex() {
		return mountainMutex;
	}

	// obstacle: river semaphores
	public Semaphore getGroupRiver() {
		return groupRiver;
	}

	public Semaphore getRiverMutex() {
		return riverMutex;
	}

	public Semaphore getGroupReadyToCrossRiver() {
		return groupReadyToCrossRiver;
	}

	public Semaphore getJudgeIsAvailable() {
		return judgeIsAvailable;
	}

	public Semaphore getGroupFinishedCrossing() {
		return groupFinishedCrossing;
	}

	public Semaphore getWaitForReport() {
		return waitForReport;
	}

	public Semaphore getGoHome() {
		return goHome;
	}

	public Semaphore getFinishedRace() {
		return finishedRace;
	}

	public Semaphore getMutex() {
		return mutex;
	}
}
