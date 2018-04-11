package translation;

import pipe.dataLayer.DataLayer;

import java.io.IOException;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public interface Translator {
    void translate(final DataLayer pModel) throws IOException;
}
