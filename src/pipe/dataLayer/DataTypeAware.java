package pipe.dataLayer;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public interface DataTypeAware {
  DataType getDataType();
  void setDataType(final DataType pDataType);
}
