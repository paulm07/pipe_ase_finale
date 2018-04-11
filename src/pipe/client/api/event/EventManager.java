package pipe.client.api.event;

import java.util.HashMap;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class EventManager {

  private static EventManager sInstance = new EventManager();
  private final HashMap<String, EventEmitter> mEventQueues = new HashMap<>();

  private EventManager() {
    mEventQueues.put("animation.history", new AnimationEventHandler());
  }

  public static EventManager getManager() {
    return sInstance;
  }
}
