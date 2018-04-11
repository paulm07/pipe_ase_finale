package formula;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class GlobalClock {

  private ClockState mState;
  private static GlobalClock sInstance = new GlobalClock();

  private GlobalClock() {
    mState = new ClockState(0, 1);
  }

  public static GlobalClock getInstance() {
    return sInstance;
  }

  public ClockState getClockState() {
    return new ClockState(mState.prev, mState.next);
  }

  public void increment() {
    mState.prev = mState.next;
    mState.next++;
  }

  public void reset() {
    mState.next = 1;
    mState.prev = 0;
  }

  public static class ClockState {
    private int prev = 0;
    public int next = 1;

    public ClockState (final int pPrev, final int pNext) {
      prev = pPrev;
      next = pNext;
    }
  }
}
