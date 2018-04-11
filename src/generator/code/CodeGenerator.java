package generator.code;

import pipe.dataLayer.DataLayer;

import java.io.IOException;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public interface CodeGenerator {
  void generate(final DataLayer pDataLayer) throws IOException;
}
