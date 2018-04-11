package pipe.client.api.event;

import java.beans.PropertyChangeSupport;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public abstract class EventEmitter extends PropertyChangeSupport {
  /**
   * Constructs a <code>PropertyChangeSupport</code> object.
   *
   * @param sourceBean The bean to be given as the source for any events.
   */
  public EventEmitter(Object sourceBean) {
    super(sourceBean);
  }

  public void emitEvent(final String pEventKey, final Object pObject) {
  }
}
