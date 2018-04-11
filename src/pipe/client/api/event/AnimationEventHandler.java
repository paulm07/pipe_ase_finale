package pipe.client.api.event;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class AnimationEventHandler extends EventEmitter {

  public AnimationEventHandler() {
    super("");
  }
  /**
   * Constructs a <code>PropertyChangeSupport</code> object.
   *
   * @param sourceBean The bean to be given as the source for any events.
   */
  public AnimationEventHandler(Object sourceBean) {
    super(sourceBean);
  }

}
